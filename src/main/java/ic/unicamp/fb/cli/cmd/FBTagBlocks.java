package ic.unicamp.fb.cli.cmd;

import ic.unicamp.fb.graph.neo4j.schema.Block;
import ic.unicamp.fb.graph.neo4j.schema.Fragment;
import ic.unicamp.fb.graph.neo4j.schema.relations.BlockToFragment;
import ic.unicamp.fb.graph.neo4j.services.BlockService;
import ic.unicamp.fb.graph.neo4j.services.BlockServiceImpl;
import ic.unicamp.fb.graph.neo4j.services.FragmentService;
import ic.unicamp.fb.graph.neo4j.services.FragmentServiceImpl;
import picocli.CommandLine;
import picocli.CommandLine.Parameters;

//ok
@CommandLine.Command(name = FBTagBlocks.CMD_NAME)
public class FBTagBlocks implements Runnable {

    public static final String CMD_NAME = "tag-blocks";

    @Parameters(index = "0", description = "fragment Id", defaultValue = "")
    String fragmentId;

    @Parameters(index = "1..*")
    String[] blockList;

    @Override
    public void run() {
        FragmentService fragmentService = new FragmentServiceImpl();
        Fragment fullFragment = fragmentService.getFragmentByID(fragmentId);
        if (fullFragment == null) {
            System.out.println("No Fragment with the given ID could be found");
            return;
        }
        if (blockList == null) {
            System.out.println("This command requires block ids");
            return;
        }

        BlockService blockService = new BlockServiceImpl();
        for (String blockId : blockList) {
            Block fullBlock = blockService.getBlockByID(blockId);
            if (fullBlock == null) {
                String message = String.format("No Block with the given ID (%s) could be found", blockId);
                System.out.println(message);
            } else {
                BlockToFragment blockToFragment = fullBlock.getAssociatedTo();
                if (blockToFragment == null) {
                    blockToFragment = new BlockToFragment();
                    blockToFragment.setStartBlock(fullBlock);
                }
                blockToFragment.setEndFragment(fullFragment);

                fullBlock.setAssociatedTo(blockToFragment);
                blockService.createOrUpdate(fullBlock);
                String message = String.format("Block %s tagged", blockId);
                System.out.println(message);
            }
        }
    }
}
