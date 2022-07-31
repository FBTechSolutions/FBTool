package ic.unicamp.bm.cli.cmd;

import ic.unicamp.bm.graph.neo4j.schema.Feature;
import ic.unicamp.bm.graph.neo4j.services.FeatureService;
import ic.unicamp.bm.graph.neo4j.services.FeatureServiceImpl;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

@Command(
    name = BMRemoveFeatures.CMD_NAME,
    description = "This command will remove a list of feature")
public class BMRemoveFeatures implements Runnable {

  public static final String CMD_NAME = "remove-features";


  @Parameters(index = "0..*")
  String[] featureIds;

  @Override
  public void run() {
    System.out.println("Remove Features");
    FeatureService featureService = new FeatureServiceImpl();
    for (String featureId : featureIds) {
      Feature feature = featureService.getFeatureByID(featureId);
      featureService.delete(feature.getId());
    }
  }
}