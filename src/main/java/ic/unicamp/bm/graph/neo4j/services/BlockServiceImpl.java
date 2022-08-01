package ic.unicamp.bm.graph.neo4j.services;

import ic.unicamp.bm.graph.neo4j.factory.Neo4jSessionFactory;
import ic.unicamp.bm.graph.neo4j.schema.Block;
import ic.unicamp.bm.graph.neo4j.schema.Feature;
import ic.unicamp.bm.graph.neo4j.schema.RawData;
import ic.unicamp.bm.graph.neo4j.schema.enums.DataState;
import ic.unicamp.bm.graph.neo4j.schema.relations.BlockToRawData;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.neo4j.ogm.cypher.ComparisonOperator;
import org.neo4j.ogm.cypher.Filter;
import org.neo4j.ogm.cypher.Filters;

public class BlockServiceImpl extends GenericService<Block> implements BlockService {

  @Override
  public Iterable<Block> findAll() {
    return session.loadAll(Block.class, 1);
  }

  @Override
  public Block getBlockByID(String productId) {
    Filter filter = new Filter("blockId", ComparisonOperator.EQUALS, productId);
    Collection<Block> features = session.loadAll(Block.class, new Filters().add(filter));
    if(features.size()>1){
      System.out.println("Two IDs for Product is not good");
    }
    Iterator<Block> iter = features.iterator();
    if(iter.hasNext()){
      return iter.next();
    }
    return null;
  }

  @Override
  public List<Block> getBlockByRawState(DataState dataState) {
    BlockService blockService = new BlockServiceImpl();
    System.out.println("Enter Query");
    String queryTemplate = "MATCH (b:Block)-[r:GET_RAW_DATA]->(e:RawData {currentState: '%s'}) return b";
    String query = String.format(queryTemplate, dataState);
    System.out.println(query);
    Iterable<Map<String, Object>> queryResult = Neo4jSessionFactory.getInstance().getNeo4jSession().query(query, Collections.EMPTY_MAP);
    System.out.println(queryResult);
    List<Block> result = new LinkedList<>();
    queryResult.forEach(map -> {
      Block block = (Block)map.get("b");
      Block blockWithRelations =  blockService.getBlockByID(block.getBlockId());
      result.add(blockWithRelations);
      System.out.println(blockWithRelations.getGetRawData());
      System.out.println(blockWithRelations.getBlockId());
    });
    return result;
  }

  @Override
  public Class<Block> getEntityType() {
    return Block.class;
  }

}