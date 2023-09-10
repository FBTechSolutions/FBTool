package ic.unicamp.fb.cli.cmd;

import ic.unicamp.fb.graph.neo4j.schema.Block;
import ic.unicamp.fb.graph.neo4j.schema.relations.BlockToBlock;
import ic.unicamp.fb.graph.neo4j.services.BlockService;
import ic.unicamp.fb.graph.neo4j.services.BlockServiceImpl;
import org.eclipse.jgit.util.StringUtils;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

//ok
@Command(
        name = FBUpsertBlockOut.CMD_NAME,
        description = "Internal command")
public class FBUpsertBlockOut implements Runnable {

    public static final String CMD_NAME = "upsert-block-out";

    @CommandLine.Option(names = {"--ignore-target"}, description = "")
    private boolean isWithoutTarget;

    @Parameters(index = "0")
    String currentBlockId;

    @Parameters(index = "1")
    String targetBlockId;

    @Override
    public void run() {
        if (StringUtils.isEmptyOrNull(currentBlockId)) {
            System.out.println("This command requires a currentBlockId param");
            return;
        }
        if(!isWithoutTarget){
            if (StringUtils.isEmptyOrNull(targetBlockId)) {
                System.out.println("This command requires a nextBlockId param");
                return;
            }
        }


        BlockService blockService = new BlockServiceImpl();

        Block currentFullBLock = blockService.getBlockByID(currentBlockId);
        if (currentFullBLock == null) {
            System.out.println("currentBlockId not found");
            return;
        }
        if(!isWithoutTarget){
            Block targetFullBLock = blockService.getBlockByID(targetBlockId);
            if (targetFullBLock == null) {
                System.out.println("targetFullBLock not found");
                return;
            }

            BlockToBlock blockToBlock = currentFullBLock.getGoNextBlock();
            if (blockToBlock == null) {
                blockToBlock = new BlockToBlock();
            }
            blockToBlock.setStartBlock(currentFullBLock);
            blockToBlock.setEndBlock(targetFullBLock);
            currentFullBLock.setGoNextBlock(blockToBlock);

            blockService.createOrUpdate(currentFullBLock);
        }else{
            currentFullBLock.setGoNextBlock(null);
            blockService.createOrUpdate(currentFullBLock);
        }

    }
}
