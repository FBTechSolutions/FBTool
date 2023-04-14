package ic.unicamp.fb.graph.neo4j.services;

import ic.unicamp.fb.graph.neo4j.schema.Product;

public interface ProductService extends Service<Product> {

    Product getProductByID(String productId);
}
