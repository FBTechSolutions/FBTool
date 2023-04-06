package ic.unicamp.fb.cli.cmd;

import ic.unicamp.fb.graph.neo4j.schema.Fragment;
import ic.unicamp.fb.graph.neo4j.services.FragmentService;
import ic.unicamp.fb.graph.neo4j.services.FragmentServiceImpl;
import picocli.CommandLine.Command;

@Command(
        name = BMListFragments.CMD_NAME,
        description = "This command will subscribe a product in the current Git branch")
public class BMListFragments implements Runnable {

    public static final String CMD_NAME = "list-fragments";

    @Override
    public void run() {
        System.out.println("Listing all fragments...");
        FragmentService fragmentService = new FragmentServiceImpl();
        for (Fragment fragment : fragmentService.findAll()) {
            String message = String.format("id:%s label:%s", fragment.getFragmentId(),
                    fragment.getFragmentLabel());
            System.out.println(message);
        }
    }
}
