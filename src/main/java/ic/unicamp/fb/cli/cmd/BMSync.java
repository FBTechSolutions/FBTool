package ic.unicamp.fb.cli.cmd;

import static ic.unicamp.fb.cli.cmd.BMConfigure.FB_GENERIC_FEATURE;
import static ic.unicamp.fb.graph.neo4j.utils.FragmentUtil.retrieveOrCreateGenericFragment;

import ic.unicamp.fb.block.GitVCSManager;
import ic.unicamp.fb.block.IVCRepository;
import ic.unicamp.fb.block.IVCSAPI;
import ic.unicamp.fb.graph.neo4j.schema.Block;
import ic.unicamp.fb.graph.neo4j.schema.Container;
import ic.unicamp.fb.graph.neo4j.schema.Feature;
import ic.unicamp.fb.graph.neo4j.schema.Fragment;
import ic.unicamp.fb.graph.neo4j.schema.enums.BlockState;
import ic.unicamp.fb.graph.neo4j.schema.enums.ContainerType;
import ic.unicamp.fb.graph.neo4j.schema.enums.DataState;
import ic.unicamp.fb.graph.neo4j.schema.relations.BlockToBlock;
import ic.unicamp.fb.graph.neo4j.schema.relations.BlockToFragment;
import ic.unicamp.fb.graph.neo4j.schema.relations.ContainerToBlock;
import ic.unicamp.fb.graph.neo4j.schema.relations.ContainerToContainer;
import ic.unicamp.fb.graph.neo4j.services.BlockService;
import ic.unicamp.fb.graph.neo4j.services.BlockServiceImpl;
import ic.unicamp.fb.graph.neo4j.services.ContainerService;
import ic.unicamp.fb.graph.neo4j.services.ContainerServiceImpl;
import ic.unicamp.fb.graph.neo4j.services.FeatureService;
import ic.unicamp.fb.graph.neo4j.services.FeatureServiceImpl;
import ic.unicamp.fb.graph.neo4j.services.FragmentService;
import ic.unicamp.fb.graph.neo4j.services.FragmentServiceImpl;
import ic.unicamp.fb.scanner.BlockScanner;
import ic.unicamp.fb.scanner.BlockNumberSequencer;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.treewalk.TreeWalk;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

@Command(
        name = BMSync.CMD_NAME,
        description = "This command will inform whether exits modifications in the blocks using as reference the DB")
public class BMSync implements Runnable {

    @Parameters(index = "0", description = "repository Name", defaultValue = "")
    String repositoryName;

    public static final String CMD_NAME = "sync";
    IVCSAPI temporalVC = GitVCSManager.createTemporalGitBlockInstance();

