package ic.unicamp.bm.cli.cmd;

import ic.unicamp.bm.graph.neo4j.schema.Block;
import ic.unicamp.bm.graph.neo4j.schema.Fragment;
import ic.unicamp.bm.graph.neo4j.schema.relations.BlockToFragment;
import ic.unicamp.bm.graph.neo4j.services.BlockService;
import ic.unicamp.bm.graph.neo4j.services.BlockServiceImpl;
import ic.unicamp.bm.graph.neo4j.services.FragmentService;
import ic.unicamp.bm.graph.neo4j.services.FragmentServiceImpl;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

import java.util.List;

import static ic.unicamp.bm.graph.neo4j.utils.FragmentUtil.retrieveOrCreateAStandardFragment;

@Command(
        name = BMMoveBlocks.CMD_NAME,
        description = "This command will add the blocks from the temporal folder to the real folder")
public class BMMoveBlocks implements Runnable {

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

        Fragment oldF = fragmentService.getFragmentByID(oldFragmentId);
        if (oldF == null) {
            System.out.println("Fragment source not valid");
            return;
        }
        Fragment newF = retrieveOrCreateAStandardFragment(fragmentService, newFragmentId, newFragmentId);

        BlockService blockService = new BlockServiceImpl();
        List<Block> blocks = blockService.getBlocksByFeature(oldFragmentId);
        for (Block block : blocks) {
            Block blockUpdated = blockService.getBlockByID(block.getBlockId());
            BlockToFragment blockToFeature = new BlockToFragment();
            blockToFeature.setStartBlock(block);
            blockToFeature.setEndFragment(newF);
            blockUpdated.setAssociatedTo(blockToFeature);
            blockService.createOrUpdate(blockUpdated);
        }
    }
}
