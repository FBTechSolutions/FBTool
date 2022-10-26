package ic.unicamp.bm.cli.cmd;

import ic.unicamp.bm.graph.neo4j.schema.Feature;
import ic.unicamp.bm.graph.neo4j.services.FeatureService;
import ic.unicamp.bm.graph.neo4j.services.FeatureServiceImpl;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

//ok
@Command(
    name = BMUpsertFeature.CMD_NAME,
    description = "This command will update or create a Feature")
public class BMUpsertFeature implements Runnable {

  public static final String CMD_NAME = "upsert-feature";

  @Parameters(index = "0", description = "featureId (required)", defaultValue = "")
  String featureId;

  @Parameters(index = "1..*")
  String[] labelInParts;

  @Override
  public void run() {
    String featureLabel = retrieveLabel();
    FeatureService featureService = new FeatureServiceImpl();
    Feature feature = featureService.getFeatureByID(featureId);
    if (feature == null) {
      feature = new Feature();
      feature.setFeatureId(featureId);
    }
    feature.setFeatureLabel(featureLabel);
    featureService.createOrUpdate(feature);
  }

  private String retrieveLabel() {
    String featureLabel = "";
    if (labelInParts != null) {
      StringBuilder sb = new StringBuilder();
      for (String s : labelInParts) {
        sb.append(s);
      }
      featureLabel = sb.toString();
    }
    return featureLabel;
  }
}
