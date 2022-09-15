package ic.unicamp.bm.cli.util;


import ic.unicamp.bm.cli.cmd.BMAdd;
import ic.unicamp.bm.cli.cmd.BMAnalyze;
import ic.unicamp.bm.cli.cmd.BMCommit;
import ic.unicamp.bm.cli.cmd.BMConfigure;
import ic.unicamp.bm.cli.cmd.BMExit;
import ic.unicamp.bm.cli.cmd.BMListBlocks;
import ic.unicamp.bm.cli.cmd.BMListFeatures;
import ic.unicamp.bm.cli.cmd.BMProjectProduct;
import ic.unicamp.bm.cli.cmd.BMSync;
import ic.unicamp.bm.cli.cmd.BMTagBlocks;
import ic.unicamp.bm.cli.cmd.BMUpsertFeatures;
import ic.unicamp.bm.cli.cmd.BMUpsertProduct;
import ic.unicamp.bm.cli.cmd.BMVersion;

public interface CmdTag {

  // basic
  String CMD_EXIT = BMExit.CMD_NAME;
  //String CMD_INIT = BMInit.CMD_NAME;
  String CMD_VERSION = BMVersion.CMD_NAME;
  //String CMD_SP = BMSB.CMD_NAME;
  String CMD_CONFIGURE = BMConfigure.CMD_NAME;
  String CMD_ANALYSE = BMAnalyze.CMD_NAME;

  String CMD_ADD = BMAdd.CMD_NAME;
  String CMD_COMMIT = BMCommit.CMD_NAME;
  String CMD_UPSERT_PRODUCT = BMUpsertProduct.CMD_NAME;
  String CMD_UPSERT_FEATURES = BMUpsertFeatures.CMD_NAME;
  String CMD_PROJECT_PRODUCT = BMProjectProduct.CMD_NAME;
  String CMD_TAGBLOCKS = BMTagBlocks.CMD_NAME;
  String CMD_LIST_FEATURES = BMListFeatures.CMD_NAME;
  String CMD_LIST_BLOCKS = BMListBlocks.CMD_NAME;
  String CMD_SYNC = BMSync.CMD_NAME;
}
