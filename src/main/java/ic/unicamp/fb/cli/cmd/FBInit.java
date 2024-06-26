package ic.unicamp.fb.cli.cmd;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(
        name = FBInit.CMD_NAME,
        description = "Deprecated Or to Think")
public class FBInit implements Runnable {

    public static final String ROOT_FEATURE_NAME = "all";
    public static final String ROOT_PRODUCT_NAME = "All";

    public static final String CMD_NAME = "init";

    @Option(
            names = "-fn",
            description =
                    "The 'init -fn' names the default feature (first feature)")
    private String featureName = ROOT_FEATURE_NAME;

    @Option(
            names = "-pn",
            description =
                    "The 'init -pn' names the default product (first product)")
    private String productName = ROOT_PRODUCT_NAME;

    @Override
    public void run() {
    }
}
