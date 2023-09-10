package ic.unicamp.fb.cli.cmd;

import ic.unicamp.fb.graph.neo4j.schema.Feature;
import ic.unicamp.fb.graph.neo4j.schema.Index;
import ic.unicamp.fb.graph.neo4j.schema.Block;
import ic.unicamp.fb.graph.neo4j.schema.Fragment;
import ic.unicamp.fb.graph.neo4j.services.IndexService;
import ic.unicamp.fb.graph.neo4j.services.IndexServiceImpl;
import ic.unicamp.fb.graph.neo4j.services.BlockService;
import ic.unicamp.fb.graph.neo4j.services.BlockServiceImpl;
import ic.unicamp.fb.graph.neo4j.services.FragmentService;
import ic.unicamp.fb.graph.neo4j.services.FragmentServiceImpl;
import ic.unicamp.fb.graph.neo4j.utils.FeatureUtil;
import ic.unicamp.fb.graph.neo4j.utils.FragmentUtil;
import picocli.CommandLine;
import picocli.CommandLine.Command;

import java.util.Collections;
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
        List<Fragment> orderedFragments = FragmentUtil.orderFragments(fragmentService.findAll());
        for (Fragment fragment : orderedFragments) {
            String message = String.format("- id:%s label:%s", fragment.getFragmentId(),
                    fragment.getFragmentLabel());
            System.out.println(message);
            if (isBlockEnabled) {
                System.out.println("  blocks ...");
                BlockService blockService = new BlockServiceImpl();
                List<Block> blockList = blockService.getBlocksByFragment(fragment.getFragmentId());
                Collections.sort(blockList);
                for (Block block : blockList) {
                    System.out.printf("  - id:%s state:%s vcState:%s%n", block.getBlockId(),
                            block.getBlockState(), block.getVcBlockState());
                }
            }
            if (isOrderEnabled) {
                System.out.println("  bits order ...");
                IndexService indexService = new IndexServiceImpl();
                List<Index> indexList = indexService.getIndexByFragment(fragment.getFragmentId());
                Collections.sort(indexList);
                for (Index index : indexList) {
                    System.out.printf("  - bit:%s %n", index.getIndexId());
                }
            }
        }
    }
}
