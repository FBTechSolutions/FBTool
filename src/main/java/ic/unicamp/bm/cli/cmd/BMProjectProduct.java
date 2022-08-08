package ic.unicamp.bm.cli.cmd;

import ic.unicamp.bm.graph.neo4j.schema.Feature;
import ic.unicamp.bm.graph.neo4j.services.BlockService;
import ic.unicamp.bm.graph.neo4j.services.BlockServiceImpl;
import ic.unicamp.bm.graph.neo4j.services.FeatureService;
import ic.unicamp.bm.graph.neo4j.services.FeatureServiceImpl;
import ic.unicamp.bm.graph.neo4j.services.ProductService;
import ic.unicamp.bm.graph.neo4j.services.ProductServiceImpl;
import java.util.List;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

@Command(
    name = BMProjectProduct.CMD_NAME,
    description = "This command will project several SPL source code related to a list of products.")
public class BMProjectProduct implements Runnable {

  public static final String CMD_NAME = "project";

  @Option(names = "-clean")     boolean clean;

  @Parameters(index = "0..*")
  String[] products;

  @Override
  public void run() {
    if(clean){
      for (String product : products) {
        projectCleanProduct(product);
      }
    }else{
      for (String product : products) {
        projectProduct(product);
      }
    }

  }

  private void projectCleanProduct(String product) {
    FeatureService featureService = new FeatureServiceImpl();
    BlockService blockService = new BlockServiceImpl();
    List<Feature> featureList = featureService.getFeaturesByProductId();
    //blockService.getBlockByFeatures(featureList);
  }

  private void projectProduct(String product) {

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