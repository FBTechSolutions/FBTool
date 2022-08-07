package ic.unicamp.bm.cli.cmd;

import ic.unicamp.bm.block.GitVCS;
import ic.unicamp.bm.block.GitVCSManager;
import ic.unicamp.bm.block.IVCSAPI;
import ic.unicamp.bm.graph.neo4j.schema.Block;
import ic.unicamp.bm.graph.neo4j.schema.enums.BlockState;
import ic.unicamp.bm.graph.neo4j.schema.enums.DataState;
import ic.unicamp.bm.graph.neo4j.services.BlockService;
import ic.unicamp.bm.graph.neo4j.services.BlockServiceImpl;
import java.util.List;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import picocli.CommandLine.Command;

@Command(
    name = BMAdd.CMD_NAME,
    description = "This command will add the blocks")
public class BMAdd implements Runnable {

  private final IVCSAPI temporalGitBlock = GitVCSManager.createTemporalGitBlockInstance();
  private final IVCSAPI gitBlock = GitVCSManager.createInstance();
  private final Git git = (Git) temporalGitBlock.retrieveDirector();

  public static final String CMD_NAME = "add";

  @Override
  public void run() {
    BlockService blockService = new BlockServiceImpl();
    try {
      git.checkout().setName(GitVCS.BMBranchLabel).call();
      List<Block> temporalDataList = blockService.getBlockByRawState(DataState.TEMPORAL);
      for (Block contentBlock : temporalDataList) {
        String blockId = contentBlock.getBlockId();
        String content = temporalGitBlock.retrieveContent(blockId);
        gitBlock.upsertContent(blockId, content);
        temporalGitBlock.removeContent(blockId);
        git.add().addFilepattern(".bm/" + blockId).call();
        contentBlock.setVcBlockState(DataState.STAGE);
        contentBlock.setBlockState(BlockState.TO_INSERT);
        blockService.createOrUpdate(contentBlock);
      }
      List<Block> stageData = blockService.getBlockByRawState(DataState.STAGE);
      System.out.println("Block List:");
      for (Block block : stageData) {
        String blockId = block.getBlockId();
        System.out.println("blockId - " + blockId + "  state - " + DataState.STAGE );
      }

    } catch (GitAPIException e) {
      throw new RuntimeException(e);
    }

}
}
