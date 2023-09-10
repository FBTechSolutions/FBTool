package ic.unicamp.fb.cli;

import com.github.lalyos.jfiglet.FigletFont;
import ic.unicamp.fb.cli.cmd.FBAdd;
import ic.unicamp.fb.cli.cmd.FBAnalyze;
import ic.unicamp.fb.cli.cmd.FBCheckAnd;
import ic.unicamp.fb.cli.cmd.FBCleanDBBlocks;
import ic.unicamp.fb.cli.cmd.FBCommit;
import ic.unicamp.fb.cli.cmd.FBConfigure;
import ic.unicamp.fb.cli.cmd.FBDBConnection;
import ic.unicamp.fb.cli.cmd.FBExit;
import ic.unicamp.fb.cli.cmd.FBGenerateCuts;
import ic.unicamp.fb.cli.cmd.FBInspectFiles;
import ic.unicamp.fb.cli.cmd.FBListIndexes;
import ic.unicamp.fb.cli.cmd.FBListBlocks;
import ic.unicamp.fb.cli.cmd.FBListFeatures;
import ic.unicamp.fb.cli.cmd.FBListFiles;
import ic.unicamp.fb.cli.cmd.FBListFragments;
import ic.unicamp.fb.cli.cmd.FBListProducts;
import ic.unicamp.fb.cli.cmd.FBAssociateFragToIndex;
import ic.unicamp.fb.cli.cmd.FBMoveBlocks;
import ic.unicamp.fb.cli.cmd.FBProjectFeatures;
import ic.unicamp.fb.cli.cmd.FBProjectProduct;
import ic.unicamp.fb.cli.cmd.FBProjectSPL;
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
import ic.unicamp.fb.cli.util.logger.SplMgrLogger;
import picocli.CommandLine;
import picocli.CommandLine.Command;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

import static ic.unicamp.fb.cli.util.CmdTag.CMD_ADD;
import static ic.unicamp.fb.cli.util.CmdTag.CMD_ANALYSE;
import static ic.unicamp.fb.cli.util.CmdTag.CMD_CHECK_AND;
import static ic.unicamp.fb.cli.util.CmdTag.CMD_CLEAN_DB_BLOCKS;
import static ic.unicamp.fb.cli.util.CmdTag.CMD_COMMIT;
import static ic.unicamp.fb.cli.util.CmdTag.CMD_CONFIGURE;
import static ic.unicamp.fb.cli.util.CmdTag.CMD_DB_CONNECTION;
import static ic.unicamp.fb.cli.util.CmdTag.CMD_EXIT;
import static ic.unicamp.fb.cli.util.CmdTag.CMD_GENERATE_CUTS;
import static ic.unicamp.fb.cli.util.CmdTag.CMD_INSPECT_FILES;
import static ic.unicamp.fb.cli.util.CmdTag.CMD_LIST_OF_INDEXES;
import static ic.unicamp.fb.cli.util.CmdTag.CMD_LIST_BLOCKS;
import static ic.unicamp.fb.cli.util.CmdTag.CMD_LIST_FEATURES;
import static ic.unicamp.fb.cli.util.CmdTag.CMD_LIST_FILES;
import static ic.unicamp.fb.cli.util.CmdTag.CMD_LIST_FRAGMENTS;
import static ic.unicamp.fb.cli.util.CmdTag.CMD_LIST_PRODUCTS;
import static ic.unicamp.fb.cli.util.CmdTag.CMD_MAP_FRAGMENTS;
import static ic.unicamp.fb.cli.util.CmdTag.CMD_MOVE_BLOCKS;
import static ic.unicamp.fb.cli.util.CmdTag.CMD_PROJECT_FEATURES;
import static ic.unicamp.fb.cli.util.CmdTag.CMD_PROJECT_PRODUCT;
import static ic.unicamp.fb.cli.util.CmdTag.CMD_PROJECT_SPL;
import static ic.unicamp.fb.cli.util.CmdTag.CMD_REMOVE_FEATURES;
import static ic.unicamp.fb.cli.util.CmdTag.CMD_REMOVE_FRAGMENTS;
import static ic.unicamp.fb.cli.util.CmdTag.CMD_REMOVE_PRODUCTS;
import static ic.unicamp.fb.cli.util.CmdTag.CMD_SYNC;
import static ic.unicamp.fb.cli.util.CmdTag.CMD_TAG_BLOCKS;
import static ic.unicamp.fb.cli.util.CmdTag.CMD_UPSERT_BLOCK_OUT;
import static ic.unicamp.fb.cli.util.CmdTag.CMD_UPSERT_FEATURES;
import static ic.unicamp.fb.cli.util.CmdTag.CMD_UPSERT_FRAGMENT;
import static ic.unicamp.fb.cli.util.CmdTag.CMD_UPSERT_PRODUCT;
import static ic.unicamp.fb.cli.util.CmdTag.CMD_VERSION;
import static ic.unicamp.fb.cli.util.msg.InfoMessages.INF_0__CMD_ACCEPTED;
import static ic.unicamp.fb.cli.util.msg.InfoMessages.INF_0__CMD_END;
import static ic.unicamp.fb.cli.util.msg.InfoMessages.INF_0__CMD_NOT_VALID;
import static ic.unicamp.fb.cli.util.msg.InfoMessages.INF_0__FB_ASCII;
import static ic.unicamp.fb.cli.util.msg.InfoMessages.INF_0__PROMPT;
import static ic.unicamp.fb.cli.util.msg.InfoMessages.INF_0__SPLM_AUTHOR;
import static ic.unicamp.fb.cli.util.msg.InfoMessages.INF_0__SPLM_VERSION;
import static ic.unicamp.fb.cli.util.msg.InfoMessages.INF_0__WELCOME_SPLM;


