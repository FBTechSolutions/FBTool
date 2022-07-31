package ic.unicamp.bm.block;

import ic.unicamp.bm.block.utils.TempBMDirectoryUtil;

public class TempGitVCS extends GitVCS {

  @Override
  public String getCurrentDirectory() {
    return TempBMDirectoryUtil.getBMDirectoryAsPath().toString();
  }
}
