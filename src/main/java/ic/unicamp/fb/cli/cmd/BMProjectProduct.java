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
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ListBranchCommand.ListMode;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Ref;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Command(
        name = BMProjectProduct.CMD_NAME,
        description = "It will project the source code with or without blocks of a given product.")
public class BMProjectProduct implements Runnable {

    private final IVCSAPI gitVC = GitVCSManager.createInstance();
    private final Git git = (Git) gitVC.retrieveDirector();
    public static final String CMD_NAME = "project";

    @Option(names = "-clean", defaultValue = "false")
    boolean clean;

    @Parameters(index = "0..*")
    String[] productList;

    @Override
    public void run() {
        if (productList == null) {
            System.out.println("You need to specify at least one Product Id");
            return;
        }
        for (String productId : productList) {
            projectProduct(productId, clean);
        }
    }

    private void projectProduct(String productId, boolean clean) {
        FeatureService featureService = new FeatureServiceImpl();
        List<Feature> featureList = featureService.getFeaturesByProductId(productId);

        BlockService blockService = new BlockServiceImpl();
        Map<String, List<Block>> map = new LinkedHashMap<>();
        List<ContainerToBlock> containerToBlockList = blockService.getContainerToBlockRelations();
        for (ContainerToBlock containerToBlock : containerToBlockList) {
            Container container = containerToBlock.getStartContainer();
            Block block = containerToBlock.getEndBlock();
            List<Fragment> fragmentList = retrieveBlockList(featureList);
            List<Block> blockRetrieved = retrieveBlockInBatches(block, fragmentList);
            List<Block> blockRetrievedCommitted = retrieveBlockCommitted(blockRetrieved);
            //blockService.getBlockByID(block.getBlockId());
            if (!blockRetrievedCommitted.isEmpty()) {
                map.put(container.getContainerId(), blockRetrievedCommitted);
            }
        }
        try {
            Map<String, List<String>> rawMap = retrieveContents(map, clean);
            //creating repositories
            IVCRepository repository = GitVCSManager.createGitRepositoryInstance();
            Path path = repository.upsertRepository(productId);

            //project in files
            projectRawFiles(rawMap, clean, path);

            //commit
            try {
                Git gitRepository = repository.createGitDir(path);
                //gitRepository.checkout().setName(productId).call();
                gitRepository.add().addFilepattern(".").call();
                gitRepository.commit().setMessage("BM: Projecting Commit").call();
            } catch (GitAPIException e) {
                throw new RuntimeException(e);
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean exitsBranch(String productId) {
        List<Ref> call;
        try {
            call = git.branchList().setListMode(ListMode.ALL).call();
            for (Ref ref : call) {
                if (ref.getName().contains(productId)) {
                    return true;
                }
            }
        } catch (GitAPIException e) {
            throw new RuntimeException(e);
        }
        return false;
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

    private List<Fragment> retrieveBlockList(List<Feature> featureList) {
        Set<Fragment> fragmentSet = new HashSet<>();
        FragmentService fragmentService = new FragmentServiceImpl();
        for (Feature feature : featureList) {
            List<Fragment> fragments = fragmentService.getFragmentsByFeatureId(feature.getFeatureId());
            fragmentSet.addAll(fragments);
        }
        return fragmentSet.stream().toList();
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

    /*  private void projectRawFiles(Map<String, List<Block>> map, boolean clean) throws IOException {
        for (String path : map.keySet()) {
          File file = new File(path);
          if (!file.exists()) {
              Files.createFile(file.toPath());
          }
          for (Block block : map.get(path)) {
            String rawContent = gitBlock.retrieveContent(block.getBlockId());
            Files.writeString(Paths.get(file.toURI()), rawContent);
          }
        }
      }*/
    private void projectRawFiles(Map<String, List<String>> map, boolean clean, Path path)
            throws IOException {
        System.out.println("Enter Project Files");
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
/*  private final GraphAPI graph = GraphBuilder.createGraphInstance();
  private final IBlockAPI gitBlock = GitBlockManager.createGitBlockInstance();
  private final Git git = (Git) gitBlock.retrieveDirector();*/

// blocks tags(bm project features_list/ default)
// without blocks tags ( bm project -prod)

/*    try {
      //
      git.checkout().setName("BM_ALL").setOrphan(true).call();

    } catch (GitAPIException e) {
      throw new RuntimeException(e);
    }*/
//process blocks by Feature (query in the DB)
//create new branch
//project files and stuff

//set an ID for the fist commit