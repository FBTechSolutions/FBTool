package ic.unicamp.fb.cli.cmd;

import ic.unicamp.fb.graph.neo4j.schema.Block;
import ic.unicamp.fb.graph.neo4j.schema.enums.BlockState;
import ic.unicamp.fb.graph.neo4j.services.BlockService;
import ic.unicamp.fb.graph.neo4j.services.BlockServiceImpl;
import picocli.CommandLine;
import picocli.CommandLine.Command;

@Command(
        name = FBCleanDBBlocks.CMD_NAME,
        description = "This command will clean the block directives from the current Git branch")
public class FBCleanDBBlocks implements Runnable {

    public static final String CMD_NAME = "clean-db-blocks";

    @Override
    public void run() {
        BlockService blockService = new BlockServiceImpl();
        for (Block block : blockService.findAll()) {
            if (block.getBlockState().equals(BlockState.TO_INSERT)) {
                blockService.delete(block.getId());
            }
        }
        //calling another command
        CommandLine commandLine = new CommandLine(new FBListBlocks());
        commandLine.execute();
    }
}
