package ic.unicamp.bm.cli.cmd;

import static ic.unicamp.bm.block.GitVCS.BMBranchLabel;

import ic.unicamp.bm.block.GitVCSManager;
import ic.unicamp.bm.block.IVCSAPI;
import ic.unicamp.bm.graph.neo4j.schema.Block;
import ic.unicamp.bm.graph.neo4j.schema.Container;
import ic.unicamp.bm.graph.neo4j.schema.Feature;
import ic.unicamp.bm.graph.neo4j.schema.enums.DataState;
import ic.unicamp.bm.graph.neo4j.schema.relations.ContainerToBlock;
import ic.unicamp.bm.graph.neo4j.services.BlockService;
import ic.unicamp.bm.graph.neo4j.services.BlockServiceImpl;
import ic.unicamp.bm.graph.neo4j.services.FeatureService;
import ic.unicamp.bm.graph.neo4j.services.FeatureServiceImpl;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ListBranchCommand.ListMode;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Ref;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

@Command(
    name = BMProjectProduct.CMD_NAME,
    description = "This command will project the source code of a product.")
public class BMProjectProduct implements Runnable {

  private final IVCSAPI gitVC = GitVCSManager.createInstance();
  private final Git git = (Git) gitVC.retrieveDirector();
  public static final String CMD_NAME = "project";

  @Option(names = "-clean",  defaultValue = "false")
  boolean clean;

  @Parameters(index = "0..*")
  String[] productList;

  @Override
  public void run() {
    if(productList == null){
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
      List<Block> blockRetrieved = retrieveBlockInBatches(block, featureList);
      List<Block> blockRetrievedCommitted = retrieveBlockCommitted(blockRetrieved);
      //blockService.getBlockByID(block.getBlockId());
      if (!blockRetrievedCommitted.isEmpty()) {
        map.put(container.getContainerId(), blockRetrievedCommitted);
      }
    }
    try {
      Map<String, List<String>> rawMap = retrieveContents(map);
      //creating branch
      try {
        if (!exitsBranch(productId)) {
          git.checkout().setCreateBranch(true).setName(productId).setForced(true).setOrphan(true).call();
          /*git.checkout().setCreateBranch(true).setOrphan(true).setName(productId)
              .call();
          git.rm().setCached(true).addFilepattern(".").call();
          git.rm().addFilepattern(".").call();*/

          //git.commit().setMessage("BM: Projecting create").call();
        } else {
        /*  git.checkout().setName(BMBranchLabel).call();
          git.branchDelete().setBranchNames(productId).setForce(true).call();
          git.checkout().setCreateBranch(true).setOrphan(true).setName(productId).call();
          git.rm().setCached(true).addFilepattern(".").call();
          git.rm().addFilepattern(".").call();

          git.commit().setMessage("BM: Projecting updated").call();*/


          //git.checkout().setName(productId).call();
          //git.checkout().setOrphan(true).setName(productId).call();
          //git.rm().setCached(true).call();
        }
      } catch (GitAPIException e) {
        throw new RuntimeException(e);
      }
      //project in files
      projectRawFiles(rawMap, clean);

      //commit
      try {
        git.checkout().setName(productId).call();
        git.add().addFilepattern(".").call();
        git.commit().setMessage("BM: Projecting Commit").call();
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

  private Map<String, List<String>> retrieveContents(Map<String, List<Block>> map) {
    Map<String, List<String>> result = new LinkedHashMap<>();
    for (String path : map.keySet()) {
      List<String> rawBlock = new LinkedList<>();
      for (Block block : map.get(path)) {
        String rawContent = gitVC.retrieveContent(block.getBlockId());
        rawBlock.add(rawContent);
      }
      result.put(path, rawBlock);
    }
    return result;
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

  private List<Block> retrieveBlockInBatches(Block block, List<Feature> featureList) {
    List<Block> result = new LinkedList<>();
    BlockService blockService = new BlockServiceImpl();
    Block blockFull = blockService.getBlockByID(block.getBlockId());
    if (blockFull.getAssociatedTo() != null) {
      String feature = blockFull.getAssociatedTo().getEndFeature().getFeatureId();
      if (featureIsInTheList(featureList, feature)) {
        result.add(block);
      }
    }
    while (blockFull.getGoNextBlock() != null) {
      blockFull = blockFull.getGoNextBlock().getEndBlock();
      if (blockFull.getAssociatedTo() != null) {
        String feature = blockFull.getAssociatedTo().getEndFeature().getFeatureId();
        if (featureIsInTheList(featureList, feature)) {
          result.add(block);
        }
      }
    }
    return result;
  }

  private static boolean featureIsInTheList(List<Feature> featureList, String feature) {
    for (Feature featureNode : featureList) {
      if (featureNode.getFeatureId().equals(feature)) {
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
  private void projectRawFiles(Map<String, List<String>> map, boolean clean) throws IOException {
    System.out.println("Enter Project Files");
    for (String path : map.keySet()) {
      System.out.println(path);
      File file = new File(path);
      if (!file.exists()) {
        Files.createFile(file.toPath());
      }
      for (String block : map.get(path)) {
        Files.writeString(Paths.get(file.toURI()), block);
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