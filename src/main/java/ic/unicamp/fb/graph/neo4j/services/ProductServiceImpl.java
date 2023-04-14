package ic.unicamp.fb.graph.neo4j.services;

import ic.unicamp.fb.graph.neo4j.schema.Product;
import org.neo4j.ogm.cypher.ComparisonOperator;
import org.neo4j.ogm.cypher.Filter;
import org.neo4j.ogm.cypher.Filters;

import java.util.Collection;
import java.util.Iterator;

public class ProductServiceImpl extends GenericService<Product> implements ProductService {

    @Override
    public Iterable<Product> findAll() {
        return session.loadAll(Product.class, 1);
    }

    @Override
    public Product getProductByID(String productId) {
        Filter filter = new Filter("productId", ComparisonOperator.EQUALS, productId);
        Collection<Product> products = session.loadAll(Product.class, new Filters().add(filter));
        if (products.size() > 1) {
            System.out.println("Database corrupted. Two or more IDs for a Product are not allowed.");
        }
        Iterator<Product> iter = products.iterator();
        if (iter.hasNext()) {
            return iter.next();
        }
        //print warning
        return null;
    }

    @Override
    public Class<Product> getEntityType() {
        return Product.class;
    }

}