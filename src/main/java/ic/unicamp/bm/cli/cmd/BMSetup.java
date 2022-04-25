package ic.unicamp.bm.cli.cmd;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(
        name = BMSetup.CMD_NAME,
        description = "This command will create the hidden folder")
public class BMSetup implements Runnable {
    public static final String MASTER_DEFAULT_FEATURE_NAME = "_MDF"; // the default feature
    public static final String MASTER_DEFAULT_PRODUCT_NAME = "_MDP"; // the main product / the master product

    public static final String CMD_NAME = "setup";

    @Option(
            names = "-mdfn",
            description =
                    "The 'init -fn' names the default feature (first feature)")
    private String masterFN = MASTER_DEFAULT_FEATURE_NAME;

    @Option(
            names = "-,mdpn",
            description =
                    "The 'init -pn' names the default product (first product)")
    private String productFN = MASTER_DEFAULT_PRODUCT_NAME;

    @Override
    public void run() {
       // create folder
    }
}
