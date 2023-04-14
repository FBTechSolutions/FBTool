package ic.unicamp.fb.cli.cmd;

import ic.unicamp.fb.graph.neo4j.schema.Block;
import ic.unicamp.fb.graph.neo4j.schema.Feature;
import ic.unicamp.fb.graph.neo4j.schema.Product;
import ic.unicamp.fb.graph.neo4j.schema.relations.BlockToBlock;
import ic.unicamp.fb.graph.neo4j.schema.relations.ProductToFeature;
import ic.unicamp.fb.graph.neo4j.services.BlockService;
import ic.unicamp.fb.graph.neo4j.services.BlockServiceImpl;
import ic.unicamp.fb.graph.neo4j.services.FeatureService;
import ic.unicamp.fb.graph.neo4j.services.FeatureServiceImpl;
import ic.unicamp.fb.graph.neo4j.services.ProductService;
import ic.unicamp.fb.graph.neo4j.services.ProductServiceImpl;
import org.eclipse.jgit.util.StringUtils;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

import java.util.LinkedList;
import java.util.List;

//ok
@Command(
        name = BMUpsertBlockOut.CMD_NAME,
        description = "This command will create or update a product")
public class BMUpsertBlockOut implements Runnable {

    public static final String CMD_NAME = "upsert-block-out";

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
        if (StringUtils.isEmptyOrNull(targetBlockId)) {
            System.out.println("This command requires a nextBlockId param");
            return;
        }

        BlockService blockService = new BlockServiceImpl();

        Block currentFullBLock = blockService.getBlockByID(currentBlockId);
        if (currentFullBLock == null) {
            System.out.println("currentBlockId not found");
            return;
        }
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
    }
}
