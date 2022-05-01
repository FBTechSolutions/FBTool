package ic.unicamp.bm.block;

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
    public static String BMBlockMaster = "bm_block_master";

    public GitBlock() {
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

    @Override
    public Boolean exitBlockBranchDir() {
        List<Ref> call;
        try {
            call = git.branchList().setListMode(ListMode.ALL).call();
            for (Ref ref : call) {
                if(ref.getName().contains(BMBlockMaster)){
                    return true;
                }
            }
        } catch (GitAPIException e) {
            throw new RuntimeException(e);
        }
        return false;
    }

    @Override
    public void createBlockBranchDir() {
        try {

            git.branchCreate().setName(BMBlockMaster).call();
            git.checkout().setName(BMBlockMaster).call();

        } catch (GitAPIException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Object getBlockDirector() {
        return git;
    }
}
