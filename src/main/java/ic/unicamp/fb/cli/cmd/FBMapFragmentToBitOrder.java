package ic.unicamp.fb.cli.cmd;

import ic.unicamp.fb.graph.neo4j.schema.BitOrder;
import ic.unicamp.fb.graph.neo4j.schema.Fragment;
import ic.unicamp.fb.graph.neo4j.schema.relations.FragmentToBitOrder;
import ic.unicamp.fb.graph.neo4j.services.BitOrderService;
import ic.unicamp.fb.graph.neo4j.services.BitOrderServiceImpl;
import ic.unicamp.fb.graph.neo4j.services.FragmentService;
import ic.unicamp.fb.graph.neo4j.services.FragmentServiceImpl;
import picocli.CommandLine;
import picocli.CommandLine.Parameters;

import java.util.ArrayList;
import java.util.List;

//ok
@CommandLine.Command(name = FBMapFragmentToBitOrder.CMD_NAME)
public class FBMapFragmentToBitOrder implements Runnable {

    public static final String CMD_NAME = "map-frag-to-order";

    @Parameters(index = "0", description = "fragment Id", defaultValue = "")
    String fragmentId;

    @Parameters(index = "1..*")
    String[] bitOrderList;

    @Override
    public void run() {
        FragmentService fragmentService = new FragmentServiceImpl();
        Fragment fullFragment = fragmentService.getFragmentByID(fragmentId);
        if (fullFragment == null) {
            System.out.println("No Fragment with the given ID could be found");
            return;
        }
        if (bitOrderList == null) {
            System.out.println("This command requires bit order ids");
            return;
        }

        BitOrderService bitOrderService = new BitOrderServiceImpl();
        List<FragmentToBitOrder> relations = new ArrayList<>();
        for (String bitOrderId : bitOrderList) {
            BitOrder fullBitOrder= bitOrderService.getBitOrderByID(bitOrderId);
            if (fullBitOrder == null) {
                String message = String.format("No Bit Order with the given ID (%s) could be found", bitOrderId);
                System.out.println(message);
            } else {
                FragmentToBitOrder relation = new FragmentToBitOrder();
                relation.setStartFragment(fullFragment);
                relation.setEndBitOrder(fullBitOrder);
                relations.add(relation);
            }
        }
        fullFragment.setAssociatedTo(relations);
        fragmentService.createOrUpdate(fullFragment);
    }
}
