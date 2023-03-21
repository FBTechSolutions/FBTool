package ic.unicamp.bm.cli.cmd;

import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

@Command(
        name = BMClean.CMD_NAME,
        description = "This command will clean the block directives from the current Git branch")
public class BMClean implements Runnable {

    public static final String CMD_NAME = "subscribe";

    @Parameters(index = "0")
    String product;

    @Override
    public void run() {
        System.out.println(product);
    }
}
