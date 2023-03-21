package ic.unicamp.bm.cli.cmd;

import ic.unicamp.bm.block.GitVCSManager;
import ic.unicamp.bm.block.IVCSAPI;
import ic.unicamp.bm.graph.neo4j.schema.Block;
import ic.unicamp.bm.graph.neo4j.schema.Feature;
import ic.unicamp.bm.graph.neo4j.schema.relations.BlockToFeature;
import ic.unicamp.bm.graph.neo4j.services.BlockService;
import ic.unicamp.bm.graph.neo4j.services.BlockServiceImpl;
import ic.unicamp.bm.graph.neo4j.services.FeatureService;
import ic.unicamp.bm.graph.neo4j.services.FeatureServiceImpl;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.jgit.api.Git;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

@Command(
        name = BMMoveBlocks.CMD_NAME,
        description = "This command will add the blocks from the temporal folder to the real folder")
public class BMMoveBlocks implements Runnable {

    private final IVCSAPI temporalGitVC = GitVCSManager.createTemporalGitBlockInstance();
    private final IVCSAPI gitVC = GitVCSManager.createInstance();
    private final Git git = (Git) temporalGitVC.retrieveDirector();

    public static final String CMD_NAME = "move-blocks";

    @Parameters(index = "0", description = "oldFeatureId", defaultValue = "")
    String oldFeatureId;
    @Parameters(index = "1", description = "newFeatureId", defaultValue = "")
    String newFeatureId;

    @Override
    public void run() {
        if (oldFeatureId.equals("") || newFeatureId.equals("")) {
            System.out.println("You need two parameters");
            return;
        }
        FeatureService featureService = new FeatureServiceImpl();
        Feature oldF = featureService.getFeatureByID(oldFeatureId);
        if (oldF == null) {
            System.out.println("FeatureId target not valid");
            return;
        }

        BlockService blockService = new BlockServiceImpl();
        List<Block> blocks = blockService.getBlocksByFeature(oldFeatureId);
        Feature newF = featureService.getFeatureByID(newFeatureId);
        if (newF == null) {
            newF = new Feature();
            newF.setFeatureId(newFeatureId);
            newF.setFeatureLabel(newFeatureId);
        }
        for (Block block : blocks) {
            Block blockUpdated = blockService.getBlockByID(block.getBlockId());
            BlockToFeature blockToFeature = new BlockToFeature();
            blockToFeature.setStartBlock(block);
            blockToFeature.setEndFeature(newF);
            blockUpdated.setAssociatedTo(blockToFeature);
            blockService.createOrUpdate(blockUpdated);
        }
    }
}
