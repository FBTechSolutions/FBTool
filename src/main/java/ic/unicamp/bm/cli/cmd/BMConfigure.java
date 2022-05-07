package ic.unicamp.bm.cli.cmd;

import ic.unicamp.bm.block.BMDirUtil;
import ic.unicamp.bm.block.GitBlock;
import ic.unicamp.bm.block.GitBlockManager;
import ic.unicamp.bm.block.GitDirUtil;
import ic.unicamp.bm.block.IBlockAPI;
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
    IBlockAPI gitBlock = GitBlockManager.createInstance();
    if (!gitBlock.exitBlockBranchDir()) {
      gitBlock.createBlockBranchDir();
    }
    checkoutBlockBranch(gitBlock);
    if (!BMDirUtil.existsBmDirectory()) {
      BMDirUtil.createBMDirectory();
      commitBMDirectory();
    }
  }

  private void checkoutBlockBranch(IBlockAPI gitBlock) {
    Git git = (Git) gitBlock.getBlockDirector();
    try {
      git.checkout().setName(GitBlock.BMBlockMaster).call();
    } catch (GitAPIException e) {
      throw new RuntimeException(e);
    }
  }

  private void upsertGitDir() {
    if (!GitDirUtil.existsGitDir()) {
      GitDirUtil.createGitDir();
      commitHelloWorld();
    }
  }

  private void commitBMDirectory() {
    IBlockAPI gitBlockManager = GitBlockManager.createInstance();
    Git git = (Git) gitBlockManager.getBlockDirector();
    try {
      git.checkout().setName(GitBlock.BMBlockMaster).call();
      git.add().addFilepattern(".").call();
      git.commit().setMessage("Adding BM directory").call();
    } catch (GitAPIException e) {
      throw new RuntimeException(e);
    }
  }

  private void commitHelloWorld() {
    IBlockAPI gitBlockManager = GitBlockManager.createInstance();
    Git git = (Git) gitBlockManager.getBlockDirector();
    File myFile =
        new File(git.getRepository().getDirectory().getParent(), ".gitignore");
    try {
      FileUtils.writeStringToFile(myFile, createGitIgnoreContent(), "ISO-8859-1");
      git.add().addFilepattern(".gitignore").call();
      git.add().addFilepattern(".").call();
      git.commit().setMessage("SPLM: Init master branch with a commit").call();
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
