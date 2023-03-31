package ic.unicamp.bm.cli.cmd;

import ic.unicamp.bm.graph.neo4j.schema.Block;
import ic.unicamp.bm.graph.neo4j.schema.Fragment;
import ic.unicamp.bm.graph.neo4j.services.BlockService;
import ic.unicamp.bm.graph.neo4j.services.BlockServiceImpl;
import ic.unicamp.bm.graph.neo4j.services.FragmentService;
import ic.unicamp.bm.graph.neo4j.services.FragmentServiceImpl;
import picocli.CommandLine.Command;

@Command(
        name = BMListFragments.CMD_NAME,
        description = "This command will subscribe a product in the current Git branch")
public class BMListFragments implements Runnable {

    public static final String CMD_NAME = "list-fragments";

    @Override
    public void run() {
        FragmentService fragmentService = new FragmentServiceImpl();
        for (Fragment fragment : fragmentService.findAll()) {
            String message = String.format("FragmentId:%s FragmentLabel:%s", fragment.getFragmentId(),
                    fragment.getFragmentLabel());
            System.out.println(message);
        }
    }
}
