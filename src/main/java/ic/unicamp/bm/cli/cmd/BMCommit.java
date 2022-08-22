package ic.unicamp.bm.cli.cmd;

import ic.unicamp.bm.block.GitVCS;
import ic.unicamp.bm.block.GitVCSManager;
import ic.unicamp.bm.block.IVCSAPI;
import ic.unicamp.bm.graph.neo4j.schema.Block;
import ic.unicamp.bm.graph.neo4j.schema.enums.DataState;
import ic.unicamp.bm.graph.neo4j.services.BlockService;
import ic.unicamp.bm.graph.neo4j.services.BlockServiceImpl;
import java.util.List;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import picocli.CommandLine.Command;

@Command(
    name = BMCommit.CMD_NAME,
    description = "This command will add the blocks")
public class BMCommit implements Runnable {

  public static final String CMD_NAME = "commit";

  @Override
  public void run() {
    try {
      //IVCSAPI temporalGitBlock = GitVCSManager.createTemporalGitBlockInstance();
      IVCSAPI gitBlock = GitVCSManager.createInstance();
      Git git = (Git) gitBlock.retrieveDirector();
      git.checkout().setName(GitVCS.BMBranchLabel).call();

      BlockService blockService = new BlockServiceImpl();
      List<Block> stageBlocks = blockService.getBlockByVCBlockState(DataState.STAGE);

      for (Block block : stageBlocks) {
        block.setVcBlockState(DataState.COMMITTED);
        blockService.createOrUpdate(block);
      }

      git.commit().setMessage("BM Adding blocks").call();
      List<Block> committedBlocks = blockService.getBlockByVCBlockState(DataState.COMMITTED);

      System.out.println("Block List:");
      for(Block block:committedBlocks){
        String blockId = block.getBlockId();
        System.out.println("blockId - " + blockId + "  state - "+ DataState.COMMITTED);
      }

    } catch (GitAPIException e) {
      throw new RuntimeException(e);
    }
  }
}
