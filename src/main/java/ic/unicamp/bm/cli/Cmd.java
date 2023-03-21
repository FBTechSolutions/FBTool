package ic.unicamp.bm.cli;

import com.github.lalyos.jfiglet.FigletFont;
import ic.unicamp.bm.cli.cmd.BMAdd;
import ic.unicamp.bm.cli.cmd.BMAnalyze;
import ic.unicamp.bm.cli.cmd.BMCommit;
import ic.unicamp.bm.cli.cmd.BMConfigure;
import ic.unicamp.bm.cli.cmd.BMExit;
import ic.unicamp.bm.cli.cmd.BMInit;
import ic.unicamp.bm.cli.cmd.BMListBlocks;
import ic.unicamp.bm.cli.cmd.BMListFeatures;
import ic.unicamp.bm.cli.cmd.BMProjectProduct;
import ic.unicamp.bm.cli.cmd.BMMoveBlocks;
import ic.unicamp.bm.cli.cmd.BMSync;
import ic.unicamp.bm.cli.cmd.BMTagBlocks;
import ic.unicamp.bm.cli.cmd.BMUpsertFeature;
import ic.unicamp.bm.cli.cmd.BMUpsertProduct;
import ic.unicamp.bm.cli.cmd.BMVersion;
import ic.unicamp.bm.cli.util.logger.SplMgrLogger;
import picocli.CommandLine;
import picocli.CommandLine.Command;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

import static ic.unicamp.bm.cli.util.CmdTag.*;
import static ic.unicamp.bm.cli.util.msg.InfoMessages.*;


@Command(
        header = {""},
        description = {"", "An Block manager that internally use git", ""},
        subcommands = {
                // basic
                BMInit.class,
                BMExit.class,
                BMVersion.class,
                // others
                BMConfigure.class,
                BMAnalyze.class,
                BMProjectProduct.class,
                BMUpsertProduct.class,
                BMUpsertFeature.class,
                BMListFeatures.class,
                BMListBlocks.class,
                BMSync.class,
                BMMoveBlocks.class

        })
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
                    case CMD_PROJECT_PRODUCT -> {
                        CommandLine commandLine = new CommandLine(new BMProjectProduct());
                        __executeCmd(inputs, commandLine);
                    }
                    case CMD_TAGBLOCKS -> {
                        CommandLine commandLine = new CommandLine(new BMTagBlocks());
                        __executeCmd(inputs, commandLine);
                    }
                    case CMD_LIST_FEATURES -> {
                        CommandLine commandLine = new CommandLine(new BMListFeatures());
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
