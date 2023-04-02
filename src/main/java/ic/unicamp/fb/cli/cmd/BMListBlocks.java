package ic.unicamp.fb.cli.cmd;

import ic.unicamp.fb.graph.neo4j.schema.Block;
import ic.unicamp.fb.graph.neo4j.services.BlockService;
import ic.unicamp.fb.graph.neo4j.services.BlockServiceImpl;
import picocli.CommandLine.Command;

@Command(
        name = BMListBlocks.CMD_NAME,
        description = "This command will subscribe a product in the current Git branch")
public class BMListBlocks implements Runnable {

    public static final String CMD_NAME = "list-blocks";

    @Override
    public void run() {
        BlockService blockService = new BlockServiceImpl();
        for (Block block : blockService.findAll()) {
            String message = String.format("BlockId:%s BlockState:%s BlockVState:%s", block.getBlockId(),
                    block.getBlockState(), block.getVcBlockState());
            System.out.println(message);
        }
    }
}
