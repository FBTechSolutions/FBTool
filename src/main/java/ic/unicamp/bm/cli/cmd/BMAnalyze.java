package ic.unicamp.bm.cli.cmd;
import ic.unicamp.bm.block.BMDirectory;
import ic.unicamp.bm.block.GitBlock;
import ic.unicamp.bm.block.GitBlockManager;
import ic.unicamp.bm.block.GitDirectory;
import ic.unicamp.bm.block.IBlockAPI;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.revwalk.RevCommit;
import picocli.CommandLine.Command;

@Command(
        name = BMAnalyze.CMD_NAME,
        description = "This command will Analyze all files")
public class BMAnalyze implements Runnable {
    public static final String CMD_NAME = "analyze";

    @Override
    public void run() {
        Path path = Paths.get(System.getProperty("user.dir"));
        List<Path> paths = null;
        try {
            IBlockAPI gitBlock = GitBlockManager.createInstance();
            Git git = (Git) gitBlock.getBlockDirector();
            git.checkout().setName(GitBlock.BMBlockMaster).call();

            paths = listFiles(path);
            paths.forEach(System.out::println);

            git.add().addFilepattern(".").call();
            Collection<RevCommit> stashes = git.stashList().call();;
            for(RevCommit rev : stashes) {
                System.out.println("Found stash: " + rev + ": " + rev.getFullMessage());
            }
        } catch (IOException | GitAPIException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<Path> listFiles(Path path) throws IOException {
        List<Path> result;
        try (Stream<Path> walk = Files.walk(path)) {
            result = walk.filter(Files::isRegularFile).filter(aPath -> !GitDirectory.existNameInPath(aPath)).filter(aPath -> !BMDirectory.existNameInPath(aPath))
                .collect(Collectors.toList());
        }
        return result;

    }
}
