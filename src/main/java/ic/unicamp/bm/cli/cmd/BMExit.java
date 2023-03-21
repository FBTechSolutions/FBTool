package ic.unicamp.bm.cli.cmd;

import ic.unicamp.bm.cli.util.logger.SplMgrLogger;
import picocli.CommandLine.Command;

import static ic.unicamp.bm.cli.util.msg.InfoMessages.INF_0__ENDING_FROM_PROMPT;


@Command(name = BMExit.CMD_NAME)
public class BMExit implements Runnable {

    public static final String CMD_NAME = "exit";

    @Override
    public void run() {
        SplMgrLogger.info(INF_0__ENDING_FROM_PROMPT, true);
    }
}
