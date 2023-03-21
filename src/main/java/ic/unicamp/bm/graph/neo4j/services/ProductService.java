package ic.unicamp.bm.graph.neo4j.services;

import ic.unicamp.bm.graph.neo4j.schema.Feature;
import ic.unicamp.bm.graph.neo4j.schema.Product;

import java.util.List;
import java.util.Map;

public interface ProductService extends Service<Product> {

    Iterable<Map<String, Object>> getStudyBuddiesByPopularity();

    Product getProductByID(String productId);
}
