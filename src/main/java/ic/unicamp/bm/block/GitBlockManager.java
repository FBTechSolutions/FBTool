package ic.unicamp.bm.block;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.lib.Repository;

import java.io.IOException;

public class GitBlockManager implements IBlockAPI {
    private Git git;

    public GitBlockManager() {
        if(!GitDirectory.existsGitDir()){
            GitDirectory.createGitDir();
        }
        FileRepositoryBuilder repositoryBuilder = new FileRepositoryBuilder();
        repositoryBuilder.setMustExist(true);
        repositoryBuilder.setGitDir(GitDirectory.getGitDirAsFile());
        try {
            Repository repository = repositoryBuilder.build();
            this.git = new Git(repository);
        } catch (IOException e) {
            //error
        }
    }


    @Override
    public String insertBlock(String content) {
        return null;
    }

    @Override
    public void editBlock(String blockId, String Content) {

    }

    @Override
    public void removeBlock(String blockId) {

    }

    @Override
    public String retrieveBlock(String blockId) {
        return null;
    }

    @Override
    public Boolean exitBlock(String blockId) {
        return null;
    }
}
