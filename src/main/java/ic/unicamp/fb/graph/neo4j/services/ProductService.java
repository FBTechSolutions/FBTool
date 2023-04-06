package ic.unicamp.fb.graph.neo4j.services;

import ic.unicamp.fb.graph.neo4j.schema.Product;

import java.util.Map;

public interface ProductService extends Service<Product> {

    Iterable<Map<String, Object>> getStudyBuddiesByPopularity();

    Product getProductByID(String productId);
}