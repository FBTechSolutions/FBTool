package ic.unicamp.fb.cli.cmd;

import ic.unicamp.fb.cli.util.logger.SplMgrLogger;
import picocli.CommandLine;

import static ic.unicamp.fb.cli.util.msg.InfoMessages.INF_0__SPLM_VERSION;

@CommandLine.Command(
        name = BMVersion.CMD_NAME,
        description = "It will show you the version of the tool")
public class BMVersion implements Runnable {

    public static final String CMD_NAME = "version";

    @Override
    public void run() {
        SplMgrLogger.message_ln(INF_0__SPLM_VERSION, false);
    }
}
