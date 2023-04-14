package ic.unicamp.fb.cli.cmd;

import ic.unicamp.fb.graph.neo4j.schema.Feature;
import ic.unicamp.fb.graph.neo4j.schema.Fragment;
import ic.unicamp.fb.graph.neo4j.schema.relations.FeatureToFragment;
import ic.unicamp.fb.graph.neo4j.services.FeatureService;
import ic.unicamp.fb.graph.neo4j.services.FeatureServiceImpl;
import ic.unicamp.fb.graph.neo4j.services.FragmentService;
import ic.unicamp.fb.graph.neo4j.services.FragmentServiceImpl;
import picocli.CommandLine;
import picocli.CommandLine.Parameters;

import java.util.ArrayList;
import java.util.List;

//ok
@CommandLine.Command(name = BMMapFragments.CMD_NAME)
public class BMMapFragments implements Runnable {

    public static final String CMD_NAME = "map-fragments";

    @Parameters(index = "0", description = "feature Id", defaultValue = "")
    String featureId;

    @Parameters(index = "1..*")
    String[] fragmentList;

    @Override
    public void run() {
        FeatureService featureService = new FeatureServiceImpl();
        Feature fullFeature = featureService.getFeatureByID(featureId);
        if (fullFeature == null) {
            System.out.println("No Feature with the given ID could be found");
            return;
        }
        if (fragmentList == null) {
            System.out.println("This command requires block ids");
            return;
        }

        FragmentService fragmentService = new FragmentServiceImpl();
        List<FeatureToFragment> relations = new ArrayList<>();
        for (String fragmentId : fragmentList) {
            Fragment fullFragment = fragmentService.getFragmentByID(fragmentId);
            if (fullFragment == null) {
                String message = String.format("No Fragment with the given ID (%s) could be found", fragmentId);
                System.out.println(message);
            } else {
                FeatureToFragment relation = new FeatureToFragment();
                relation.setStartFeature(fullFeature);
                relation.setEndFragment(fullFragment);
                relations.add(relation);
            }
        }
        fullFeature.setAssociatedTo(relations);
        featureService.createOrUpdate(fullFeature);
    }
}
