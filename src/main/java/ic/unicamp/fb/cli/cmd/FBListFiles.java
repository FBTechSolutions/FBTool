package ic.unicamp.fb.cli.cmd;

import ic.unicamp.fb.graph.neo4j.schema.Container;
import ic.unicamp.fb.graph.neo4j.schema.enums.ContainerType;
import ic.unicamp.fb.graph.neo4j.services.ContainerService;
import ic.unicamp.fb.graph.neo4j.services.ContainerServiceImpl;
import picocli.CommandLine.Command;

@Command(
        name = FBListFiles.CMD_NAME,
        description = "This command will list all the files")
public class FBListFiles implements Runnable {

    public static final String CMD_NAME = "list-files";

    @Override
    public void run() {
        System.out.println("Listing all files...");
        ContainerService containerService = new ContainerServiceImpl();
        for (Container container : containerService.findAll()) {
            if (container.getContainerType() == ContainerType.FILE) {
                String message = String.format("- id:%s ", container.getContainerId());
                System.out.println(message);
            }
        }
    }
}
