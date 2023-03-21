package ic.unicamp.bm.cli.cmd;

import static ic.unicamp.bm.cli.util.msg.InfoMessages.INF_0__SPLM_AUTHOR;
import static ic.unicamp.bm.cli.util.msg.InfoMessages.INF_0__SPLM_VERSION;

import ic.unicamp.bm.cli.util.logger.SplMgrLogger;
import picocli.CommandLine;

@CommandLine.Command(name = BMVersion.CMD_NAME)
public class BMVersion implements Runnable {

    public static final String CMD_NAME = "version";

    @Override
    public void run() {
        SplMgrLogger.message_ln(INF_0__SPLM_VERSION, false);
    }
}
