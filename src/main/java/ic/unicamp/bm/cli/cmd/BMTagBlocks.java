package ic.unicamp.bm.cli.cmd;

import ic.unicamp.bm.graph.neo4j.schema.Block;
import ic.unicamp.bm.graph.neo4j.schema.Feature;
import ic.unicamp.bm.graph.neo4j.schema.relations.BlockToFeature;
import ic.unicamp.bm.graph.neo4j.services.BlockService;
import ic.unicamp.bm.graph.neo4j.services.BlockServiceImpl;
import ic.unicamp.bm.graph.neo4j.services.FeatureService;
import ic.unicamp.bm.graph.neo4j.services.FeatureServiceImpl;
import picocli.CommandLine;
import picocli.CommandLine.Parameters;

@CommandLine.Command(name = BMTagBlocks.CMD_NAME)
public class BMTagBlocks implements Runnable {

  public static final String CMD_NAME = "tag-blocks";

  @Parameters(index = "0", description = "featureId", defaultValue = "")
  String featureId;

  @Parameters(index = "1..*")
  String[] blockIds;

  @Override
  public void run() {
    FeatureService featureService = new FeatureServiceImpl();
    BlockService blockService = new BlockServiceImpl();
    Feature feature = featureService.getFeatureByID(featureId);
    if (feature == null) {
      System.out.println("There is not a feature with that Id");
    }
    for (String blockId : blockIds) {
      Block block = blockService.getBlockByID(blockId);
      if (block == null) {
        System.out.println("There is not a Block with Id " + blockId);
      }else{
        BlockToFeature relation = new BlockToFeature();
        relation.setStartBlock(block);
        relation.setEndFeature(feature);
        block.setAssociatedTo(relation);
        blockService.createOrUpdate(block);
      }
    }
  }
}
