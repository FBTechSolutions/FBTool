package ic.unicamp.bm.cli.cmd;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(
    name = BMDetect.CMD_NAME,
    description = "This command will inform whether exits modifications in the blocks using as reference the DB")
public class BMDetect implements Runnable {


  public static final String CMD_NAME = "detect";

  @Override
  public void run() {
    //from first commit to last commit differences

    //read files
    //read blocks tags
    //compare blocks with db
    //show review of blocks
  }
}
