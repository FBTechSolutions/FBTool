package ic.unicamp.bm.cli.cmd;

import ic.unicamp.bm.block.GitVCS;
import ic.unicamp.bm.block.GitVCSManager;
import ic.unicamp.bm.block.IVCSAPI;
import ic.unicamp.bm.graph.GraphDBAPI;
import ic.unicamp.bm.graph.GraphDBBuilder;
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
      GraphDBAPI graph = GraphDBBuilder.createGraphInstance();
      IVCSAPI temporalGitBlock = GitVCSManager.createTemporalGitBlockInstance();
      IVCSAPI gitBlock = GitVCSManager.createInstance();
      Git git = (Git) temporalGitBlock.retrieveDirector();
      git.checkout().setName(GitVCS.BMBranchLabel).call();

/*      List<Data> stateData = graph.retrieveDataByState(DataState.STAGE);

      for(Data data:stateData){
        ContentBlock block = data.getBelongsTo();
        String blockId = block.getContentId();
        data.setCurrentState(DataState.COMMITTED);
        graph.upsertData(data, NodePart.VERTEX);
      }
      git.commit().setMessage("BM Adding blocks").call();

      List<Data> stageData = graph.retrieveDataByState(DataState.COMMITTED);
      System.out.println("Block List:");
      for(Data record:stageData){
        ContentBlock block = record.getBelongsTo();
        String blockId = block.getContentId();
        System.out.println("blockId - " + blockId + "  state - "+ DataState.COMMITTED+ " FROM "+block.getBelongsTo().getContainerId());
      }*/

    } catch (GitAPIException e) {
      throw new RuntimeException(e);
    }
  }
}
