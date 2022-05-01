package ic.unicamp.bm.block;

public interface IBlockAPI {
    //block limit-> String lenght
    public String insertBlock(String content); //return sha -> commit
    public void editBlock(String blockId, String Content);
    public void removeBlock(String blockId); //not used
    public String retrieveBlock(String blockId); // return content
    public Boolean exitBlock(String blockId);

    public Object getBlockDirector();
    public Boolean exitBlockBranchDir();
    public void createBlockBranchDir();
}
