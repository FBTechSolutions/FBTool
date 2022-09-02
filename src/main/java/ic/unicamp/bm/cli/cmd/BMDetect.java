package ic.unicamp.bm.cli.cmd;

import ic.unicamp.bm.block.GitVCSManager;
import ic.unicamp.bm.block.IVCRepository;
import ic.unicamp.bm.scanner.BlockScanner;
import java.nio.file.Path;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

@Command(
    name = BMDetect.CMD_NAME,
    description = "This command will inform whether exits modifications in the blocks using as reference the DB")
public class BMDetect implements Runnable {

  @Parameters(index = "0", description = "featureId", defaultValue = "")
  String repositoryName;

  public static final String CMD_NAME = "detect";

  @Override
  public void run() {
    if (StringUtils.isEmpty(repositoryName)) {
      System.out.println("You need to specify at repository name");
      return;
    }
    BlockScanner blockScanner = new BlockScanner();
    IVCRepository gitRepository = GitVCSManager.createGitRepositoryInstance();
    Path path = gitRepository.getOutDirectory(repositoryName);
    try {
      Map<String, String> updatedBlocks = blockScanner.retrieveAllBlocks(path);
      //if begin with A
      //add in the temporal? and to insert
    } catch (Exception e) {
      throw new RuntimeException(e);
    }

    //from first commit to last commit differences
    //read files
    //read blocks tags
    //compare blocks with db
    //show review of blocks
  }
}
