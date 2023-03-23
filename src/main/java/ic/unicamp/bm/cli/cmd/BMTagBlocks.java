package ic.unicamp.bm.cli.cmd;

import ic.unicamp.bm.graph.neo4j.schema.Block;
import ic.unicamp.bm.graph.neo4j.schema.Feature;
import ic.unicamp.bm.graph.neo4j.schema.Fragment;
import ic.unicamp.bm.graph.neo4j.schema.relations.BlockToFragment;
import ic.unicamp.bm.graph.neo4j.services.BlockService;
import ic.unicamp.bm.graph.neo4j.services.BlockServiceImpl;
import ic.unicamp.bm.graph.neo4j.services.FeatureService;
import ic.unicamp.bm.graph.neo4j.services.FeatureServiceImpl;
import ic.unicamp.bm.graph.neo4j.services.FragmentService;
import ic.unicamp.bm.graph.neo4j.services.FragmentServiceImpl;
import picocli.CommandLine;
import picocli.CommandLine.Parameters;

//ok
@CommandLine.Command(name = BMTagBlocks.CMD_NAME)
public class BMTagBlocks implements Runnable {

    public static final String CMD_NAME = "tag-blocks";

    @Parameters(index = "0", description = "fragment Id", defaultValue = "")
    String fragmentId;

    @Parameters(index = "1..*")
    String[] blockList;

    @Override
    public void run() {
        FragmentService fragmentService = new FragmentServiceImpl();
        Fragment fragment = fragmentService.getFragmentByID(fragmentId);
        if (fragment == null) {
            System.out.println("No Fragment with the given ID could be found");
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
                BlockToFragment relation = new BlockToFragment();
                relation.setStartBlock(block);
                relation.setEndFragment(fragment);
                block.setAssociatedTo(relation);
                blockService.createOrUpdate(block);
                String message = String.format("Block %s tagged", blockId);
                System.out.println(message);
            }
        }
    }
}
