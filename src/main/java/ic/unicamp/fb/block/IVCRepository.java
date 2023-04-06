package ic.unicamp.fb.block;

import org.eclipse.jgit.api.Git;

import java.nio.file.Path;

public interface IVCRepository {

    Path upsertRepository(String repositoryName);

    void updateOutDirectory(String path);

    Path getOutDirectory(String name);

    Git createGitDir(Path repositoryPath);
}
