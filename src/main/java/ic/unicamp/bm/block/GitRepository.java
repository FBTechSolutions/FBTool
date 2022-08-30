package ic.unicamp.bm.block;

import ic.unicamp.bm.block.utils.DirectoryUtil;
import ic.unicamp.bm.block.utils.GitDirectoryUtil;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.InitCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

public class GitRepository implements  IVCRepository {

  private final Path path = DirectoryUtil.getOutDirectoryAsPath();
  @Override
  public Path upsertRepository(String repositoryName) {

    Path productRepository = Paths.get(String.valueOf(path), repositoryName);
    File  file = new File(String.valueOf(productRepository));
    if(file.exists()){
      try {
        FileUtils.deleteDirectory(file);
      } catch (IOException e) {
        System.out.println("We could not remove the Folder");
        throw new RuntimeException(e);
      }
    }
    try {
        return Files.createDirectories(Paths.get(String.valueOf(productRepository)));
    } catch (IOException e) {
        System.out.println("We could not create the product folder");
        throw new RuntimeException(e);
    }
  }

  @Override
  public void updateOutDirectory(String path) {

  }

  @Override
  public void getOutDirectory() {

  }
  @Override
  public Git createGitDir(Path repositoryPath) {
    try {
      File currentDirectoryFile = new File(String.valueOf(repositoryPath));
      Git.init().setDirectory(currentDirectoryFile).call();
      FileRepositoryBuilder repositoryBuilder = new FileRepositoryBuilder();
      repositoryBuilder.setMustExist(true);
      repositoryBuilder.setGitDir(currentDirectoryFile);

      try {
        Repository repository = repositoryBuilder.build();
        return new Git(repository);
      } catch (IOException e) {
        e.printStackTrace();
      }
    } catch (GitAPIException e) {
      e.printStackTrace();
      return null;
    }
    return null;
  }
}
