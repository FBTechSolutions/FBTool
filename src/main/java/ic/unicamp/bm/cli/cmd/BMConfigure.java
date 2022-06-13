package ic.unicamp.bm.cli.cmd;

import static ic.unicamp.bm.block.GitBlock.BMBlockMasterLabel;

import ic.unicamp.bm.block.BMDirUtil;
import ic.unicamp.bm.block.GitBlockManager;
import ic.unicamp.bm.block.GitDirUtil;
import ic.unicamp.bm.block.IBlockAPI;
import ic.unicamp.bm.cli.util.logger.SplMgrLogger;
import java.io.File;
import java.io.IOException;
import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import picocli.CommandLine.Command;

@Command(
    name = BMConfigure.CMD_NAME,
    description = "This command will create the hidden required folders")
public class BMConfigure implements Runnable {

  public static final String CMD_NAME = "configure";

  @Override
  public void run() {
    upsertGitDir();
    upsertBMDir();
  }

  private void upsertBMDir() {
    IBlockAPI gitBlock = GitBlockManager.createGitBlockInstance();

    if (!gitBlock.exitInternalBranch()) {
      gitBlock.createInternalBranch();
      SplMgrLogger.message_ln("- " + BMBlockMasterLabel + " branch was created", false);
    }
    checkoutBlockBranch(gitBlock);
    if (!BMDirUtil.existsBmDirectory()) {
      BMDirUtil.createBMDirectory();
      SplMgrLogger.message_ln("- BM directory was created", false);
    }
    if (!BMDirUtil.existsBMContactFile()) {
      BMDirUtil.createBMContactFile();
      commitBMDirectory();
    }
  }

  private void checkoutBlockBranch(IBlockAPI gitBlock) {
    Git git = (Git) gitBlock.retrieveDirector();
    try {
      git.checkout().setName(BMBlockMasterLabel).call();
    } catch (GitAPIException e) {
      throw new RuntimeException(e);
    }
  }

  private void upsertGitDir() {
    if (!GitDirUtil.existsGitDir()) {
      GitDirUtil.createGitDir();
      SplMgrLogger.message_ln("- Git directory was created", false);
      commitGitIgnoreAsFirstCommit();
    }
  }

  private void commitBMDirectory() {
    IBlockAPI gitBlockManager = GitBlockManager.createGitBlockInstance();
    Git git = (Git) gitBlockManager.retrieveDirector();
    try {
      git.checkout().setName(BMBlockMasterLabel).call();
      git.add().addFilepattern(".").call();
      git.commit().setMessage("BM: Adding BM directory").call();
    } catch (GitAPIException e) {
      throw new RuntimeException(e);
    }
  }

  private void commitGitIgnoreAsFirstCommit() {
    IBlockAPI gitBlockManager = GitBlockManager.createGitBlockInstance();
    Git git = (Git) gitBlockManager.retrieveDirector();
    File myFile =
        new File(git.getRepository().getDirectory().getParent(), ".gitignore");
    try {
      FileUtils.writeStringToFile(myFile, createGitIgnoreContent(), "ISO-8859-1");
      git.add().addFilepattern(".gitignore").call();
      git.add().addFilepattern(".").call();
      git.commit().setMessage("BM: Adding Head with a commit").call();
    } catch (GitAPIException | IOException e) {
      throw new RuntimeException(e);
    }
  }

  private String createGitIgnoreContent() {
    String msg = "# prod\n" +
        ".bm/logs/bm.log\n";
    return msg;
  }
}
