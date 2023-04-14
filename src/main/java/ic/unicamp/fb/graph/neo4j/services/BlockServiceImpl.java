package ic.unicamp.fb.graph.neo4j.services;

import com.google.common.collect.Lists;
import ic.unicamp.fb.graph.neo4j.factory.Neo4jSessionFactory;
import ic.unicamp.fb.graph.neo4j.schema.Block;
import ic.unicamp.fb.graph.neo4j.schema.Container;
import ic.unicamp.fb.graph.neo4j.schema.Feature;
import ic.unicamp.fb.graph.neo4j.schema.Fragment;
import ic.unicamp.fb.graph.neo4j.schema.enums.DataState;
import ic.unicamp.fb.graph.neo4j.schema.relations.BlockToBlock;
import ic.unicamp.fb.graph.neo4j.schema.relations.ContainerToBlock;
import org.neo4j.ogm.cypher.ComparisonOperator;
import org.neo4j.ogm.cypher.Filter;
import org.neo4j.ogm.cypher.Filters;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class BlockServiceImpl extends GenericService<Block> implements BlockService {

    @Override
    public Iterable<Block> findAll() {
        return session.loadAll(Block.class, 1);
    }

    @Override
    public Block getBlockByID(String blockId) {
        Filter filter = new Filter("blockId", ComparisonOperator.EQUALS, blockId);
        Collection<Block> features = session.loadAll(Block.class, new Filters().add(filter));
        if (features.size() > 1) {
            System.out.println("Database corrupted. Two or more IDs for a Block are not allowed.");
        }
        Iterator<Block> iter = features.iterator();
        if (iter.hasNext()) {
            return iter.next();
        }
        //print warning
        return null;
    }


    @Override
    public List<Block> getBlockByVCBlockState(DataState vcBlockState) {
        BlockService blockService = new BlockServiceImpl();
        String queryTemplate = "MATCH (b:Block{vcBlockState: '%s'}) return b";
        String query = String.format(queryTemplate, vcBlockState);
        System.out.println(query);
        Iterable<Map<String, Object>> queryResult = Neo4jSessionFactory.getInstance().getNeo4jSession()
                .query(query, Collections.EMPTY_MAP);
        List<Block> result = new LinkedList<>();
        queryResult.forEach(map -> {
            Block block = (Block) map.get("b");
            if (block != null) {
                Block fullBlock = blockService.getBlockByID(
                        block.getBlockId());
                result.add(fullBlock);
            }
        });
        return result;
    }

    @Override
    public List<ContainerToBlock> getContainerToBlockRelations() {
        String queryTemplate = "MATCH (c:Container)-[r:POINT_TO]->(b:Block) return c,b";
        Iterable<Map<String, Object>> queryResult = Neo4jSessionFactory.getInstance().getNeo4jSession()
                .query(queryTemplate, Collections.EMPTY_MAP);
        List<ContainerToBlock> result = new LinkedList<>();
        queryResult.forEach(map -> {
            Container container = (Container) map.get("c");
            Block block = (Block) map.get("b");
            ContainerToBlock rel = new ContainerToBlock();
            rel.setStartContainer(container);
            rel.setEndBlock(block);
            result.add(rel);
        });
        return result;
    }

    @Override
    public List<Block> getBlocksByFile(String pathToFile) {
        ContainerService containerService = new ContainerServiceImpl();
        BlockService blockService = new BlockServiceImpl();

        LinkedList<Block> linkedBlocks = Lists.newLinkedList();

        Container fullContainer = containerService.getContainerByID(pathToFile);
        ContainerToBlock containerToBlock = fullContainer.getBlock();
        Block simpleBlock = containerToBlock.getEndBlock();
        Block initialFullBlock = blockService.getBlockByID(simpleBlock.getBlockId());

        linkedBlocks.add(initialFullBlock);
        BlockToBlock blocktoBlock = initialFullBlock.getGoNextBlock();
        while (blocktoBlock != null && blocktoBlock.getEndBlock() != null) {
            Block simpleNextBlock = blocktoBlock.getEndBlock();
            Block fullNewBlock = blockService.getBlockByID(simpleNextBlock.getBlockId());
            linkedBlocks.add(fullNewBlock);
            blocktoBlock = fullNewBlock.getGoNextBlock();
        }
        return linkedBlocks;
    }

    @Override
    public Block getFirstBlockByFile(String pathToFile) {
        ContainerService containerService = new ContainerServiceImpl();
        BlockService blockService = new BlockServiceImpl();

        Container fullContainer = containerService.getContainerByID(pathToFile);
        ContainerToBlock containerToBlock = fullContainer.getBlock();
        Block simpleBlock = containerToBlock.getEndBlock();
        Block initialFullBlock = blockService.getBlockByID(simpleBlock.getBlockId());
        return initialFullBlock;
    }

    @Override
    public Class<Block> getEntityType() {
        return Block.class;
    }

    @Override
    public List<Block> getBlocksByFragment(String oldFragmentId) {
        String queryTemplate = "MATCH (b:Block)-[r:ASSOCIATED_TO]->(f:Fragment) return b,f";
        Iterable<Map<String, Object>> queryResult = Neo4jSessionFactory.getInstance().getNeo4jSession()
                .query(queryTemplate, Collections.EMPTY_MAP);
        List<Block> result = new LinkedList<>();
        queryResult.forEach(map -> {
            Fragment fragment = (Fragment) map.get("f");
            Block block = (Block) map.get("b");
            if (fragment != null && block != null) {
                if (fragment.getFragmentId().equals(oldFragmentId)) {
                    result.add(block);
                }
            }
        });
        return result;
    }
}