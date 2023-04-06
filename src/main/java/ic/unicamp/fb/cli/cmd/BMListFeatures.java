package ic.unicamp.fb.cli.cmd;

import ic.unicamp.fb.graph.neo4j.schema.Feature;
import ic.unicamp.fb.graph.neo4j.schema.relations.FeatureToFragment;
import ic.unicamp.fb.graph.neo4j.services.FeatureService;
import ic.unicamp.fb.graph.neo4j.services.FeatureServiceImpl;
import picocli.CommandLine;
import picocli.CommandLine.Command;

@Command(
        name = BMListFeatures.CMD_NAME,
        description = "This command will subscribe a product in the current Git branch")
public class BMListFeatures implements Runnable {

    public static final String CMD_NAME = "list-features";

    @CommandLine.Option(names = {"-f", "--fragments"}, description = "Add fragments to the list")
    private boolean isFragmentEnabled;

    @Override
    public void run() {
        System.out.println("Fetching feature list...");
        FeatureService featureService = new FeatureServiceImpl();
        for (Feature feature : featureService.findAll()) {
            String message = String.format("- id:%s label:%s", feature.getFeatureId(),
                    feature.getFeatureLabel());
            System.out.println(message);
            if (isFragmentEnabled) {
                for (FeatureToFragment relation : feature.getAssociatedTo()) {
                    System.out.println("  - " + relation.getEndFragment().getFragmentId());
                }
            }
        }
    }
}
