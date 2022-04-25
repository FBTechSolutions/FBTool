package ic.unicamp.bm.block;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;

public class GitDirectory {
    public static Path getGitDirAsPath() {
        String currentDirectory = System.getProperty("user.dir");
        return Paths.get(currentDirectory, ".git");
    }

    public static File getGitDirAsFile() {
        return new File(String.valueOf(GitDirectory.getGitDirAsPath()));
    }

    public static boolean existsGitDir() {
        return GitDirectory.getGitDirAsFile().exists();
    }

    public static boolean createGitDir() {
        try {
            String currentDirectory = System.getProperty("user.dir");
            File currentDirectoryFile = new File(String.valueOf(currentDirectory));
            Git.init().setDirectory(currentDirectoryFile).call();
            return true;
        } catch (GitAPIException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean removeGitDir() {
        if (GitDirectory.existsGitDir()) {
            try {
                FileUtils.deleteDirectory(GitDirectory.getGitDirAsFile());
                return true;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        } else {
            return true;
        }
    }
}
