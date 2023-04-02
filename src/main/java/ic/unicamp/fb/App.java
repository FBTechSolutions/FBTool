package ic.unicamp.fb;

import ic.unicamp.fb.cli.Cmd;
import picocli.CommandLine;

/**
 * The main class that starts the application.
 */
public class App {

    /**
     * The main method that receives command line arguments and processes them.
     *
     * @param args The command line arguments.
     */
    public static void main(String[] args) {
        Cmd cmd = new Cmd();
        CommandLine cmdLine = new CommandLine(cmd);
        cmdLine.setUsageHelpWidth(80);
        cmdLine.usage(System.out);
        cmdLine.execute(args);
    }
}
