package ic.unicamp.fb.cli.util;


import ic.unicamp.fb.cli.cmd.BMAdd;
import ic.unicamp.fb.cli.cmd.BMAnalyze;
import ic.unicamp.fb.cli.cmd.BMCommit;
import ic.unicamp.fb.cli.cmd.BMConfigure;
import ic.unicamp.fb.cli.cmd.BMExit;
import ic.unicamp.fb.cli.cmd.BMLinkFragments;
import ic.unicamp.fb.cli.cmd.BMListBlocks;
import ic.unicamp.fb.cli.cmd.BMListFeatures;
import ic.unicamp.fb.cli.cmd.BMListFragments;
import ic.unicamp.fb.cli.cmd.BMListProducts;
import ic.unicamp.fb.cli.cmd.BMMoveBlocks;
import ic.unicamp.fb.cli.cmd.BMProjectProduct;
import ic.unicamp.fb.cli.cmd.BMSync;
import ic.unicamp.fb.cli.cmd.BMTagBlocks;
import ic.unicamp.fb.cli.cmd.BMUpsertFeature;
import ic.unicamp.fb.cli.cmd.BMUpsertFragment;
import ic.unicamp.fb.cli.cmd.BMUpsertProduct;
import ic.unicamp.fb.cli.cmd.BMVersion;

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
    String CMD_UPSERT_FEATURES = BMUpsertFeature.CMD_NAME;

    String CMD_UPSERT_FRAGMENT = BMUpsertFragment.CMD_NAME;
    String CMD_PROJECT_PRODUCT = BMProjectProduct.CMD_NAME;

    String CMD_LIST_PRODUCTS = BMListProducts.CMD_NAME;
    String CMD_LIST_FEATURES = BMListFeatures.CMD_NAME;
    String CMD_LIST_FRAGMENTS = BMListFragments.CMD_NAME;
    String CMD_LIST_BLOCKS = BMListBlocks.CMD_NAME;
    String CMD_SYNC = BMSync.CMD_NAME;
    String CMD_MOVE_BLOCKS = BMMoveBlocks.CMD_NAME;

    String CMD_TAG_BLOCKS = BMTagBlocks.CMD_NAME;

    String CMD_LINK_FRAGMENTS = BMLinkFragments.CMD_NAME;
}