@Command(
        name = "(fb)",
        header = {"Thanks for choosing the FB Tool.", ""},
        description = {"Description: This tool internally use git to stored the block data and a graph db to manage logic and relationships", ""},
        subcommands = {
                // basic
//                FBInit.class,
//                FBExit.class,
                FBVersion.class,
                // others
                FBConfigure.class,
                FBAnalyze.class,
                FBProjectProduct.class,
                FBProjectFeatures.class,
                FBUpsertProduct.class,
                FBUpsertFeature.class,
                FBUpsertFragment.class,
                FBListProducts.class,
                FBListFeatures.class,
                FBListFragments.class,
                FBListBlocks.class,
                FBListFiles.class,
                FBSync.class,
                FBMoveBlocks.class,
                FBTagBlocks.class,
                FBAssociateFragToIndex.class,
                FBInspectFiles.class,
                FBUpsertBlockOut.class,
                FBRemoveFeatures.class,
                FBRemoveProducts.class,
                FBRemoveFragments.class,
                FBGenerateCuts.class,
                FBCleanDBBlocks.class,
                FBCheckAnd.class,
                FBDBConnection.class,
                FBProjectSPL.class},
        footer = {"", "launching prompt ...", ""})
public class Cmd implements Runnable {

    @Override
    public void run() {
        __showToolSignature();
        __runDefaultCommands();
        __runSplmPrompt();
    }

    private void __showToolSignature() {
        String asciiArt = null;
        try {
            asciiArt = FigletFont.convertOneLine(INF_0__FB_ASCII);
        } catch (IOException e) {
            e.printStackTrace();
        }

        SplMgrLogger.message(asciiArt, true);
        SplMgrLogger.info(INF_0__SPLM_VERSION, true);
        SplMgrLogger.info(INF_0__SPLM_AUTHOR, true);
        SplMgrLogger.message_ln(INF_0__WELCOME_SPLM, false);
    }

    private void __runDefaultCommands() {

    }

