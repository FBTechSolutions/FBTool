package ic.unicamp.fb.cli.cmd;

import ic.unicamp.fb.graph.neo4j.schema.BitOrder;
import ic.unicamp.fb.graph.neo4j.schema.Block;
import ic.unicamp.fb.graph.neo4j.services.BitOrderService;
import ic.unicamp.fb.graph.neo4j.services.BitOrderServiceImpl;
import ic.unicamp.fb.graph.neo4j.services.BlockService;
import ic.unicamp.fb.graph.neo4j.services.BlockServiceImpl;
import picocli.CommandLine;
import picocli.CommandLine.Command;

@Command(
        name = FBListBitOrders.CMD_NAME,
        description = "This command will list all the blocks")
public class FBListBitOrders implements Runnable {

    public static final String CMD_NAME = "list-bit-order";
    
    @Override
    public void run() {
        System.out.println("Listing all bit orders ...");
        BitOrderService bitOrderService = new BitOrderServiceImpl();
        for (BitOrder bit : bitOrderService.findAll()) {
            String message = String.format("- bit:%s", bit.getBitOrderId());
            System.out.println(message);
        }
    }
}
