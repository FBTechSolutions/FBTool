package ic.unicamp.fb.cli.cmd;

import ic.unicamp.fb.graph.neo4j.schema.Feature;
import ic.unicamp.fb.graph.neo4j.services.FeatureService;
import ic.unicamp.fb.graph.neo4j.services.FeatureServiceImpl;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

@Command(
        name = FBRemoveFeatures.CMD_NAME,
        description = "This command will remove a list of features")
public class FBRemoveFeatures implements Runnable {

    public static final String CMD_NAME = "remove-features";

    @Parameters(index = "0..*")
    String[] featureIds;

    @Override
    public void run() {
        if (featureIds == null) {
            System.out.println("You need to specify at least one Feature Id");
            return;
        }
        FeatureService featureService = new FeatureServiceImpl();
        for (String featureId : featureIds) {
            Feature feature = featureService.getFeatureByID(featureId);
            featureService.delete(feature.getId());
        }
    }
}
