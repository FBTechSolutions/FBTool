package ic.unicamp.fb.cli.cmd;

import ic.unicamp.fb.graph.neo4j.schema.Fragment;
import ic.unicamp.fb.graph.neo4j.services.FragmentService;
import ic.unicamp.fb.graph.neo4j.services.FragmentServiceImpl;
import ic.unicamp.fb.graph.neo4j.utils.FragmentUtil;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

import static ic.unicamp.fb.cli.util.CommonUtil.retrieveStringFromPieces;

//ok
@Command(
        name = FBUpsertFragment.CMD_NAME,
        description = "This command will update or create a Fragment")
public class FBUpsertFragment implements Runnable {

    public static final String CMD_NAME = "upsert-fragment";

    @Parameters(index = "0", description = "fragmentId (e.g., sF019F018)", defaultValue = "")
    String fragmentId;

    @Parameters(index = "1..*", description = "fragmentLabel (e.g.,  CallButtons and DirectedCall)", defaultValue = "")
    String[] labelInParts;

    @Override
    public void run() {
        String featureLabel = retrieveLabel();
        FragmentService fragmentService = new FragmentServiceImpl();
        Fragment fragment = FragmentUtil.retrieveOrCreateAStandardFragmentBean(fragmentService, fragmentId, featureLabel);
        fragmentService.createOrUpdate(fragment);
    }

    private String retrieveLabel() {
        return retrieveStringFromPieces(labelInParts);
    }

}
