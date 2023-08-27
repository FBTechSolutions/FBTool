package ic.unicamp.fb.cli.cmd;

import ic.unicamp.fb.graph.neo4j.schema.Index;
import ic.unicamp.fb.graph.neo4j.schema.Feature;
import ic.unicamp.fb.graph.neo4j.schema.relations.FeatureToIndex;
import ic.unicamp.fb.graph.neo4j.services.IndexService;
import ic.unicamp.fb.graph.neo4j.services.IndexServiceImpl;
import ic.unicamp.fb.graph.neo4j.services.FeatureService;
import ic.unicamp.fb.graph.neo4j.services.FeatureServiceImpl;
import ic.unicamp.fb.graph.neo4j.utils.IndexUtil;
import ic.unicamp.fb.graph.neo4j.utils.FeatureUtil;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

import static ic.unicamp.fb.cli.util.CommonUtil.retrieveStringFromPieces;

//ok
@Command(
        name = FBUpsertFeature.CMD_NAME,
        description = "This command will update or create a Feature")
public class FBUpsertFeature implements Runnable {

    public static final String CMD_NAME = "upsert-feature";

    @Parameters(index = "0", description = "featureId (required)", defaultValue = "")
    String featureId;

    @Parameters(index = "1", description = "index (required)", defaultValue = "100")
    String indexId;

    @Parameters(index = "2..*")
    String[] labelInParts;

    @Override
    public void run() {
        String featureLabel = retrieveLabel();
        FeatureService featureService = new FeatureServiceImpl();
        Feature feature = FeatureUtil.retrieveOrCreateAStandardFeatureBean(featureService, featureId, featureLabel);
        IndexService indexService = new IndexServiceImpl();
        Index index = IndexUtil.retrieveOrCreateAStandardIndexBean(indexService, Integer.parseInt(indexId));
        FeatureToIndex rel = new FeatureToIndex();
        rel.setStartFeature(feature);
        rel.setEndIndex(index);
        feature.setAssociatedTo(rel);
        featureService.createOrUpdate(feature);
    }

    private String retrieveLabel() {
        return retrieveStringFromPieces(labelInParts);
    }
}
