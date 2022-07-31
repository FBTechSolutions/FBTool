package ic.unicamp.bm.graph.neo4j.services;

import ic.unicamp.bm.graph.neo4j.factory.Neo4jSessionFactory;
import ic.unicamp.bm.graph.neo4j.schema.Product;
import ic.unicamp.bm.graph.neo4j.schema.RawData;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import org.neo4j.ogm.cypher.ComparisonOperator;
import org.neo4j.ogm.cypher.Filter;
import org.neo4j.ogm.cypher.Filters;

public class RawDataServiceImpl extends GenericService<RawData> implements RawDataService {

  @Override
  public Iterable<RawData> findAll() {
    return session.loadAll(RawData.class, 1);
  }

  @Override
  public RawData getRawDataByID(String productId) {
   /* String queryTemplate = "MATCH (p:Product {productId: '%s'}) return p";
    String query = String.format(queryTemplate, productId);
    return Neo4jSessionFactory.getInstance().getNeo4jSession().query(query, Collections.EMPTY_MAP);*/
    Filter filter = new Filter("productId", ComparisonOperator.EQUALS, productId);
    Collection<RawData> products = session.loadAll(RawData.class, new Filters().add(filter));
    if(products.size()>1){
      System.out.println("Two IDs for Product is not good");
    }
    Iterator<RawData> iter = products.iterator();
    if(iter.hasNext()){
      return iter.next();
    }
    return null;
  }

  @Override
  public Class<RawData> getEntityType() {
    return RawData.class;
  }

}