    @Override
    public void run() {
        if (StringUtils.isEmpty(repositoryName)) {
            System.out.println("You need to specify at repository name");
            return;
        }
        BlockScanner blockScanner = new BlockScanner();
        IVCRepository gitRepository = GitVCSManager.createGitRepositoryInstance();
        Path path = gitRepository.getOutDirectory(repositoryName);

        Path repositoryGit = Paths.get(String.valueOf(path), ".git");
        File currentDirectoryGit = new File(String.valueOf(repositoryGit));
        if (currentDirectoryGit.exists()) {
            FileRepositoryBuilder repositoryBuilder = new FileRepositoryBuilder();
            repositoryBuilder.setMustExist(true);
            repositoryBuilder.setGitDir(currentDirectoryGit);

            try {
                Repository repository = repositoryBuilder.build();
                Ref head = repository.findRef("HEAD");
                RevWalk walk = new RevWalk(repository);
                RevCommit commit = walk.parseCommit(head.getObjectId());
                TreeWalk treeWalk = new TreeWalk(repository);
                treeWalk.addTree(commit.getTree());
                treeWalk.setRecursive(false);
                BlockService blockService = new BlockServiceImpl();
                ContainerService containerService = new ContainerServiceImpl();
                Container mainContainer = containerService.getContainerByType(ContainerType.MAIN);
                while (treeWalk.next()) {
                    if (treeWalk.isSubtree()) {
                        String pathFileString = treeWalk.getPathString();
                        Container container = containerService.getContainerByID(pathFileString);
                        if (container == null) {
                            //case: If the container does not exist, we create it
                            //create container
                            Container newContainer = new Container();
                            newContainer.setContainerId(pathFileString);
                            newContainer.setContainerType(ContainerType.FOLDER);
                            //find a registered parent
                            Container parentContainer = retrieveParentContainer(path, pathFileString,
                                    containerService, mainContainer);
                            //relation
                            ContainerToContainer aRelation = new ContainerToContainer();
                            aRelation.setStartContainer(parentContainer);
                            aRelation.setEndContainer(newContainer);
                            List<ContainerToContainer> relations = parentContainer.getParentFrom();
                            if (relations == null) {
                                relations = new LinkedList<>();
                            }
                            relations.add(aRelation);
                            parentContainer.setParentFrom(relations);
                            containerService.createOrUpdate(parentContainer);
                        }
                        treeWalk.enterSubtree();
                    } else {

                        String pathFileString = treeWalk.getPathString();
                        Path pathFileRepository = Paths.get(String.valueOf(path), pathFileString);
                        Container container = containerService.getContainerByID(pathFileString);
                        if (container == null) {
                            // case: new File
                            Container newContainerFile = new Container();
                            newContainerFile.setContainerId(pathFileString);
                            newContainerFile.setContainerType(ContainerType.FILE);
                            // find a registed parent
                            Container parentContainer = retrieveParentContainer(path, pathFileString,
                                    containerService, mainContainer);
                            // create relations
                            ContainerToContainer relation = new ContainerToContainer();
                            relation.setStartContainer(parentContainer);
                            relation.setEndContainer(newContainerFile);
                            List<ContainerToContainer> relations = parentContainer.getParentFrom();
                            if (relations == null) {
                                relations = new LinkedList<>();
                            }
                            relations.add(relation);
                            parentContainer.setParentFrom(relations);
                            //update container
                            containerService.createOrUpdate(parentContainer);

                            //creating blocks
                            Path pathFolderRepository = Paths.get(String.valueOf(path), pathFileString);
                            Map<String, String> scannedBlocks = blockScanner.createInitialBlocks(
                                    pathFolderRepository); //id and data
                            IVCSAPI temporalGitBlock = GitVCSManager.createTemporalGitBlockInstance();

                            //memory to first block and previous block
                            Block firstBlock = null;
                            Block previousBlock = null;

                            FragmentService fragmentService = new FragmentServiceImpl();
                            Fragment defaultFragment = retrieveOrCreateGenericFragment(fragmentService);

                            for (String key : scannedBlocks.keySet()) {
                                String data = scannedBlocks.get(key);
                                String shaData = DigestUtils.sha256Hex(data);
                                //path
                                temporalGitBlock.upsertContent(key, data);
                                //db
                                Block block = new Block();
                                block.setBlockId(key);
                                block.setBlockSha(shaData);
                                System.out.println(block.getBlockId());
                                block.setBlockState(BlockState.TO_INSERT);
                                block.setVcBlockState(DataState.TEMPORAL);
                                // tag block
                                BlockToFragment blockToFeature = new BlockToFragment();
                                blockToFeature.setStartBlock(block);
                                blockToFeature.setEndFragment(defaultFragment);
                                block.setAssociatedTo(blockToFeature);

                                if (previousBlock == null) {
                                    firstBlock = block;
                                    previousBlock = block;
                                } else {
                                    BlockToBlock relation2 = new BlockToBlock();
                                    relation2.setStartBlock(previousBlock);
                                    relation2.setEndBlock(block);
                                    previousBlock.setGoNextBlock(relation2);

                                    blockService.createOrUpdate(previousBlock);
                                    previousBlock = block;
                                }

                            }
                            // container
                            ContainerToBlock relation3 = new ContainerToBlock();
                            relation3.setStartContainer(newContainerFile);
                            relation3.setEndBlock(firstBlock);
                            newContainerFile.setBlock(relation3);
                            containerService.createOrUpdate(newContainerFile);
                        } else {
                            // case updated file.
                            Block beginPivot = blockService.getFirstBlockByFile(pathFileString);
                            String beginPivotId = beginPivot.getBlockId();
                            Block endPivot;
                            String endPivotId = null;
                            if (beginPivot.getGoNextBlock() != null) {
                                BlockToBlock blockToBlock = beginPivot.getGoNextBlock();
                                endPivot = blockToBlock.getEndBlock();
                                endPivotId = endPivot.getBlockId();
                            }
                            Map<String, String> updatedBlocks = blockScanner.retrieveAllBlocks(
                                    pathFileRepository); //by file
                            List<String> updatedBlocksReverse = new ArrayList<>(updatedBlocks.keySet());

                            LinkedHashMap<String, String> temporalNewBlocks = new LinkedHashMap<>(); //news
                            for (String key : updatedBlocksReverse) {
                                if (key.charAt(0) == 'N') {
                                    temporalNewBlocks.put(key, updatedBlocks.get(key));
                                } else {
                                    if (!temporalNewBlocks.isEmpty()) {
                                        processNewBlocks(blockService, beginPivotId, endPivotId, temporalNewBlocks);
                                    } else {
                                        Block updatedBlock = blockService.getBlockByID(key);
                                        updatedBlock.setBlockState(BlockState.TO_UPDATE);
                                        updatedBlock.setVcBlockState(DataState.TEMPORAL);
                                        beginPivotId = updatedBlock.getBlockId();
                                        BlockToBlock relationUpdated = updatedBlock.getGoNextBlock();
                                        if (relationUpdated != null) {
                                            endPivotId = relationUpdated.getEndBlock().getBlockId();
                                        } else {
                                            endPivotId = null;
                                        }
                                        String content = updatedBlocks.get(key);
                                        String cleanContent = blockScanner.cleanTagMarks(content);
                                        //treat the size
                                        temporalVC.upsertContent(key, cleanContent);
                                        blockService.createOrUpdate(updatedBlock);
                                    }
                                }
                            }
                            if (!temporalNewBlocks.isEmpty()) {
                                processNewBlocks(blockService, beginPivotId, endPivotId, temporalNewBlocks);
                            }
                        }
                    }
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        //from first commit to last commit differences
        //read files
        //read blocks tags
        //compare blocks with db
        //show review of blocks
    }

    private static Feature getDefaultFeature() {
        FeatureService featureService = new FeatureServiceImpl();
        Feature defaultFeature = featureService.getFeatureByID(FB_GENERIC_FEATURE);
        if (defaultFeature == null) {
            defaultFeature = new Feature();
            defaultFeature.setFeatureId(FB_GENERIC_FEATURE);
            defaultFeature.setFeatureLabel(FB_GENERIC_FEATURE);
        }
        return defaultFeature;
    }

    private Container retrieveParentContainer(Path path, String pathFileString,
                                              ContainerService containerService,
                                              Container mainContainer) {
        boolean back = true;
        Path pathFolderRepository = Paths.get(String.valueOf(path), pathFileString);
        Path parent = pathFolderRepository.getParent();
        Path parentCleaned = path.relativize(parent);
        while (back) {
            String parentCleanedString = String.valueOf(parentCleaned);
            Container container = containerService.getContainerByID(parentCleanedString);
            if (container != null) {
                return container;
            }
            parent = parent.getParent();
            parentCleaned = path.relativize(parent);
            if (parentCleanedString.equals("")) {
                back = false;
            }
        }
        return mainContainer;
    }

    private Container backStack(TreeWalk treeWalk, Stack<Container> stack,
                                Container parentPivot) {
        boolean back = true;
        while (back) {
            File exists =
                    new File(parentPivot.getContainerId(), treeWalk.getNameString());
            if (exists.exists()) {
                back = false;
            } else {
                stack.pop();
                parentPivot = stack.peek();
                if (parentPivot.getContainerType() == ContainerType.MAIN) {
                    back = false;
                }
            }
        }
        return parentPivot;
    }

    private String cleanTags(String content) {
        return null;
    }

    private void processNewBlocks(BlockService blockService, String beginPivotId, String endPivotId,
                                  LinkedHashMap<String, String> temporalNewBlocks) {
        List<String> reverseNewBlocks = new ArrayList<>(temporalNewBlocks.keySet());
        Collections.reverse(reverseNewBlocks);
        for (String key : reverseNewBlocks) {
            //create a new block
            Block newBlock = new Block();
            String newBlockId = BlockNumberSequencer.getNextStringCode();
            newBlock.setBlockState(BlockState.TO_INSERT);
            newBlock.setVcBlockState(DataState.TEMPORAL);
            newBlock.setBlockId(newBlockId);

            //adding default fragment
            FragmentService fragmentService = new FragmentServiceImpl();
            Fragment fragment = fragmentService.getFragmentByID(BMConfigure.FB_GENERIC_FRAGMENT);
            BlockToFragment blockToFeature = new BlockToFragment();
            blockToFeature.setStartBlock(newBlock);
            blockToFeature.setEndFragment(fragment);
            newBlock.setAssociatedTo(blockToFeature);

            //create relation
            if (endPivotId != null) {
                BlockToBlock relation = new BlockToBlock();
                relation.setEndBlock(blockService.getBlockByID(endPivotId));
                relation.setStartBlock(newBlock);
                //update relation
                newBlock.setGoNextBlock(relation);
            }
            //update db
            blockService.createOrUpdate(newBlock);
            String content = temporalNewBlocks.get(key);
            // treat the size
            //update VC
            temporalVC.upsertContent(newBlockId, content);
            //update pivots
            endPivotId = newBlockId;
        }
        temporalNewBlocks.clear();
        Block pivotBlock = blockService.getBlockByID(beginPivotId);
        BlockToBlock relationPivot = new BlockToBlock();
        relationPivot.setStartBlock(pivotBlock);
        relationPivot.setEndBlock(blockService.getBlockByID(endPivotId));
        pivotBlock.setGoNextBlock(relationPivot);
        blockService.createOrUpdate(pivotBlock);
    }
}
