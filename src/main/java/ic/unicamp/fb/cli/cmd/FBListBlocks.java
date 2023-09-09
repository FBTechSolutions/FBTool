package ic.unicamp.fb.cli.cmd;

import ic.unicamp.fb.graph.neo4j.schema.Block;
import ic.unicamp.fb.graph.neo4j.schema.Fragment;
import ic.unicamp.fb.graph.neo4j.services.BlockService;
import ic.unicamp.fb.graph.neo4j.services.BlockServiceImpl;
import ic.unicamp.fb.graph.neo4j.utils.BlockUtil;
import ic.unicamp.fb.graph.neo4j.utils.FragmentUtil;
import picocli.CommandLine.Command;

import java.util.List;

@Command(
        name = FBListBlocks.CMD_NAME,
        description = "This command will list all the blocks")
public class FBListBlocks implements Runnable {

    public static final String CMD_NAME = "list-blocks";

    @Override
    public void run() {
        System.out.println("Listing all blocks...");
        BlockService blockService = new BlockServiceImpl();
        List<Block> orderedBlocks = BlockUtil.orderBlocks(blockService.findAll());
        for (Block block : orderedBlocks) {
            String message = String.format("- id:%s state:%s vcState:%s", block.getBlockId(),
                    block.getBlockState(), block.getVcBlockState());
            System.out.println(message);
        }
    }
}
