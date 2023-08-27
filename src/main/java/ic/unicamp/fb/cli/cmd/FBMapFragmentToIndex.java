package ic.unicamp.fb.cli.cmd;

import ic.unicamp.fb.graph.neo4j.schema.Index;
import ic.unicamp.fb.graph.neo4j.schema.Fragment;
import ic.unicamp.fb.graph.neo4j.schema.relations.FragmentToIndex;
import ic.unicamp.fb.graph.neo4j.services.IndexService;
import ic.unicamp.fb.graph.neo4j.services.IndexServiceImpl;
import ic.unicamp.fb.graph.neo4j.services.FragmentService;
import ic.unicamp.fb.graph.neo4j.services.FragmentServiceImpl;
import picocli.CommandLine;
import picocli.CommandLine.Parameters;

import java.util.ArrayList;
import java.util.List;

//ok
@CommandLine.Command(name = FBMapFragmentToIndex.CMD_NAME)
public class FBMapFragmentToIndex implements Runnable {

    public static final String CMD_NAME = "map-frag-to-order";

    @Parameters(index = "0", description = "fragment Id", defaultValue = "")
    String fragmentId;

    @Parameters(index = "1..*")
    String[] indexList;

    @Override
    public void run() {
        FragmentService fragmentService = new FragmentServiceImpl();
        Fragment fullFragment = fragmentService.getFragmentByID(fragmentId);
        if (fullFragment == null) {
            System.out.println("No Fragment with the given ID could be found");
            return;
        }
        if (indexList == null) {
            System.out.println("This command requires bit order ids");
            return;
        }

        IndexService indexService = new IndexServiceImpl();
        List<FragmentToIndex> relations = new ArrayList<>();
        for (String indexId : indexList) {
            Index fullIndex = indexService.getIndexByID(indexId);
            if (fullIndex == null) {
                String message = String.format("No Bit Order with the given ID (%s) could be found", indexId);
                System.out.println(message);
            } else {
                FragmentToIndex relation = new FragmentToIndex();
                relation.setStartFragment(fullFragment);
                relation.setEndIndex(fullIndex);
                relations.add(relation);
            }
        }
        fullFragment.setAssociatedTo(relations);
        fragmentService.createOrUpdate(fullFragment);
    }
}
