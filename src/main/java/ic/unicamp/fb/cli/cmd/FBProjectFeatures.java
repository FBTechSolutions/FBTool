package ic.unicamp.fb.cli.cmd;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

@Command(
        name = FBProjectFeatures.CMD_NAME,
        description = "It will project the source code related to a list of features.")
public class FBProjectFeatures implements Runnable {

    public static final String CMD_NAME = "project-features";

    @Option(names = "-clean")
    boolean clean;

    @Parameters(index = "0..*")
    String[] features;

    @Override
    public void run() {
        System.out.println("Option clean " + clean);
        System.out.println("List of features:");
        for (String feature : features) {
            System.out.println(feature);
        }
    }
}
