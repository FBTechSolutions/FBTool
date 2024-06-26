package ic.unicamp.fb.block;

import ic.unicamp.fb.block.utils.DirectoryUtil;
import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class GitRepository implements IVCRepository {

    private final Path path = DirectoryUtil.getOutDirectoryAsPath();

    @Override
    public Path upsertRepository(String repositoryName) {

        Path productRepository = Paths.get(String.valueOf(path), repositoryName);
        File file = new File(String.valueOf(productRepository));
        if (file.exists()) {
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
    public Path getOutDirectory(String name) {
        return Paths.get(String.valueOf(path), name);
    }

    @Override
    public Path getParentDirectory() {
        return Paths.get(String.valueOf(path));
    }

    @Override
    public Git createGitDir(Path repositoryPath) {
        try {

            File currentDirectoryFile = new File(String.valueOf(repositoryPath));
            System.out.println(currentDirectoryFile.getAbsolutePath());
            Git.init().setDirectory(currentDirectoryFile).call();
            Path repositoryGit = Paths.get(String.valueOf(repositoryPath), ".git");
            File currentDirectoryFile2 = new File(String.valueOf(repositoryGit));
            if (currentDirectoryFile2.exists()) {

                FileRepositoryBuilder repositoryBuilder = new FileRepositoryBuilder();
                repositoryBuilder.setMustExist(true);
                repositoryBuilder.setGitDir(currentDirectoryFile2);

                try {
                    Repository repository = repositoryBuilder.build();
                    return new Git(repository);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        } catch (GitAPIException e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }
}
