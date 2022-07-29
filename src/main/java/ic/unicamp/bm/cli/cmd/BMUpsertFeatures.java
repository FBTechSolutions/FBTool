package ic.unicamp.bm.cli.cmd;

import ic.unicamp.bm.graph.neo4j.schema.Feature;
import ic.unicamp.bm.graph.neo4j.services.FeatureService;
import ic.unicamp.bm.graph.neo4j.services.FeatureServiceImpl;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

@Command(
    name = BMUpsertFeatures.CMD_NAME,
    description = "This command will update or create a Feature")
public class BMUpsertFeatures implements Runnable {

  public static final String CMD_NAME = "upsert-feature";

  @Parameters(index = "0")
  String featureId;

  @Parameters(index = "1..*")
  String[] labelInParts;

  @Override
  public void run() {
    System.out.println("Upsert Feature");
    StringBuilder sb = new StringBuilder();
    for (String s : labelInParts) {
      sb.append(s);
    }
    String label = sb.toString();

    FeatureService featureService = new FeatureServiceImpl();
    Feature feature = featureService.getFeatureByID(featureId);
    if (feature == null) {
      feature = new Feature();
      feature.setFeatureId(featureId);
    }
    feature.setFeatureLabel(label);
    featureService.createOrUpdate(feature);
  }
}
