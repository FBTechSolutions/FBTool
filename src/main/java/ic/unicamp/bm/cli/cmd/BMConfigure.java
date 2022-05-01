package ic.unicamp.bm.cli.cmd;

import ic.unicamp.bm.block.BMDirectory;
import ic.unicamp.bm.block.GitBlockManager;
import ic.unicamp.bm.block.GitDirectory;
import ic.unicamp.bm.block.IBlockAPI;
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
    if (!GitDirectory.existsGitDir()) {
      GitDirectory.createGitDir();
    }
    IBlockAPI gitBlockManager = new GitBlockManager();
    if (!gitBlockManager.exitBlockBranchDir()) {
      gitBlockManager.createBlockBranchDir();
    }
    if (!BMDirectory.existsBmDirectory()) {
      BMDirectory.createBMDirectory();
      commitBMDirectory(gitBlockManager);
    }

  }

  private void commitBMDirectory(IBlockAPI gitBlockManager) {
    Git git = (Git) gitBlockManager.getBlockDirector();
    try {
      git.checkout().setName(GitBlockManager.BMBlockMaster).call();
      git.commit().setMessage("Adding BM directory").call();
    } catch (GitAPIException e) {
      throw new RuntimeException(e);
    }
  }
}
