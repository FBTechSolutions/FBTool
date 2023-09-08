package ic.unicamp.fb.cli.cmd;

import ic.unicamp.fb.graph.neo4j.schema.Index;
import ic.unicamp.fb.graph.neo4j.services.IndexService;
import ic.unicamp.fb.graph.neo4j.services.IndexServiceImpl;
import picocli.CommandLine.Command;

@Command(
        name = FBListIndexes.CMD_NAME,
        description = "This command will list all the blocks")
public class FBListIndexes implements Runnable {

    public static final String CMD_NAME = "list-indexes";
    
    @Override
    public void run() {
        System.out.println("Listing all bit orders ...");
        IndexService indexService = new IndexServiceImpl();
        for (Index bit : indexService.findAll()) {
            String message = String.format("- bit:%s", bit.getIndexId());
            System.out.println(message);
        }
    }
}
