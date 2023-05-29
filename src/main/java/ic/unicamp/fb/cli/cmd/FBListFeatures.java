package ic.unicamp.fb.cli.cmd;

import ic.unicamp.fb.graph.neo4j.schema.BitOrder;
import ic.unicamp.fb.graph.neo4j.schema.Feature;
import ic.unicamp.fb.graph.neo4j.services.BitOrderService;
import ic.unicamp.fb.graph.neo4j.services.BitOrderServiceImpl;
import ic.unicamp.fb.graph.neo4j.services.FeatureService;
import ic.unicamp.fb.graph.neo4j.services.FeatureServiceImpl;
import picocli.CommandLine;
import picocli.CommandLine.Command;

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
        BitOrderService bitOrderService = new BitOrderServiceImpl();
        for (Feature feature : featureService.findAll()) {
            String message = String.format("- id:%s label:%s", feature.getFeatureId(),
                    feature.getFeatureLabel());
            System.out.println(message);

            if (isOrderEnabled) {
                BitOrder bitOrder = bitOrderService.getBitOrderByFeature(feature.getFeatureId());
                System.out.printf("  - bit:%s %n", bitOrder.getBitOrderId());
            }
        }
    }
}
