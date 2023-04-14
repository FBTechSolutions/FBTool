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
    public Block getBlockByID(String productId) {
        Filter filter = new Filter("blockId", ComparisonOperator.EQUALS, productId);
        Collection<Block> features = session.loadAll(Block.class, new Filters().add(filter));
        if (features.size() > 1) {
            System.out.println("Two IDs for Product is not good");
        }
        Iterator<Block> iter = features.iterator();
        if (iter.hasNext()) {
            return iter.next();
        }
        return null;
    }


    @Override
    public List<Block> getBlockByVCBlockState(DataState dataState) {
        BlockService blockService = new BlockServiceImpl();
        System.out.println("Enter Query");
        String queryTemplate = "MATCH (b:Block{vcBlockState: '%s'}) return b";
        String query = String.format(queryTemplate, dataState);
        System.out.println(query);
        Iterable<Map<String, Object>> queryResult = Neo4jSessionFactory.getInstance().getNeo4jSession()
                .query(query, Collections.EMPTY_MAP);
        System.out.println(queryResult);
        List<Block> result = new LinkedList<>();
        queryResult.forEach(map -> {
            Block block = (Block) map.get("b");
            Block blockWithRelations = blockService.getBlockByID(
                    block.getBlockId()); // to optimized, relations is not saved
            result.add(blockWithRelations);
            System.out.println(blockWithRelations.getBlockId());
        });
        return result;
    }

    @Override
    public List<ContainerToBlock> getContainerToBlockRelations() {
        String queryTemplate = "MATCH (c:Container)-[r:POINT_TO]->(b:Block) return c,b";
        //String query = String.format(queryTemplate, productId);
        Iterable<Map<String, Object>> queryResult = Neo4jSessionFactory.getInstance().getNeo4jSession()
                .query(queryTemplate, Collections.EMPTY_MAP);
        List<ContainerToBlock> result = new LinkedList<>();
        queryResult.forEach(map -> {
            //ContainerToBlock relation = (ContainerToBlock)map.get("r");
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
    public List<Block> getBlocksByFile(String pathFile) {
        ContainerService containerService = new ContainerServiceImpl();
        BlockService blockService = new BlockServiceImpl();

        LinkedList<Block> blocks = Lists.newLinkedList();

        Container fullContainer = containerService.getContainerByID(pathFile);
        ContainerToBlock containerToBlockRelation = fullContainer.getBlock();
        Block simpleBlock = containerToBlockRelation.getEndBlock();
        Block initialFullBlock = blockService.getBlockByID(simpleBlock.getBlockId());
        blocks.add(initialFullBlock);
        BlockToBlock blockToBlockRelation = initialFullBlock.getGoNextBlock();
        while (blockToBlockRelation != null && blockToBlockRelation.getEndBlock() != null) {
            Block simpleNextBlock = blockToBlockRelation.getEndBlock();
            Block fullNewBlock = blockService.getBlockByID(simpleNextBlock.getBlockId());
            blocks.add(fullNewBlock);
            blockToBlockRelation = fullNewBlock.getGoNextBlock();
        }
        return blocks;
    }

    @Override
    public Block getFirstBlockByFile(String pathFile) {
        //LinkedList<Block> blocks = Lists.newLinkedList();
        ContainerService containerService = new ContainerServiceImpl();
        Container container = containerService.getContainerByID(pathFile);
        ContainerToBlock blockRelation = container.getBlock();
        Block block = blockRelation.getEndBlock();
        BlockService blockService = new BlockServiceImpl();
        Block initial = blockService.getBlockByID(block.getBlockId());
        return initial;
    }

    @Override
    public Class<Block> getEntityType() {
        return Block.class;
    }

    @Override
    public List<Block> getBlocksByFragment(String oldFragmentId) {
        String queryTemplate = "MATCH (b:Block)-[r:ASSOCIATED_TO]->(f:Fragment) return b,f";
        //String query = String.format(queryTemplate, productId);
        Iterable<Map<String, Object>> queryResult = Neo4jSessionFactory.getInstance().getNeo4jSession()
                .query(queryTemplate, Collections.EMPTY_MAP);
        List<Block> result = new LinkedList<>();
        queryResult.forEach(map -> {
            Fragment fragment = (Fragment) map.get("f");
            Block block = (Block) map.get("b");
            if (fragment.getFragmentId().equals(oldFragmentId)) {
                result.add(block);
            }
        });
        return result;
    }
}