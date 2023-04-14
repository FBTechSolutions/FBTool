package ic.unicamp.fb.cli;

import com.github.lalyos.jfiglet.FigletFont;
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
import static ic.unicamp.fb.cli.util.CmdTag.CMD_COMMIT;
import static ic.unicamp.fb.cli.util.CmdTag.CMD_CONFIGURE;
import static ic.unicamp.fb.cli.util.CmdTag.CMD_EXIT;
import static ic.unicamp.fb.cli.util.CmdTag.CMD_LINK_FRAGMENTS;
import static ic.unicamp.fb.cli.util.CmdTag.CMD_LIST_BLOCKS;
import static ic.unicamp.fb.cli.util.CmdTag.CMD_LIST_FEATURES;
import static ic.unicamp.fb.cli.util.CmdTag.CMD_LIST_FRAGMENTS;
import static ic.unicamp.fb.cli.util.CmdTag.CMD_LIST_PRODUCTS;
import static ic.unicamp.fb.cli.util.CmdTag.CMD_MOVE_BLOCKS;
import static ic.unicamp.fb.cli.util.CmdTag.CMD_PROJECT_PRODUCT;
import static ic.unicamp.fb.cli.util.CmdTag.CMD_SYNC;
import static ic.unicamp.fb.cli.util.CmdTag.CMD_TAG_BLOCKS;
import static ic.unicamp.fb.cli.util.CmdTag.CMD_UPSERT_FEATURES;
import static ic.unicamp.fb.cli.util.CmdTag.CMD_UPSERT_FRAGMENT;
import static ic.unicamp.fb.cli.util.CmdTag.CMD_UPSERT_PRODUCT;
import static ic.unicamp.fb.cli.util.CmdTag.CMD_VERSION;
import static ic.unicamp.fb.cli.util.msg.InfoMessages.INF_0__BM_ASCII;
import static ic.unicamp.fb.cli.util.msg.InfoMessages.INF_0__CMD_ACCEPTED;
import static ic.unicamp.fb.cli.util.msg.InfoMessages.INF_0__CMD_END;
import static ic.unicamp.fb.cli.util.msg.InfoMessages.INF_0__CMD_NOT_VALID;
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
//                BMInit.class,
//                BMExit.class,
                BMVersion.class,
                // others
                BMConfigure.class,
                BMAnalyze.class,
                BMProjectProduct.class,
                BMUpsertProduct.class,
                BMUpsertFeature.class,
                BMUpsertFragment.class,
                BMListProducts.class,
                BMListFeatures.class,
                BMListFragments.class,
                BMListBlocks.class,
                BMSync.class,
                BMMoveBlocks.class,
                BMTagBlocks.class,
                BMLinkFragments.class},
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
            asciiArt = FigletFont.convertOneLine(INF_0__BM_ASCII);
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
                        CommandLine commandLine = new CommandLine(new BMExit());
                        __executeCmd(inputs, commandLine);
                    }
                    case CMD_VERSION -> {
                        CommandLine commandLine = new CommandLine(new BMVersion());
                        __executeCmd(inputs, commandLine);
                    }
                    //
                    case CMD_CONFIGURE -> {
                        CommandLine commandLine = new CommandLine(new BMConfigure());
                        __executeCmd(inputs, commandLine);
                    }
                    case CMD_ANALYSE -> {
                        CommandLine commandLine = new CommandLine(new BMAnalyze());
                        __executeCmd(inputs, commandLine);
                    }
                    case CMD_ADD -> {
                        CommandLine commandLine = new CommandLine(new BMAdd());
                        __executeCmd(inputs, commandLine);
                    }
                    case CMD_COMMIT -> {
                        CommandLine commandLine = new CommandLine(new BMCommit());
                        __executeCmd(inputs, commandLine);
                    }
                    case CMD_UPSERT_PRODUCT -> {
                        CommandLine commandLine = new CommandLine(new BMUpsertProduct());
                        __executeCmd(inputs, commandLine);
                    }
                    case CMD_UPSERT_FEATURES -> {
                        CommandLine commandLine = new CommandLine(new BMUpsertFeature());
                        __executeCmd(inputs, commandLine);
                    }
                    case CMD_UPSERT_FRAGMENT -> {
                        CommandLine commandLine = new CommandLine(new BMUpsertFragment());
                        __executeCmd(inputs, commandLine);
                    }
                    case CMD_PROJECT_PRODUCT -> {
                        CommandLine commandLine = new CommandLine(new BMProjectProduct());
                        __executeCmd(inputs, commandLine);
                    }
                    case CMD_LIST_PRODUCTS -> {
                        CommandLine commandLine = new CommandLine(new BMListProducts());
                        __executeCmd(inputs, commandLine);
                    }
                    case CMD_LIST_FEATURES -> {
                        CommandLine commandLine = new CommandLine(new BMListFeatures());
                        __executeCmd(inputs, commandLine);
                    }
                    case CMD_LIST_FRAGMENTS -> {
                        CommandLine commandLine = new CommandLine(new BMListFragments());
                        __executeCmd(inputs, commandLine);
                    }
                    case CMD_LIST_BLOCKS -> {
                        CommandLine commandLine = new CommandLine(new BMListBlocks());
                        __executeCmd(inputs, commandLine);
                    }
                    case CMD_SYNC -> {
                        CommandLine commandLine = new CommandLine(new BMSync());
                        __executeCmd(inputs, commandLine);
                    }
                    case CMD_MOVE_BLOCKS -> {
                        CommandLine commandLine = new CommandLine(new BMMoveBlocks());
                        __executeCmd(inputs, commandLine);
                    }
                    case CMD_TAG_BLOCKS -> {
                        CommandLine commandLine = new CommandLine(new BMTagBlocks());
                        __executeCmd(inputs, commandLine);
                    }
                    case CMD_LINK_FRAGMENTS -> {
                        CommandLine commandLine = new CommandLine(new BMLinkFragments());
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
