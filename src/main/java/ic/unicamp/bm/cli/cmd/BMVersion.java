package ic.unicamp.bm.cli.cmd;

import picocli.CommandLine;

@CommandLine.Command(name = BMVersion.CMD_NAME)
public class BMVersion implements Runnable {
    public static final String CMD_NAME = "version";

    @Override
    public void run() {

    }
}
