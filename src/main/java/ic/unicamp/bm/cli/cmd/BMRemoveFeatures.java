package ic.unicamp.bm.cli.cmd;

import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

@Command(
    name = BMRemoveFeatures.CMD_NAME,
    description = "This command will subscribe a product in the current Git branch")
public class BMRemoveFeatures implements Runnable {

  public static final String CMD_NAME = "subscribe";

  @Parameters(index = "0")
  String product;

  @Override
  public void run() {
    System.out.println(product);
  }
}
