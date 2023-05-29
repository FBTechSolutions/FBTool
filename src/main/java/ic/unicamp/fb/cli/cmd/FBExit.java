package ic.unicamp.fb.cli.cmd;

import ic.unicamp.fb.cli.util.logger.SplMgrLogger;
import picocli.CommandLine.Command;

import static ic.unicamp.fb.cli.util.msg.InfoMessages.INF_0__ENDING_FROM_PROMPT;


@Command(
        name = FBExit.CMD_NAME,
        description = "Deprecated Or to Think")
public class FBExit implements Runnable {

    public static final String CMD_NAME = "exit";

    @Override
    public void run() {
        SplMgrLogger.info(INF_0__ENDING_FROM_PROMPT, true);
    }
}