    private void __runSplmPrompt() {
        boolean alive = true;
        Scanner sc = new Scanner(System.in);
        while (alive) {
            __printPrompt();
            String line = sc.nextLine();
            List<String> inputs = new LinkedList<>(Arrays.asList(line.split(" ")));
            if (!line.isEmpty()) {
                String command = inputs.get(0);
                switch (command) {
                    // basic
                    case CMD_EXIT -> {
                        alive = false;
                        CommandLine commandLine = new CommandLine(new FBExit());
                        __executeCmd(inputs, commandLine);
                    }
                    case CMD_VERSION -> {
                        CommandLine commandLine = new CommandLine(new FBVersion());
                        __executeCmd(inputs, commandLine);
                    }
                    //
                    case CMD_CONFIGURE -> {
                        CommandLine commandLine = new CommandLine(new FBConfigure());
                        __executeCmd(inputs, commandLine);
                    }
                    case CMD_ANALYSE -> {
                        CommandLine commandLine = new CommandLine(new FBAnalyze());
                        __executeCmd(inputs, commandLine);
                    }
                    case CMD_ADD -> {
                        CommandLine commandLine = new CommandLine(new FBAdd());
                        __executeCmd(inputs, commandLine);
                    }
                    case CMD_COMMIT -> {
                        CommandLine commandLine = new CommandLine(new FBCommit());
                        __executeCmd(inputs, commandLine);
                    }
                    case CMD_UPSERT_PRODUCT -> {
                        CommandLine commandLine = new CommandLine(new FBUpsertProduct());
                        __executeCmd(inputs, commandLine);
                    }
                    case CMD_UPSERT_FEATURES -> {
                        CommandLine commandLine = new CommandLine(new FBUpsertFeature());
                        __executeCmd(inputs, commandLine);
                    }
                    case CMD_UPSERT_FRAGMENT -> {
                        CommandLine commandLine = new CommandLine(new FBUpsertFragment());
                        __executeCmd(inputs, commandLine);
                    }
                    case CMD_PROJECT_PRODUCT -> {
                        CommandLine commandLine = new CommandLine(new FBProjectProduct());
                        __executeCmd(inputs, commandLine);
                    }
                    case CMD_PROJECT_FEATURES -> {
                        CommandLine commandLine = new CommandLine(new FBProjectFeatures());
                        __executeCmd(inputs, commandLine);
                    }
                    case CMD_LIST_PRODUCTS -> {
                        CommandLine commandLine = new CommandLine(new FBListProducts());
                        __executeCmd(inputs, commandLine);
                    }
                    case CMD_LIST_FEATURES -> {
                        CommandLine commandLine = new CommandLine(new FBListFeatures());
                        __executeCmd(inputs, commandLine);
                    }
                    case CMD_LIST_FRAGMENTS -> {
                        CommandLine commandLine = new CommandLine(new FBListFragments());
                        __executeCmd(inputs, commandLine);
                    }
                    case CMD_LIST_BLOCKS -> {
                        CommandLine commandLine = new CommandLine(new FBListBlocks());
                        __executeCmd(inputs, commandLine);
                    }
                    case CMD_LIST_FILES -> {
                        CommandLine commandLine = new CommandLine(new FBListFiles());
                        __executeCmd(inputs, commandLine);
                    }
                    case CMD_LIST_OF_INDEXES -> {
                        CommandLine commandLine = new CommandLine(new FBListIndexes());
                        __executeCmd(inputs, commandLine);
                    }
                    case CMD_SYNC -> {
                        CommandLine commandLine = new CommandLine(new FBSync());
                        __executeCmd(inputs, commandLine);
                    }
                    case CMD_MOVE_BLOCKS -> {
                        CommandLine commandLine = new CommandLine(new FBMoveBlocks());
                        __executeCmd(inputs, commandLine);
                    }
                    case CMD_TAG_BLOCKS -> {
                        CommandLine commandLine = new CommandLine(new FBTagBlocks());
                        __executeCmd(inputs, commandLine);
                    }
                    case CMD_MAP_FRAGMENTS -> {
                        CommandLine commandLine = new CommandLine(new FBAssociateFragToIndex());
                        __executeCmd(inputs, commandLine);
                    }
                    case CMD_INSPECT_FILES -> {
                        CommandLine commandLine = new CommandLine(new FBInspectFiles());
                        __executeCmd(inputs, commandLine);
                    }
                    case CMD_UPSERT_BLOCK_OUT -> {
                        CommandLine commandLine = new CommandLine(new FBUpsertBlockOut());
                        __executeCmd(inputs, commandLine);
                    }
                    case CMD_REMOVE_FEATURES -> {
                        CommandLine commandLine = new CommandLine(new FBRemoveFeatures());
                        __executeCmd(inputs, commandLine);
                    }
                    case CMD_REMOVE_PRODUCTS -> {
                        CommandLine commandLine = new CommandLine(new FBRemoveProducts());
                        __executeCmd(inputs, commandLine);
                    }
                    case CMD_REMOVE_FRAGMENTS -> {
                        CommandLine commandLine = new CommandLine(new FBRemoveFragments());
                        __executeCmd(inputs, commandLine);
                    }
                    case CMD_GENERATE_CUTS -> {
                        CommandLine commandLine = new CommandLine(new FBGenerateCuts());
                        __executeCmd(inputs, commandLine);
                    }
                    case CMD_CLEAN_DB_BLOCKS -> {
                        CommandLine commandLine = new CommandLine(new FBCleanDBBlocks());
                        __executeCmd(inputs, commandLine);
                    }
                    case CMD_CHECK_AND -> {
                        CommandLine commandLine = new CommandLine(new FBCheckAnd());
                        __executeCmd(inputs, commandLine);
                    }
                    case CMD_DB_CONNECTION -> {
                        CommandLine commandLine = new CommandLine(new FBDBConnection());
                        __executeCmd(inputs, commandLine);
                    }
                    case CMD_PROJECT_SPL -> {
                        CommandLine commandLine = new CommandLine(new FBProjectSPL());
                        __executeCmd(inputs, commandLine);
                    }
                    default -> __printCmdNotValid();
                }
            }
        }
    }

    private void __executeCmd(List<String> inputs, CommandLine command) {
        __printCmdValid();
        if (command != null) {
            inputs.remove(0); // remove the command from the arguments
            if (inputs.isEmpty()) {
                command.execute();
            } else {
                String[] new_args = inputs.toArray(new String[0]);
                command.execute(new_args); // send arguments
            }
        }
        __printCmdEnd();
    }

    private void __printCmdValid() {
        SplMgrLogger.message_ln(INF_0__CMD_ACCEPTED, false);
    }

    private void __printCmdEnd() {
        SplMgrLogger.message_ln(INF_0__CMD_END, false);
    }

    private void __printCmdNotValid() {
        SplMgrLogger.message_ln(INF_0__CMD_NOT_VALID, false);
    }

    private void __printPrompt() {
        SplMgrLogger.message(INF_0__PROMPT, false);
    }

}
