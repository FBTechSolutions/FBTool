package ic.unicamp.fb.cli.cmd;

import ic.unicamp.fb.graph.neo4j.schema.Feature;
import ic.unicamp.fb.graph.neo4j.services.FeatureService;
import ic.unicamp.fb.graph.neo4j.services.FeatureServiceImpl;
import picocli.CommandLine.Command;

@Command(
        name = BMListFeatures.CMD_NAME,
        description = "This command will subscribe a product in the current Git branch")
public class BMListFeatures implements Runnable {

    public static final String CMD_NAME = "list-features";

    @Override
    public void run() {
        FeatureService featureService = new FeatureServiceImpl();
        for (Feature feature : featureService.findAll()) {
            String message = String.format("FeatureId: %s FeatureLabel: %s", feature.getFeatureId(),
                    feature.getFeatureLabel());
            System.out.println(message);
        }
    }
}
