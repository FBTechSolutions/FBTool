package ic.unicamp.fb.cli.cmd;

import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

@Command(
        name = FBClean.CMD_NAME,
        description = "This command will clean the block directives from the current Git branch")
public class FBClean implements Runnable {

    public static final String CMD_NAME = "subscribe";

    @Parameters(index = "0")
    String product;

    @Override
    public void run() {
        System.out.println("Unsupported command");
    }
}
