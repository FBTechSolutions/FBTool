package ic.unicamp.bm.cli.cmd;

import ic.unicamp.bm.block.GitBlock;
import ic.unicamp.bm.block.GitBlockManager;
import ic.unicamp.bm.block.IBlockAPI;
import ic.unicamp.bm.graph.GraphDBAPI;
import ic.unicamp.bm.graph.GraphDBBuilder;
import ic.unicamp.bm.graph.NodePart;
import ic.unicamp.bm.scanner.BlockState;
import java.util.List;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import picocli.CommandLine.Command;

@Command(
    name = BMAdd.CMD_NAME,
    description = "This command will add the blocks")
public class BMAdd implements Runnable {

  private final GraphDBAPI graphDB = GraphDBBuilder.createGraphInstance();
  private final IBlockAPI temporalGitBlock = GitBlockManager.createTemporalGitBlockInstance();
  private final IBlockAPI gitBlock = GitBlockManager.createGitBlockInstance();
  private final Git git = (Git) temporalGitBlock.retrieveDirector();

  public static final String CMD_NAME = "add";

  @Override
  public void run() {
 /*   try {
      git.checkout().setName(GitBlock.BMBlockMasterLabel).call();
      List<Data> temporalDataList = graphDB.retrieveDataByState(DataState.TEMPORAL);
      for (Data data : temporalDataList) {
        ContentBlock contentBlock = data.getBelongsTo();
        String blockId = contentBlock.getContentId();
        String content = temporalGitBlock.retrieveContent(blockId);
        gitBlock.upsertContent(blockId, content);
        temporalGitBlock.removeContent(blockId);
        git.add().addFilepattern(".bm/" + blockId).call();

        data.setCurrentState(DataState.STAGE);
        graphDB.upsertData(data, NodePart.VERTEX);
        contentBlock.setCurrentState(BlockState.TO_INSERT);
        graphDB.upsertContent(contentBlock, NodePart.VERTEX);
      }

      List<Data> stageData = graphDB.retrieveDataByState(DataState.STAGE);
      System.out.println("Block List:");
      for (Data record : stageData) {
        ContentBlock block = record.getBelongsTo();
        String blockId = block.getContentId();
        System.out.println("blockId - " + blockId + "  state - " + DataState.STAGE + " FROM "
            + block.getBelongsTo().getContainerId());
      }

    } catch (GitAPIException e) {
      throw new RuntimeException(e);
    }    }
    */
}
}
