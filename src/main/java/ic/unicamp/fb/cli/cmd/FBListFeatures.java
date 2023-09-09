package ic.unicamp.fb.cli.cmd;

import ic.unicamp.fb.graph.neo4j.schema.Index;
import ic.unicamp.fb.graph.neo4j.schema.Feature;
import ic.unicamp.fb.graph.neo4j.services.IndexService;
import ic.unicamp.fb.graph.neo4j.services.IndexServiceImpl;
import ic.unicamp.fb.graph.neo4j.services.FeatureService;
import ic.unicamp.fb.graph.neo4j.services.FeatureServiceImpl;
import ic.unicamp.fb.graph.neo4j.utils.FeatureUtil;
import picocli.CommandLine;
import picocli.CommandLine.Command;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

@Command(
        name = FBListFeatures.CMD_NAME,
        description = "This command will list all the features")
public class FBListFeatures implements Runnable {

    public static final String CMD_NAME = "list-features";

    @CommandLine.Option(names = {"--o", "--order"}, description = "Add orders to the list")
    private boolean isOrderEnabled;

    @Override
    public void run() {
        System.out.println("Fetching feature list...");
        FeatureService featureService = new FeatureServiceImpl();
        IndexService indexService = new IndexServiceImpl();
        List<Feature> orderedFeatures = FeatureUtil.orderFeatures(featureService.findAll());
        for (Feature feature : orderedFeatures) {
            String message = String.format("- id:%s label:%s", feature.getFeatureId(),
                    feature.getFeatureLabel());
            System.out.println(message);

            if (isOrderEnabled) {
                Index index = indexService.getIndexByFeature(feature.getFeatureId());
                System.out.printf("  - bit:%s %n", index.getIndexId());
            }
        }
    }
}
