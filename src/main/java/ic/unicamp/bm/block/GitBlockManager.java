package ic.unicamp.bm.block;

public class GitBlockManager {

  private static IBlockAPI instance = null;
  public static IBlockAPI createInstance(){
    if(instance == null){
      instance = new GitBlock();
    }
    return instance;
  }
}
