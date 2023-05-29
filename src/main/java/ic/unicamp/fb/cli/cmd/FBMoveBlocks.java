package ic.unicamp.fb.cli.cmd;

import ic.unicamp.fb.graph.neo4j.schema.Block;
import ic.unicamp.fb.graph.neo4j.schema.Fragment;
import ic.unicamp.fb.graph.neo4j.schema.relations.BlockToFragment;
import ic.unicamp.fb.graph.neo4j.services.BlockService;
import ic.unicamp.fb.graph.neo4j.services.BlockServiceImpl;
import ic.unicamp.fb.graph.neo4j.services.FragmentService;
import ic.unicamp.fb.graph.neo4j.services.FragmentServiceImpl;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

import java.util.List;

@Command(
        name = FBMoveBlocks.CMD_NAME,
        description = "This command will add the blocks from the temporal folder to the real folder")
public class FBMoveBlocks implements Runnable {

    public static final String CMD_NAME = "move-blocks";
    @Parameters(index = "0", description = "old Fragment Id", defaultValue = "")
    String oldFragmentId;
    @Parameters(index = "1", description = "new Fragment Id", defaultValue = "")
    String newFragmentId;

    @Override
    public void run() {
        if (oldFragmentId.equals("") || newFragmentId.equals("")) {
            System.out.println("You need two parameters");
            return;
        }
        FragmentService fragmentService = new FragmentServiceImpl();

        Fragment oldFullFragment = fragmentService.getFragmentByID(oldFragmentId);
        if (oldFullFragment == null) {
            System.out.println("Fragment source not valid");
            return;
        }
        Fragment newFullFragment = fragmentService.getFragmentByID(newFragmentId);
        if (newFullFragment == null) {
            System.out.println("Fragment target not valid");
            return;
        }
        //newFullFragment = retrieveOrCreateAStandardFragment(fragmentService, newFragmentId, newFragmentId, newFragmentId);

        BlockService blockService = new BlockServiceImpl();
        List<Block> blocks = blockService.getBlocksByFragment(oldFragmentId);
        for (Block block : blocks) {
            Block fullBlock = blockService.getBlockByID(block.getBlockId());
            BlockToFragment blockToFragment = fullBlock.getAssociatedTo();
            if (blockToFragment == null) {
                blockToFragment = new BlockToFragment();
            }
            blockToFragment.setStartBlock(fullBlock);
            blockToFragment.setEndFragment(newFullFragment);

            fullBlock.setAssociatedTo(blockToFragment);
            blockService.createOrUpdate(fullBlock, 1);
        }

        //calling another command
        CommandLine commandLine = new CommandLine(new FBInspectFiles());
        commandLine.execute("--all");
    }
}
