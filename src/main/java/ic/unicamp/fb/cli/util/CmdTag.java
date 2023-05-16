package ic.unicamp.fb.cli.util;


import ic.unicamp.fb.cli.cmd.FBAdd;
import ic.unicamp.fb.cli.cmd.FBAnalyze;
import ic.unicamp.fb.cli.cmd.FBCommit;
import ic.unicamp.fb.cli.cmd.FBConfigure;
import ic.unicamp.fb.cli.cmd.FBExit;
import ic.unicamp.fb.cli.cmd.FBGenerateCuts;
import ic.unicamp.fb.cli.cmd.FBInspectFiles;
import ic.unicamp.fb.cli.cmd.FBListBitOrders;
import ic.unicamp.fb.cli.cmd.FBListBlocks;
import ic.unicamp.fb.cli.cmd.FBListFeatures;
import ic.unicamp.fb.cli.cmd.FBListFiles;
import ic.unicamp.fb.cli.cmd.FBListFragments;
import ic.unicamp.fb.cli.cmd.FBListProducts;
import ic.unicamp.fb.cli.cmd.FBMapFragmentToBitOrder;
import ic.unicamp.fb.cli.cmd.FBMoveBlocks;
import ic.unicamp.fb.cli.cmd.FBProjectProduct;
import ic.unicamp.fb.cli.cmd.FBRemoveFeatures;
import ic.unicamp.fb.cli.cmd.FBRemoveFragments;
import ic.unicamp.fb.cli.cmd.FBRemoveProducts;
import ic.unicamp.fb.cli.cmd.FBSync;
import ic.unicamp.fb.cli.cmd.FBTagBlocks;
import ic.unicamp.fb.cli.cmd.FBUpsertBlockOut;
import ic.unicamp.fb.cli.cmd.FBUpsertFeature;
import ic.unicamp.fb.cli.cmd.FBUpsertFragment;
import ic.unicamp.fb.cli.cmd.FBUpsertProduct;
import ic.unicamp.fb.cli.cmd.FBVersion;

public interface CmdTag {

    // basic
    String CMD_EXIT = FBExit.CMD_NAME;
    //String CMD_INIT = FBInit.CMD_NAME;
    String CMD_VERSION = FBVersion.CMD_NAME;
    //String CMD_SP = BMSB.CMD_NAME;
    String CMD_CONFIGURE = FBConfigure.CMD_NAME;
    String CMD_ANALYSE = FBAnalyze.CMD_NAME;

    String CMD_ADD = FBAdd.CMD_NAME;
    String CMD_COMMIT = FBCommit.CMD_NAME;
    String CMD_UPSERT_PRODUCT = FBUpsertProduct.CMD_NAME;
    String CMD_UPSERT_FEATURES = FBUpsertFeature.CMD_NAME;

    String CMD_UPSERT_FRAGMENT = FBUpsertFragment.CMD_NAME;
    String CMD_PROJECT_PRODUCT = FBProjectProduct.CMD_NAME;

    String CMD_LIST_PRODUCTS = FBListProducts.CMD_NAME;
    String CMD_LIST_FEATURES = FBListFeatures.CMD_NAME;
    String CMD_LIST_FRAGMENTS = FBListFragments.CMD_NAME;
    String CMD_LIST_BLOCKS = FBListBlocks.CMD_NAME;
    String CMD_LIST_FILES = FBListFiles.CMD_NAME;
    String CMD_SYNC = FBSync.CMD_NAME;
    String CMD_MOVE_BLOCKS = FBMoveBlocks.CMD_NAME;

    String CMD_TAG_BLOCKS = FBTagBlocks.CMD_NAME;

    String CMD_MAP_FRAGMENTS = FBMapFragmentToBitOrder.CMD_NAME;
    String CMD_INSPECT_FILES = FBInspectFiles.CMD_NAME;
    String CMD_UPSERT_BLOCK_OUT = FBUpsertBlockOut.CMD_NAME;

    String CMD_REMOVE_FEATURES = FBRemoveFeatures.CMD_NAME;
    String CMD_REMOVE_PRODUCTS = FBRemoveProducts.CMD_NAME;
    String CMD_REMOVE_FRAGMENTS = FBRemoveFragments.CMD_NAME;
    String CMD_LIST_BIT_ORDERS = FBListBitOrders.CMD_NAME;

    String CMD_GENERATE_CUTS = FBGenerateCuts.CMD_NAME;

}
