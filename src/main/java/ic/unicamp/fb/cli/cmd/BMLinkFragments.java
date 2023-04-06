package ic.unicamp.fb.cli.cmd;

import ic.unicamp.fb.graph.neo4j.schema.Feature;
import ic.unicamp.fb.graph.neo4j.schema.Fragment;
import ic.unicamp.fb.graph.neo4j.schema.relations.BlockToFragment;
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
@CommandLine.Command(name = BMLinkFragments.CMD_NAME)
public class BMLinkFragments implements Runnable {

    public static final String CMD_NAME = "link-fragments";

    @Parameters(index = "0", description = "feature Id", defaultValue = "")
    String featureId;

    @Parameters(index = "1..*")
    String[] fragmentList;

    @Override
    public void run() {
        FeatureService featureService = new FeatureServiceImpl();
        Feature feature = featureService.getFeatureByID(featureId);
        if (feature == null) {
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
            Fragment fragment = fragmentService.getFragmentByID(fragmentId);
            if (fragment == null) {
                String message = String.format("No Fragment with the given ID (%s) could be found", fragmentId);
                System.out.println(message);
            } else {
                FeatureToFragment relation = new FeatureToFragment();
                relation.setStartFeature(feature);
                relation.setEndFragment(fragment);
                relations.add(relation);
            }
        }
        feature.setAssociatedTo(relations);
        featureService.createOrUpdate(feature);
    }
}
