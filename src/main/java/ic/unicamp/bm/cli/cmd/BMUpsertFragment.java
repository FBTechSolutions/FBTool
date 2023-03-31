package ic.unicamp.bm.cli.cmd;

import ic.unicamp.bm.graph.neo4j.schema.Feature;
import ic.unicamp.bm.graph.neo4j.schema.Fragment;
import ic.unicamp.bm.graph.neo4j.services.FeatureService;
import ic.unicamp.bm.graph.neo4j.services.FeatureServiceImpl;
import ic.unicamp.bm.graph.neo4j.services.FragmentService;
import ic.unicamp.bm.graph.neo4j.services.FragmentServiceImpl;
import ic.unicamp.bm.graph.neo4j.utils.FragmentUtil;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

//ok
@Command(
        name = BMUpsertFragment.CMD_NAME,
        description = "This command will update or create a Fragment")
public class BMUpsertFragment implements Runnable {

    public static final String CMD_NAME = "upsert-fragment";

    @Parameters(index = "0", description = "fragmentId (required)", defaultValue = "")
    String fragmentId;

    @Parameters(index = "1..*")
    String[] labelInParts;

    @Override
    public void run() {
        String featureLabel = retrieveLabel();
        FragmentService fragmentService = new FragmentServiceImpl();
        Fragment fragment = FragmentUtil.retrieveOrCreateAStandardFragment(fragmentService,fragmentId, featureLabel);
        fragmentService.createOrUpdate(fragment);
    }

    private String retrieveLabel() {
        String featureLabel = "";
        if (labelInParts != null) {
            StringBuilder sb = new StringBuilder();
            for (String s : labelInParts) {
                sb.append(s);
            }
            featureLabel = sb.toString();
        }
        return featureLabel;
    }
}
