package ic.unicamp.fb.block;

public interface IVCSAPI {

    public void upsertContent(String blockId, String Content);

    public void removeContent(String blockId);

    public String retrieveContent(String blockId);

    public Boolean exitContent(String blockId);

    public void upsertContainerBlock(String blockId, String Content);

    public void removeContainerBlock(String blockId);

    public String retrieveContainerBlock(String blockId);

    public Boolean exitContainerBlock(String blockId);

    public Object retrieveDirector(); // e.g, git

    public Boolean exitBMBranch();

    public void createBMBranch();

    public String getCurrentDirectory();
}
