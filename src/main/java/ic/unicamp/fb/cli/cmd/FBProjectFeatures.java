package ic.unicamp.fb.cli.cmd;

import ic.unicamp.fb.block.GitVCSManager;
import ic.unicamp.fb.block.IVCRepository;
import ic.unicamp.fb.block.IVCSAPI;
import ic.unicamp.fb.graph.neo4j.schema.Block;
import ic.unicamp.fb.graph.neo4j.schema.Container;
import ic.unicamp.fb.graph.neo4j.schema.Feature;
import ic.unicamp.fb.graph.neo4j.schema.Fragment;
import ic.unicamp.fb.graph.neo4j.schema.enums.DataState;
import ic.unicamp.fb.graph.neo4j.schema.relations.ContainerToBlock;
import ic.unicamp.fb.graph.neo4j.services.BlockService;
import ic.unicamp.fb.graph.neo4j.services.BlockServiceImpl;
import ic.unicamp.fb.graph.neo4j.services.FeatureService;
import ic.unicamp.fb.graph.neo4j.services.FeatureServiceImpl;
import ic.unicamp.fb.graph.neo4j.services.FragmentService;
import ic.unicamp.fb.graph.neo4j.services.FragmentServiceImpl;
import ic.unicamp.fb.graph.neo4j.utils.FeatureUtil;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Command(
        name = FBProjectFeatures.CMD_NAME,
        description = "It will project the source code related to a list of features.")
public class FBProjectFeatures implements Runnable {

    private final IVCSAPI gitVC = GitVCSManager.createInstance();
    private final Git git = (Git) gitVC.retrieveDirector();

    public static final String CMD_NAME = "project-features";

    @Option(names = "--clean", defaultValue = "false")
    boolean clean;

    @Parameters(index = "0..*")
    String[] features;

    @Override
    public void run() {
        if (features == null) {
            System.out.println("You need to specify at least one feature Id");
            return;
        }
        LinkedList<String> featureIds = new LinkedList<>();
        FeatureService featureService = new FeatureServiceImpl();
        for (String featureId : features) {
            if (FeatureUtil.exitsFeatureBean(featureService, featureId)) {
                featureIds.add(featureId);
            } else {
                System.out.println("Feature not found: " + featureId);
            }
        }
        projectProject(featureIds, clean);
    }

    private void projectProject(LinkedList<String> featureIds, boolean clean) {
        BlockService blockService = new BlockServiceImpl();
        Map<String, List<Block>> map = new LinkedHashMap<>();
        List<ContainerToBlock> containerToBlockList = blockService.getContainerToBlockRelations();
        for (ContainerToBlock containerToBlock : containerToBlockList) {
            Container container = containerToBlock.getStartContainer();
            Block block = containerToBlock.getEndBlock();
            List<Fragment> fragmentList = retrieveFragmentList(featureIds);
            List<Block> blockRetrieved = retrieveBlockInBatches(block, fragmentList);
            List<Block> blockRetrievedCommitted = retrieveBlockCommitted(blockRetrieved);
            if (!blockRetrievedCommitted.isEmpty()) {
                map.put(container.getContainerId(), blockRetrievedCommitted);
            }
        }
        try {
            Map<String, List<String>> rawMap = retrieveContents(map, clean);
            //creating repositories
            IVCRepository repository = GitVCSManager.createGitRepositoryInstance();
            long seed = System.currentTimeMillis();

            Random random = new Random(seed);
            int randomNumber = random.nextInt(); // Generates a random integer
            String projectedNameByFeatures = "Feature_" + featureIds.size() + "_" + randomNumber;

            Path path = repository.upsertRepository(projectedNameByFeatures);

            //project in files
            projectRawFiles(rawMap, clean, path);

            //commit
            try {
                Git gitRepository = repository.createGitDir(path);
                gitRepository.add().addFilepattern(".").call();
                gitRepository.commit().setMessage("BM: Projecting Commit").call();
            } catch (GitAPIException e) {
                throw new RuntimeException(e);
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    private Map<String, List<String>> retrieveContents(Map<String, List<Block>> map, boolean clean) {
        Map<String, List<String>> result = new LinkedHashMap<>();
        for (String path : map.keySet()) {
            List<String> rawBlock = new LinkedList<>();

            for (Block block : map.get(path)) {
                if (!clean) {
                    rawBlock.add(createBeginTag(block.getBlockId()));
                }
                String rawContent = gitVC.retrieveContent(block.getBlockId());
                rawBlock.add(rawContent);
                if (!clean) {
                    rawBlock.add(createEndTag(block.getBlockId()));
                }
            }

            result.put(path, rawBlock);
        }
        return result;
    }

    private String createEndTag(String blockId) {
        String template = "[%s]<-b";
        String message = String.format(template, blockId);
        return message;
    }

    private String createBeginTag(String blockId) {
        String template = "b->[%s]";
        String message = String.format(template, blockId);
        return message;
    }

    private List<Block> retrieveBlockCommitted(List<Block> blockRetrieved) {
        List<Block> result = new LinkedList<>();
        for (Block block : blockRetrieved) {
            if (block.getVcBlockState() == DataState.COMMITTED) {
                result.add(block);
            }
        }
        return result;
    }

    private List<Fragment> retrieveFragmentList(List<String> featureIds) {
        FragmentService fragmentService = new FragmentServiceImpl();
        return fragmentService.calculateFragmentsByFeatureList(featureIds);
    }

    private List<Block> retrieveBlockInBatches(Block block, List<Fragment> fragmentList) {
        List<Block> result = new LinkedList<>();
        BlockService blockService = new BlockServiceImpl();
        Block blockFull = blockService.getBlockByID(block.getBlockId());
        if (blockFull.getAssociatedTo() != null) {
            String fragmentId = blockFull.getAssociatedTo().getEndFragment().getFragmentId();
            if (fragmentIsInTheList(fragmentList, fragmentId)) {
                result.add(blockFull);
            }
        }
        while (blockFull.getGoNextBlock() != null) {
            blockFull = blockService.getBlockByID(blockFull.getGoNextBlock().getEndBlock().getBlockId());
            if (blockFull.getAssociatedTo() != null) {
                String fragmentId = blockFull.getAssociatedTo().getEndFragment().getFragmentId();
                if (fragmentIsInTheList(fragmentList, fragmentId)) {
                    result.add(blockFull);
                }
            }
        }
        return result;
    }

    private static boolean fragmentIsInTheList(List<Fragment> fragmentList, String fragment) {
        for (Fragment fragmentNode : fragmentList) {
            if (fragmentNode.getFragmentId().equals(fragment)) {
                return true;
            }
        }
        return false;
    }

    private void projectRawFiles(Map<String, List<String>> map, boolean clean, Path path)
            throws IOException {
        System.out.println("Projecting Raw files ...");
        for (String filePath : map.keySet()) {
            Path productRepository = Paths.get(String.valueOf(path), filePath);
            File file = new File(String.valueOf(productRepository));
            if (!file.exists()) {
                Files.createDirectories(file.getParentFile().toPath()); // method is not atomic
                // alternative  newDir.mkdirs();
                Files.createFile(file.toPath());
                if (!file.exists()) {
                    // file.getParentFile().mkdirs()
                    //Files.createFile(file.toPath());
                    System.out.println("CreateDirectories is not working for" + file.toPath());
                }
            }

            for (String block : map.get(filePath)) {
                Files.writeString(Paths.get(file.toURI()), block, StandardOpenOption.APPEND);
            }

        }
    }
}
