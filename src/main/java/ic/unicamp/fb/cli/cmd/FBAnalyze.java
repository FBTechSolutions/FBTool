package ic.unicamp.fb.cli.cmd;

import ic.unicamp.fb.block.GitVCS;
import ic.unicamp.fb.block.GitVCSManager;
import ic.unicamp.fb.block.IVCSAPI;
import ic.unicamp.fb.block.utils.FBDirectoryUtil;
import ic.unicamp.fb.block.utils.DirectoryUtil;
import ic.unicamp.fb.block.utils.GitDirectoryUtil;
import ic.unicamp.fb.block.utils.TempFBDirectoryUtil;
import ic.unicamp.fb.cli.util.logger.SplMgrLogger;
import ic.unicamp.fb.graph.neo4j.schema.Block;
import ic.unicamp.fb.graph.neo4j.schema.Container;
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
import ic.unicamp.fb.graph.neo4j.services.FragmentService;
import ic.unicamp.fb.graph.neo4j.services.FragmentServiceImpl;
import ic.unicamp.fb.scanner.BlockScanner;
import ic.unicamp.fb.scanner.IBlockScanner;
import org.apache.commons.codec.digest.DigestUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.treewalk.TreeWalk;
import picocli.CommandLine.Command;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static ic.unicamp.fb.graph.neo4j.utils.FragmentUtil.retrieveOrCreateGenericFragmentBean;

@Command(
        name = FBAnalyze.CMD_NAME,
        description = "It will Analyze all files from the current directory, preparing the blocks from them.")
public class FBAnalyze implements Runnable {

    public static final String CMD_NAME = "analyze";

    @Override
    public void run() {
        try {
            IVCSAPI temporalVC = GitVCSManager.createTemporalGitBlockInstance();
            Git git = (Git) temporalVC.retrieveDirector();
            git.checkout().setName(GitVCS.FBBranchLabel).call();
            if (!TempFBDirectoryUtil.existsFBTemporalDirectory()) {
                TempFBDirectoryUtil.createFBTemporalDirectory();
                SplMgrLogger.message_ln("- Temporal Directory for blocks was created", false);
            }

            Ref head = git.getRepository().findRef("HEAD");
            RevWalk walk = new RevWalk(git.getRepository());
            RevCommit commit = walk.parseCommit(head.getObjectId());

            TreeWalk treeWalk = new TreeWalk(git.getRepository());
            treeWalk.addTree(commit.getTree());
            treeWalk.setRecursive(false);

            //spl main container
            Container main = new Container();
            main.setContainerId(DirectoryUtil.getDirectoryAsPath().toString());
            main.setContainerType(ContainerType.MAIN);
            changeTreeToSchemaForm(treeWalk, main);
            createContainers(main);
            //blocks
            createBlocksByFile(main);
            showTemporalData();


        } catch (IOException | GitAPIException e) {
            throw new RuntimeException(e);
        }
    }

    private void showTemporalData() {
        System.out.println("Block List:");
    }

    // algorithm
    private void createBlocksByFile(Container container) {

        if (container.getContainerType() == ContainerType.FILE) {

            IBlockScanner blockScanner = new BlockScanner();
            BlockService blockService = new BlockServiceImpl();
            ContainerService containerService = new ContainerServiceImpl();
            Path path = Paths.get(container.getContainerId());
            Map<String, String> scannedBlocks = blockScanner.createInitialBlocks(path); //id and data
            IVCSAPI temporalGitBlock = GitVCSManager.createTemporalGitBlockInstance();

            //memory to first block and previous block
            Block firstBlock = null;
            Block previousBlock = null;

            FragmentService fragmentService = new FragmentServiceImpl();
            Fragment defaultFragment = retrieveOrCreateGenericFragmentBean(fragmentService);

            for (String key : scannedBlocks.keySet()) {
                System.out.println("key");
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
                    BlockToBlock relation = new BlockToBlock();
                    relation.setStartBlock(previousBlock);
                    relation.setEndBlock(block);
                    previousBlock.setGoNextBlock(relation);

                    blockService.createOrUpdate(previousBlock);
                    previousBlock = block;
                }

            }
            // container
            ContainerToBlock relation = new ContainerToBlock();
            relation.setStartContainer(container);
            relation.setEndBlock(firstBlock);
            container.setBlock(relation);
            containerService.createOrUpdate(container);
        } else {
            for (ContainerToContainer Container : container.getParentFrom()) {
                createBlocksByFile(Container.getEndContainer());
            }
        }

    }


    private void createContainers(Container container) {
        ContainerService containerService = new ContainerServiceImpl();
        containerService.createOrUpdate(container, 20);
    }

    private void changeTreeToSchemaForm(TreeWalk treeWalk, Container main) throws IOException {
        Stack<Container> stack = new Stack<>();
        stack.push(main);
        Container parentPivot = main;
        while (treeWalk.next()) {
            String pathString = treeWalk.getPathString();
            if (treeWalk.isSubtree()) {
                //backing
                parentPivot = backStack(treeWalk, stack, parentPivot);
                //folder
                Container container = new Container();
                container.setContainerId(treeWalk.getPathString());
                container.setContainerType(ContainerType.FOLDER);

                ContainerToContainer relation = new ContainerToContainer();
                relation.setStartContainer(parentPivot);
                relation.setEndContainer(container);
                List<ContainerToContainer> relations = parentPivot.getParentFrom();
                if (relations == null) {
                    relations = new LinkedList<>();
                }
                relations.add(relation);
                parentPivot.setParentFrom(relations);
                parentPivot = container;
                stack.push(container);
                treeWalk.enterSubtree();

            } else {
                //backing
                parentPivot = backStack(treeWalk, stack, parentPivot);
                //file
                Container container = new Container();
                container.setContainerId(treeWalk.getPathString());
                container.setContainerType(ContainerType.FILE);

                ContainerToContainer relation = new ContainerToContainer();
                relation.setStartContainer(parentPivot);
                relation.setEndContainer(container);
                List<ContainerToContainer> relations = parentPivot.getParentFrom();
                if (relations == null) {
                    relations = new LinkedList<>();
                }
                relations.add(relation);
                parentPivot.setParentFrom(relations);
            }
        }
    }

    private static Container backStack(TreeWalk treeWalk, Stack<Container> stack,
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

    public static List<Path> listFiles(Path path) throws IOException {
        List<Path> result;
        try (Stream<Path> walk = Files.walk(path)) {
            result = walk.filter(Files::isRegularFile)
                    .filter(aPath -> !GitDirectoryUtil.existNameInPath(aPath))
                    .filter(aPath -> !FBDirectoryUtil.existNameInPath(aPath))
                    .collect(Collectors.toList());
        }
        return result;

    }
}
