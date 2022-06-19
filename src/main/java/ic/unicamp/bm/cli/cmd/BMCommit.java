package ic.unicamp.bm.cli.cmd;

import ic.unicamp.bm.block.BMDirUtil;
import ic.unicamp.bm.block.GitBlock;
import ic.unicamp.bm.block.GitBlockManager;
import ic.unicamp.bm.block.IBlockAPI;
import ic.unicamp.bm.graph.GraphAPI;
import ic.unicamp.bm.graph.GraphBuilder;
import ic.unicamp.bm.graph.RecordState;
import ic.unicamp.bm.graph.schema.ContentBlock;
import ic.unicamp.bm.graph.schema.Data;
import ic.unicamp.bm.graph.schema.enums.DataState;
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
      GraphAPI graph = GraphBuilder.createGraphInstance();
      IBlockAPI temporalGitBlock = GitBlockManager.createTemporalGitBlockInstance();
      IBlockAPI gitBlock = GitBlockManager.createGitBlockInstance();
      Git git = (Git) temporalGitBlock.retrieveDirector();
      git.checkout().setName(GitBlock.BMBlockMasterLabel).call();

      List<Data> stateData = graph.retrieveDataByState(DataState.STAGE);

      for(Data data:stateData){
        ContentBlock block = data.getBelongsTo();
        String blockId = block.getContentId();
        data.setCurrentState(DataState.COMMITTED);
        graph.upsertData(data, RecordState.CONTENT);
      }
      git.commit().setMessage("BM Adding blocks").call();

      List<Data> stageData = graph.retrieveDataByState(DataState.COMMITTED);
      System.out.println("Block List:");
      for(Data record:stageData){
        ContentBlock block = record.getBelongsTo();
        String blockId = block.getContentId();
        System.out.println("blockId - " + blockId + "  state - "+ DataState.COMMITTED+ " FROM "+block.getBelongsTo().getContainerId());
      }

    } catch (GitAPIException e) {
      throw new RuntimeException(e);
    }
  }
}
