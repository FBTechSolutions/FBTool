package ic.unicamp.bm.block;

public class GitBlockTemporal extends GitBlock{

  @Override
  public String getCurrentDirectory() {
    return BMTemporalDirUtil.getBMDirectoryAsPath().toString();
  }
}
