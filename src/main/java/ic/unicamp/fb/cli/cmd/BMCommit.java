package ic.unicamp.fb.cli.cmd;

import ic.unicamp.fb.block.GitVCS;
import ic.unicamp.fb.block.GitVCSManager;
import ic.unicamp.fb.block.IVCSAPI;
import ic.unicamp.fb.graph.neo4j.schema.Block;
import ic.unicamp.fb.graph.neo4j.schema.enums.BlockState;
import ic.unicamp.fb.graph.neo4j.schema.enums.DataState;
import ic.unicamp.fb.graph.neo4j.services.BlockService;
import ic.unicamp.fb.graph.neo4j.services.BlockServiceImpl;

import java.util.List;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import picocli.CommandLine.Command;

@Command(
        name = BMCommit.CMD_NAME,
        description = "This command will add the blocks")
public class BMCommit implements Runnable {

    public static final String CMD_NAME = "commit";
    private final IVCSAPI gitVC = GitVCSManager.createInstance();
    private final Git git = (Git) gitVC.retrieveDirector();

    @Override
    public void run() {
        try {
            git.checkout().setName(GitVCS.BMBranchLabel).call();

            BlockService blockService = new BlockServiceImpl();
            List<Block> stageBlocks = blockService.getBlockByVCBlockState(DataState.STAGE);
            for (Block block : stageBlocks) {
                Block fullBlock = blockService.getBlockByID(block.getBlockId());
                fullBlock.setVcBlockState(DataState.COMMITTED);
                fullBlock.setBlockState(BlockState.SYNC);
                blockService.createOrUpdate(fullBlock);
            }
            git.commit().setMessage("BM Adding blocks").call();
            List<Block> committedBlocks = blockService.getBlockByVCBlockState(DataState.COMMITTED);
            for (Block block : committedBlocks) {
                String blockId = block.getBlockId();
                System.out.println("blockId - " + blockId + "  state - " + block.getVcBlockState());
            }
        } catch (GitAPIException e) {
            throw new RuntimeException(e);
        }
    }
}