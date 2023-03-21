package ic.unicamp.bm.block.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;

public class GitDirectoryUtil {

    private static final Pattern pattern = Pattern.compile("\\.git");

    public static Path getGitDirAsPath() {
        String currentDirectory = System.getProperty("user.dir");
        return Paths.get(currentDirectory, ".git");
    }

    public static File getGitDirAsFile() {
        return new File(String.valueOf(GitDirectoryUtil.getGitDirAsPath()));
    }

    public static boolean existsGitDir() {
        return GitDirectoryUtil.getGitDirAsFile().exists();
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
        if (GitDirectoryUtil.existsGitDir()) {
            try {
                FileUtils.deleteDirectory(GitDirectoryUtil.getGitDirAsFile());
                return true;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        } else {
            return true;
        }
    }

    public static boolean existNameInPath(Path filePath) {
        Matcher matcher = pattern.matcher(filePath.toString());
        return matcher.find();
    }
}
