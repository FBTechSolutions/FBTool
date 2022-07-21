package ic.unicamp.bm.cli.cmd;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

@Command(
    name = BMProjectFeatures.CMD_NAME,
    description = "This command will project the SPL source code related to a list of features.")
public class BMProjectFeatures implements Runnable {

  public static final String CMD_NAME = "project-features";

  @Option(names = "-clean")     boolean clean;

  @Parameters(index = "0..*")
  String[] features;

  @Override
  public void run() {
    System.out.println("Option clean " + clean);
    System.out.println("List of features:");
    for (String feature : features) {
      System.out.println(feature);
    }
  }
}
