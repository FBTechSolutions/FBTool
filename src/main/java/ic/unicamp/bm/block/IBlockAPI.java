package ic.unicamp.bm.block;

public interface IBlockAPI {

  public void upsertContentBlock(String blockId, String Content);

  public void removeContentBlock(String blockId);

  public String retrieveContentBlock(String blockId);

  public Boolean exitContentBlock(String blockId);

  public void upsertContainerBlock(String blockId, String Content);

  public void removeContainerBlock(String blockId);

  public String retrieveContainerBlock(String blockId);

  public Boolean exitContainerBlock(String blockId);

  public Object retrieveDirector(); // e.g, git

  public Boolean exitInternalBranch();

  public void createInternalBranch();

  public String getCurrentDirectory();
}
