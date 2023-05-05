package ic.unicamp.fb.cli.cmd;

import ic.unicamp.fb.block.GitVCS;
import ic.unicamp.fb.block.GitVCSManager;
import ic.unicamp.fb.block.IVCSAPI;
import ic.unicamp.fb.graph.neo4j.schema.Block;
import ic.unicamp.fb.graph.neo4j.schema.enums.BlockState;
import ic.unicamp.fb.graph.neo4j.schema.enums.DataState;
import ic.unicamp.fb.graph.neo4j.services.BlockService;
import ic.unicamp.fb.graph.neo4j.services.BlockServiceImpl;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import picocli.CommandLine.Command;

import java.util.List;

@Command(
        name = FBAdd.CMD_NAME,
        description = "This command will add the blocks from the temporal folder to the real folder")
public class FBAdd implements Runnable {

    private final IVCSAPI temporalGitVC = GitVCSManager.createTemporalGitBlockInstance();
    private final IVCSAPI gitVC = GitVCSManager.createInstance();
    private final Git git = (Git) temporalGitVC.retrieveDirector();

    public static final String CMD_NAME = "add";

    @Override
    public void run() {
        BlockService blockService = new BlockServiceImpl();
        try {
            git.checkout().setName(GitVCS.BMBranchLabel).call();
            List<Block> temporalBlockList = blockService.getBlockByVCBlockState(DataState.TEMPORAL);
            for (Block block : temporalBlockList) {
                String blockId = block.getBlockId();
                String content = temporalGitVC.retrieveContent(blockId);
                gitVC.upsertContent(blockId, content);
                temporalGitVC.removeContent(blockId);
                git.add().addFilepattern(".bm/" + blockId).call();
                Block fullBlock = blockService.getBlockByID(blockId);
                fullBlock.setVcBlockState(DataState.STAGE);
                fullBlock.setBlockState(BlockState.TO_INSERT);
                blockService.createOrUpdate(fullBlock);
            }
            List<Block> stageData = blockService.getBlockByVCBlockState(DataState.STAGE);
            for (Block block : stageData) {
                String blockId = block.getBlockId();
                System.out.println("blockId - " + blockId + "  state - " + block.getVcBlockState());
            }
        } catch (GitAPIException e) {
            throw new RuntimeException(e);
        }
    }
}
