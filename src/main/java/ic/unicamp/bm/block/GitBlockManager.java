package ic.unicamp.bm.block;

public class GitBlockManager {

  private static IBlockAPI instance = null;
  private static IBlockAPI temporal = null;

  public static IBlockAPI createGitBlockInstance() {
    if (instance == null) {
      instance = new GitBlock();
    }
    return instance;
  }

  public static IBlockAPI createTemporalGitBlockInstance() {
    if (temporal == null) {
      temporal = new GitBlockTemporal();
    }
    return temporal;
  }
}
