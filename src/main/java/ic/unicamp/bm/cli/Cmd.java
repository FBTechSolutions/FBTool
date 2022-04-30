package ic.unicamp.bm.cli;

import com.github.lalyos.jfiglet.FigletFont;
import ic.unicamp.bm.cli.cmd.BMConfigure;
import ic.unicamp.bm.cli.cmd.BMExit;
import ic.unicamp.bm.cli.cmd.BMInit;
import ic.unicamp.bm.cli.cmd.BMSB;
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
                BMConfigure.class
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
                    case CMD_INIT -> {
                        CommandLine commandLine = new CommandLine(new BMInit());
                        __executeCmd(inputs, commandLine);
                    }
                    case CMD_VERSION -> {
                        CommandLine commandLine = new CommandLine(new BMVersion());
                        __executeCmd(inputs, commandLine);
                    }
                    case CMD_SP -> {
                        CommandLine commandLine = new CommandLine(new BMSB());
                        __executeCmd(inputs, commandLine);
                    }
                    //
                    case CMD_CONFIGURE -> {
                        CommandLine commandLine = new CommandLine(new BMConfigure());
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
