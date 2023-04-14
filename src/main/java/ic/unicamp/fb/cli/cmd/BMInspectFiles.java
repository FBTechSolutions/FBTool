package ic.unicamp.fb.cli.cmd;

import ic.unicamp.fb.graph.neo4j.schema.Block;
import ic.unicamp.fb.graph.neo4j.schema.Container;
import ic.unicamp.fb.graph.neo4j.schema.Fragment;
import ic.unicamp.fb.graph.neo4j.schema.relations.BlockToFragment;
import ic.unicamp.fb.graph.neo4j.services.BlockService;
import ic.unicamp.fb.graph.neo4j.services.BlockServiceImpl;
import ic.unicamp.fb.graph.neo4j.services.ContainerService;
import ic.unicamp.fb.graph.neo4j.services.ContainerServiceImpl;
import picocli.CommandLine;
import picocli.CommandLine.Command;

import java.util.List;

@Command(
        name = BMInspectFiles.CMD_NAME,
        description = "This command will subscribe a product in the current Git branch")
public class BMInspectFiles implements Runnable {

    public static final String CMD_NAME = "inspect-files";

    @CommandLine.Parameters(index = "0..*")
    String[] filesIds;

    @Override
    public void run() {
        System.out.println("Inspecting files..");

        ContainerService containerService = new ContainerServiceImpl();
        BlockService blockService = new BlockServiceImpl();
        for (String filesId : filesIds) {
            Container container = containerService.getContainerByID(filesId);
            if (container != null) {
                List<Block> blocks = blockService.getBlocksByFile(filesId);
                for (Block block : blocks) {
                    String fragId = "";
                    BlockToFragment relation = block.getAssociatedTo();
                    Fragment fragment = relation.getEndFragment();
                    if (fragment != null) {
                        fragId = fragment.getFragmentId();
                    }
                    String message = String.format("- block:%s fragment:%s", block.getBlockId(),
                            fragId);
                    System.out.println(message);
                }
            } else {
                System.out.println(filesId + " file not found in the database");
            }
        }
    }
}
