package ic.unicamp.bm.block;

public class GitVCSManager {

  private static IVCSAPI instance = null;
  private static IVCSAPI temporal = null;

  public static IVCSAPI createInstance() {
    if (instance == null) {
      instance = new GitBlock();
    }
    return instance;
  }

  public static IVCSAPI createTemporalGitBlockInstance() {
    if (temporal == null) {
      temporal = new GitBlockTemporal();
    }
    return temporal;
  }
}
