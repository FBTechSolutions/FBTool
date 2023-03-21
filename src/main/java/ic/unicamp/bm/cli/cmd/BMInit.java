package ic.unicamp.bm.cli.cmd;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(
        name = BMInit.CMD_NAME,
        description = "This command will create the initial blocks")
public class BMInit implements Runnable {

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
        System.out.println(featureName);
        System.out.println(productName);
    }
}
