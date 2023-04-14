package ic.unicamp.fb.cli.cmd;

import ic.unicamp.fb.graph.neo4j.schema.Block;
import ic.unicamp.fb.graph.neo4j.schema.Container;
import ic.unicamp.fb.graph.neo4j.schema.enums.ContainerType;
import ic.unicamp.fb.graph.neo4j.services.BlockService;
import ic.unicamp.fb.graph.neo4j.services.BlockServiceImpl;
import ic.unicamp.fb.graph.neo4j.services.ContainerService;
import ic.unicamp.fb.graph.neo4j.services.ContainerServiceImpl;
import picocli.CommandLine.Command;

@Command(
        name = BMListFiles.CMD_NAME,
        description = "This command will subscribe a product in the current Git branch")
public class BMListFiles implements Runnable {

    public static final String CMD_NAME = "list-files";

    @Override
    public void run() {
        System.out.println("Listing all files...");
        ContainerService containerService = new ContainerServiceImpl();
        for (Container container : containerService.findAll()) {
            if(container.getContainerType() == ContainerType.FILE){
                String message = String.format("- id:%s ", container.getContainerId());
                System.out.println(message);
            }
        }
    }
}
