package ic.unicamp.bm.cli.cmd;

import ic.unicamp.bm.block.GitVCSManager;
import ic.unicamp.bm.block.IVCSAPI;
import ic.unicamp.bm.graph.neo4j.schema.Block;
import ic.unicamp.bm.graph.neo4j.schema.Container;
import ic.unicamp.bm.graph.neo4j.schema.Feature;
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
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

@Command(
    name = BMProjectProduct.CMD_NAME,
    description = "This command will project several SPL source code related to a list of products.")
public class BMProjectProduct implements Runnable {

  private final IVCSAPI gitBlock = GitVCSManager.createInstance();
  private final Git git = (Git) gitBlock.retrieveDirector();
  public static final String CMD_NAME = "project";

  @Option(names = "-clean")     boolean clean;

  @Parameters(index = "0..*")
  String[] products;

  @Override
  public void run() {
    for (String productId : products) {
      projectProduct(productId, clean);
    }
  }

  private void projectCleanProduct(String productId) {

    //blockService.getBlockByFeatures(featureList);
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
      blockService.getBlockByID(block.getBlockId());
      if(!blockRetrieved.isEmpty()){
        map.put(container.getContainerId(),blockRetrieved);
      }
    }
    try {
      projectFiles(map,clean);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private List<Block> retrieveBlockInBatches(Block block, List<Feature> featureList) {
    List<Block> result = new LinkedList<>();
    BlockService blockService = new BlockServiceImpl();
    Block blockFull = blockService.getBlockByID(block.getBlockId());
    String feature = blockFull.getAssociatedTo().getEndFeature().getFeatureId();
    if(featureIsInTheList(featureList, feature)){
      result.add(block);
    }
    while(blockFull.getGoNextBlock() != null){
      blockFull = blockFull.getGoNextBlock().getEndBlock();
      feature =  blockFull.getAssociatedTo().getEndFeature().getFeatureId();
      if(featureIsInTheList(featureList, feature)){
        result.add(block);
      }
    }
    return result;
  }

  private static boolean featureIsInTheList(List<Feature> featureList, String feature) {
    for (Feature featureNode : featureList) {
      if(featureNode.getFeatureId().equals(feature)){
        return true;
      }
    }
    return false;
  }

  private void projectFiles(Map<String, List<Block>> map, boolean clean) throws IOException {
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