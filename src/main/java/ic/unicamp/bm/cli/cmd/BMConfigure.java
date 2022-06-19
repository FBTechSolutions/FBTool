package ic.unicamp.bm.cli.cmd;

import static ic.unicamp.bm.block.GitBlock.BMBlockMasterLabel;

import ic.unicamp.bm.block.BMDirUtil;
import ic.unicamp.bm.block.GitBlockManager;
import ic.unicamp.bm.block.GitDirUtil;
import ic.unicamp.bm.block.IBlockAPI;
import ic.unicamp.bm.cli.util.logger.SplMgrLogger;
import java.io.File;
import java.io.IOException;
import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import picocli.CommandLine.Command;

@Command(
    name = BMConfigure.CMD_NAME,
    description = "This command will create the hidden required folders")
public class BMConfigure implements Runnable {

  public static final String CMD_NAME = "configure";

  @Override
  public void run() {

  }
}
