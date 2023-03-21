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

//ok
@CommandLine.Command(name = BMTagBlocks.CMD_NAME)
public class BMTagBlocks implements Runnable {

    public static final String CMD_NAME = "tag-blocks";

    @Parameters(index = "0", description = "featureId", defaultValue = "")
    String featureId;

    @Parameters(index = "1..*")
    String[] blockList;

    @Override
    public void run() {
        FeatureService featureService = new FeatureServiceImpl();
        Feature feature = featureService.getFeatureByID(featureId);
        if (feature == null) {
            System.out.println("No Feature with the given ID could be found");
            return;
        }
        if (blockList == null) {
            System.out.println("This command requires block ids");
            return;
        }

        BlockService blockService = new BlockServiceImpl();
        for (String blockId : blockList) {
            Block block = blockService.getBlockByID(blockId);
            if (block == null) {
                String message = String.format("No Block with the given ID (%s) could be found", blockId);
                System.out.println(message);
            } else {
                BlockToFeature relation = new BlockToFeature();
                relation.setStartBlock(block);
                relation.setEndFeature(feature);
                block.setAssociatedTo(relation);
                blockService.createOrUpdate(block);
                String message = String.format("Block %s tagged", blockId);
                System.out.println(message);
            }
        }
    }
}
