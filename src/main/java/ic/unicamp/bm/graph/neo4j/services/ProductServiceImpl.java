package ic.unicamp.bm.graph.neo4j.services;

import ic.unicamp.bm.graph.neo4j.factory.Neo4jSessionFactory;
import ic.unicamp.bm.graph.neo4j.schema.Product;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import org.neo4j.ogm.cypher.ComparisonOperator;
import org.neo4j.ogm.cypher.Filter;
import org.neo4j.ogm.cypher.Filters;

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
  public Product getProductByID(String productId) {
   /* String queryTemplate = "MATCH (p:Product {productId: '%s'}) return p";
    String query = String.format(queryTemplate, productId);
    return Neo4jSessionFactory.getInstance().getNeo4jSession().query(query, Collections.EMPTY_MAP);*/
    Filter filter = new Filter("productId", ComparisonOperator.EQUALS, productId);
    Collection<Product> products = session.loadAll(Product.class, new Filters().add(filter));
    if (products.size() > 1) {
      System.out.println("Two IDs for Product is not good");
    }
    Iterator<Product> iter = products.iterator();
    if (iter.hasNext()) {
      return iter.next();
    }
    return null;
  }

  @Override
  public Class<Product> getEntityType() {
    return Product.class;
  }

}