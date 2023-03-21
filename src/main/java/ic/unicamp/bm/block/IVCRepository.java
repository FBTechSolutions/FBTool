package ic.unicamp.bm.block;

import java.nio.file.Path;

import org.eclipse.jgit.api.Git;

public interface IVCRepository {

    Path upsertRepository(String repositoryName);

    void updateOutDirectory(String path);

    Path getOutDirectory(String name);

    Git createGitDir(Path repositoryPath);
}
