package ic.unicamp.fb.cli.cmd;

import ic.unicamp.fb.graph.neo4j.schema.Index;
import ic.unicamp.fb.graph.neo4j.schema.Block;
import ic.unicamp.fb.graph.neo4j.schema.Fragment;
import ic.unicamp.fb.graph.neo4j.services.IndexService;
import ic.unicamp.fb.graph.neo4j.services.IndexServiceImpl;
import ic.unicamp.fb.graph.neo4j.services.BlockService;
import ic.unicamp.fb.graph.neo4j.services.BlockServiceImpl;
import ic.unicamp.fb.graph.neo4j.services.FragmentService;
import ic.unicamp.fb.graph.neo4j.services.FragmentServiceImpl;
import picocli.CommandLine;
import picocli.CommandLine.Command;

import java.util.List;

@Command(
        name = FBListFragments.CMD_NAME,
        description = "This command will list all the fragments")
public class FBListFragments implements Runnable {

    public static final String CMD_NAME = "list-fragments";

    @CommandLine.Option(names = {"--b", "--blocks"}, description = "Add blocks to the list")
    private boolean isBlockEnabled;

    @CommandLine.Option(names = {"--o", "--order"}, description = "Add orders to the list")
    private boolean isOrderEnabled;

    @Override
    public void run() {
        System.out.println("Listing all fragments...");
        FragmentService fragmentService = new FragmentServiceImpl();
        for (Fragment fragment : fragmentService.findAll()) {
            String message = String.format("- id:%s label:%s", fragment.getFragmentId(),
                    fragment.getFragmentLabel());
            System.out.println(message);
            if (isBlockEnabled) {
                System.out.println("  blocks ...");
                BlockService blockService = new BlockServiceImpl();
                for (Block block : blockService.getBlocksByFragment(fragment.getFragmentId())) {
                    System.out.printf("  - id:%s state:%s vcState:%s%n", block.getBlockId(),
                            block.getBlockState(), block.getVcBlockState());
                }
            }
            if (isOrderEnabled) {
                System.out.println("  bits order ...");
                IndexService indexService = new IndexServiceImpl();
                List<Index> indexList = indexService.getIndexByFragment(fragment.getFragmentId());
                for (Index index : indexList) {
                    System.out.printf("  - bit:%s %n", index.getIndexId());
                }
            }
        }
    }
}
