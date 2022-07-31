package ic.unicamp.bm.graph.neo4j.services;

import ic.unicamp.bm.graph.neo4j.schema.Block;
import ic.unicamp.bm.graph.neo4j.schema.Feature;
import java.util.Collection;
import java.util.Iterator;
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
    Filter filter = new Filter("featureId", ComparisonOperator.EQUALS, productId);
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
  public Class<Block> getEntityType() {
    return Block.class;
  }

}