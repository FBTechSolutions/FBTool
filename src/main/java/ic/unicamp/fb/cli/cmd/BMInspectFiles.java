package ic.unicamp.fb.cli.cmd;

import ic.unicamp.fb.graph.neo4j.schema.Block;
import ic.unicamp.fb.graph.neo4j.schema.Container;
import ic.unicamp.fb.graph.neo4j.schema.Fragment;
import ic.unicamp.fb.graph.neo4j.schema.enums.ContainerType;
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

    @CommandLine.Option(names = "-all", defaultValue = "false")
    boolean all;

    @Override
    public void run() {
        System.out.println("Inspecting files..");

        ContainerService containerService = new ContainerServiceImpl();
        BlockService blockService = new BlockServiceImpl();
        if (all) {
            for (Container container : containerService.findAll()) {
                if (container.getContainerType() == ContainerType.FILE) {
                    String fileId = container.getContainerId();
                    printFileId(fileId);
                    inspectAFile(containerService, blockService, fileId);
                }
            }
        } else {
            for (String fileId : filesIds) {
                printFileId(fileId);
                inspectAFile(containerService, blockService, fileId);
            }
        }
    }

    private static void printFileId(String fileId) {
        String message = String.format("- file:%s :", fileId);
        System.out.println(message);
    }

    private static void inspectAFile(ContainerService containerService, BlockService blockService, String fileId) {
        Container fullContainer = containerService.getContainerByID(fileId);
        if (fullContainer != null) {
            List<Block> blocks = blockService.getBlocksByFile(fileId);
            for (Block block : blocks) {
                String blockId = block.getBlockId();
                String fragId = "";
                Block fullBlock = blockService.getBlockByID(blockId);
                if (fullBlock != null) {
                    BlockToFragment relation = fullBlock.getAssociatedTo();
                    if (relation != null) {
                        Fragment fragment = relation.getEndFragment();
                        if (fragment != null) {
                            fragId = fragment.getFragmentId();
                        }
                    }else{
                        System.out.println("  - not fragment associated to "+ fullBlock.getBlockId() );
                    }
                }
                String message = String.format("  - block:%s fragment:%s", blockId,
                        fragId);
                System.out.println(message);
            }
        } else {
            System.out.println(fileId + " file not found in the database");
        }
    }
}
