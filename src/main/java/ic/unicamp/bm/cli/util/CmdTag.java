package ic.unicamp.bm.cli.util;


import ic.unicamp.bm.cli.cmd.BMConfigure;
import ic.unicamp.bm.cli.cmd.BMExit;
import ic.unicamp.bm.cli.cmd.BMInit;
import ic.unicamp.bm.cli.cmd.BMSB;
import ic.unicamp.bm.cli.cmd.BMVersion;

public interface CmdTag {
    // basic
    String CMD_EXIT = BMExit.CMD_NAME;
    String CMD_INIT = BMInit.CMD_NAME;
    String CMD_VERSION = BMVersion.CMD_NAME;
    String CMD_SP = BMSB.CMD_NAME;
    String CMD_CONFIGURE = BMConfigure.CMD_NAME;
}
