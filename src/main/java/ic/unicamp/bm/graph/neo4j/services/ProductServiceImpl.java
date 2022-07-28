package ic.unicamp.bm.graph.neo4j.services;

import ic.unicamp.bm.graph.neo4j.factory.Neo4jSessionFactory;
import ic.unicamp.bm.graph.neo4j.schema.Product;

import java.util.Collections;
import java.util.Map;

public class ProductServiceImpl extends GenericService<Product> implements ProductService {

  @Override
  public Iterable<Product> findAll() {
    return session.loadAll(Product.class, 1);
  }

  @Override
  public Iterable<Map<String, Object>> getStudyBuddiesByPopularity() {
    //String query = "MATCH (s:Feature)<-[:ASSOCIATED_TO]-(p:Product) return p, count(s) as buddies ORDER BY buddies DESC";
    String query = "MATCH (s:Feature)<-[:ASSOCIATED_TO]-(p:Product) return p";
    return Neo4jSessionFactory.getInstance().getNeo4jSession().query(query, Collections.EMPTY_MAP);
  }

  @Override
  public Class<Product> getEntityType() {
    return Product.class;
  }
}