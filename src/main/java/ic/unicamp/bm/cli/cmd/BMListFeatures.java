package ic.unicamp.bm.cli.cmd;

import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

@Command(
    name = BMListFeatures.CMD_NAME,
    description = "This command will subscribe a product in the current Git branch")
public class BMListFeatures implements Runnable {

  public static final String CMD_NAME = "subscribe";

  @Override
  public void run() {
  }
}
