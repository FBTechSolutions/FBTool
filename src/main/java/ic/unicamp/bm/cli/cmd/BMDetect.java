package ic.unicamp.bm.cli.cmd;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(
    name = BMDetect.CMD_NAME,
    description = "This command will create the initial blocks")
public class BMDetect implements Runnable {


  public static final String CMD_NAME = "detect";

  @Override
  public void run() {
    //read files
    //read blocks
    //compare blocks with db
    //show review of blocks
  }
}
