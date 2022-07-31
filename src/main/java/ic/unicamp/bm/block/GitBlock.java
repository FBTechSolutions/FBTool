package ic.unicamp.bm.block;

import ic.unicamp.bm.graph.schema.ContentBlock;
import ic.unicamp.bm.graph.schema.Data;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ListBranchCommand.ListMode;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.lib.Repository;

import java.io.IOException;

//https://github.com/centic9/jgit-cookbook
public class GitBlock implements IBlockAPI {

  private Git git;
  public static String BMBlockMasterLabel = "bm_block_master";

  public GitBlock() {
    FileRepositoryBuilder repositoryBuilder = new FileRepositoryBuilder();
    repositoryBuilder.setMustExist(true);
    repositoryBuilder.setGitDir(GitDirUtil.getGitDirAsFile());
    try {
      Repository repository = repositoryBuilder.build();
      this.git = new Git(repository);
    } catch (IOException e) {
      //error
    }
  }

  public String getCurrentDirectory() {
    return BMDirUtil.getBMDirectoryAsPath().toString();
  }

  //content
  @Override
  public void upsertContent(String blockId, String Content) {
    Path path = Paths.get(getCurrentDirectory(), blockId);
    try {
      if (!Files.exists(path, LinkOption.NOFOLLOW_LINKS)) {
        Files.createFile(path);
      }
      Files.writeString(path, Content);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void removeContent(String blockId) {
    Path path = Paths.get(getCurrentDirectory(), blockId);
    try {
      if (Files.exists(path, LinkOption.NOFOLLOW_LINKS)) {
        Files.delete(path);
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public String retrieveContent(String blockId) {
    Path path = Paths.get(getCurrentDirectory(), blockId);
    String read = "";
    try {
      read = readAllFile(path.toString(), StandardCharsets.UTF_8);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    return read;
  }

  private String readAllFile(String path, Charset encoding) throws IOException {
    byte[] encoded = Files.readAllBytes(Paths.get(path));
    return new String(encoded, encoding);
  }

  @Override
  public Boolean exitContent(String blockId) {
    Path content = Paths.get(getCurrentDirectory(), blockId);
    return Files.exists(content, LinkOption.NOFOLLOW_LINKS);
  }

  //container
  @Override
  public void upsertContainerBlock(String blockId, String path) {
    upsertContent(blockId, path);
  }

  @Override
  public void removeContainerBlock(String blockId) {
    removeContent(blockId);
  }

  @Override
  public String retrieveContainerBlock(String blockId) {
    return retrieveContent(blockId);
  }

  @Override
  public Boolean exitContainerBlock(String blockId) {
    return exitContent(blockId);
  }

  //utils
  @Override
  public Boolean exitInternalBranch() {
    List<Ref> call;
    try {
      call = git.branchList().setListMode(ListMode.ALL).call();
      for (Ref ref : call) {
        if (ref.getName().contains(BMBlockMasterLabel)) {
          return true;
        }
      }
    } catch (GitAPIException e) {
      throw new RuntimeException(e);
    }
    return false;
  }

  @Override
  public void createInternalBranch() {
    try {
      git.branchCreate().setName(BMBlockMasterLabel).call();
      git.checkout().setName(BMBlockMasterLabel).call();
    } catch (GitAPIException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public Object retrieveDirector() {
    return git;
  }
}
