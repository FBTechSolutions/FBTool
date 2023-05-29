package ic.unicamp.fb.cli.cmd;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

@Command(
        name = FBRestore.CMD_NAME,
        description = "This command will restore a list of blocks")
public class FBRestore implements Runnable {

    public static final String CMD_NAME = "restore";

    @Option(names = "-clean")
    boolean clean;

    @Parameters(index = "0..*")
    String[] products;

    @Override
    public void run() {
        System.out.println("Option clean " + clean);
        System.out.println("List of products:");
        for (String feature : products) {
            System.out.println(feature);
        }
    }
}