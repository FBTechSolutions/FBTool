package ic.unicamp.fb.cli.cmd;

import ic.unicamp.fb.graph.neo4j.schema.Block;
import ic.unicamp.fb.graph.neo4j.schema.Fragment;
import ic.unicamp.fb.graph.neo4j.services.BlockService;
import ic.unicamp.fb.graph.neo4j.services.BlockServiceImpl;
import ic.unicamp.fb.graph.neo4j.services.FragmentService;
import ic.unicamp.fb.graph.neo4j.services.FragmentServiceImpl;
import picocli.CommandLine;
import picocli.CommandLine.Command;

@Command(
        name = FBListFragments.CMD_NAME,
        description = "This command will list all the fragments")
public class FBListFragments implements Runnable {

    public static final String CMD_NAME = "list-fragments";

    @CommandLine.Option(names = {"--b", "--blocks"}, description = "Add blocks to the list")
    private boolean isBlockEnabled;

    @Override
    public void run() {
        System.out.println("Listing all fragments...");
        FragmentService fragmentService = new FragmentServiceImpl();
        for (Fragment fragment : fragmentService.findAll()) {
            String message = String.format("- id:%s label:%s", fragment.getFragmentId(),
                    fragment.getFragmentLabel());
            System.out.println(message);
            if (isBlockEnabled) {
                BlockService blockService = new BlockServiceImpl();
                for (Block block : blockService.getBlocksByFragment(fragment.getFragmentId())) {
                    System.out.printf("  - id:%s state:%s vcState:%s%n", block.getBlockId(),
                            block.getBlockState(), block.getVcBlockState());
                }
            }
        }
    }
}
