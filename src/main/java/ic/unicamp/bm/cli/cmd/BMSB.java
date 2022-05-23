package ic.unicamp.bm.cli.cmd;

import picocli.CommandLine.Parameters;
import picocli.CommandLine.Command;

@Command(name = BMSB.CMD_NAME)
public class BMSB implements Runnable {

  public static final String CMD_NAME = "sb";

  @Parameters(index = "0", description = "The starting branch")
  private String branchName;

  @Override
  public void run() {
    //in memory
    System.out.println(branchName);
  }
}
