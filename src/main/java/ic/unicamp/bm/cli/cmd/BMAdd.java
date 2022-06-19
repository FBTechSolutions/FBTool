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
import ic.unicamp.bm.scanner.BlockState;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.CheckoutConflictException;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRefNameException;
import org.eclipse.jgit.api.errors.RefAlreadyExistsException;
import org.eclipse.jgit.api.errors.RefNotFoundException;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(
    name = BMAdd.CMD_NAME,
    description = "This command will add the blocks")
public class BMAdd implements Runnable {

  private final GraphAPI graph = GraphBuilder.createGraphInstance();
  private final IBlockAPI temporalGitBlock = GitBlockManager.createTemporalGitBlockInstance();
  private final IBlockAPI gitBlock = GitBlockManager.createGitBlockInstance();
  private final Git git = (Git) temporalGitBlock.retrieveDirector();

  public static final String CMD_NAME = "add";

  @Override
  public void run() {
    try {
      git.checkout().setName(GitBlock.BMBlockMasterLabel).call();
      List<Data> temporalDataList = graph.retrieveDataByState(DataState.TEMPORAL);
      for (Data data : temporalDataList) {
        ContentBlock contentBlock = data.getBelongsTo();
        String blockId = contentBlock.getContentId();
        String content = temporalGitBlock.retrieveContentBlock(blockId);
        gitBlock.upsertContentBlock(blockId, content);
        temporalGitBlock.removeContentBlock(blockId);
        git.add().addFilepattern(".bm/" + blockId).call();

        data.setCurrentState(DataState.STAGE);
        graph.upsertData(data, RecordState.CONTENT);
        contentBlock.setCurrentState(BlockState.TO_INSERT);
        graph.upsertContent(contentBlock, RecordState.CONTENT);
      }

      List<Data> stageData = graph.retrieveDataByState(DataState.STAGE);
      System.out.println("Block List:");
      for (Data record : stageData) {
        ContentBlock block = record.getBelongsTo();
        String blockId = block.getContentId();
        System.out.println("blockId - " + blockId + "  state - " + DataState.STAGE + " FROM "
            + block.getBelongsTo().getContainerId());
      }

    } catch (GitAPIException e) {
      throw new RuntimeException(e);
    }
  }
}
