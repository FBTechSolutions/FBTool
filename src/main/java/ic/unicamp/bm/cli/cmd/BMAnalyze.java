package ic.unicamp.bm.cli.cmd;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
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
            paths = listFiles(path);
            paths.forEach(System.out::println);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<Path> listFiles(Path path) throws IOException {

        List<Path> result;
        try (Stream<Path> walk = Files.walk(path)) {
            result = walk.filter(Files::isRegularFile)
                .collect(Collectors.toList());
        }
        return result;

    }
}
