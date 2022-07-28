package ic.unicamp.bm.graph.neo4j;


import ic.unicamp.bm.graph.neo4j.schema.Feature;
import ic.unicamp.bm.graph.neo4j.schema.Product;
import ic.unicamp.bm.graph.neo4j.schema.relations.ProductToFeature;
import ic.unicamp.bm.graph.neo4j.services.ProductService;
import ic.unicamp.bm.graph.neo4j.services.ProductServiceImpl;
import java.util.LinkedList;
import java.util.List;

public class DriverNeo4jTest{

  public static void main(String... args) throws Exception {
    ProductService productService = new ProductServiceImpl();

    Product product3 = new Product();
    product3.setProductId("P4");
    product3.setProductLabel("P4");

    Feature feature1 = new Feature();
    feature1.setFeatureId("F1A");
    feature1.setFeatureLabel("F1A");

    Feature feature2 = new Feature();
    feature2.setFeatureId("F2A");
    feature2.setFeatureLabel("F2A");

    Feature feature3 = new Feature();
    feature3.setFeatureId("F3A");
    feature3.setFeatureLabel("F3A");

    Feature feature4 = new Feature();
    feature4.setFeatureId("F4A");
    feature4.setFeatureLabel("F4A");

    ProductToFeature re1 = new ProductToFeature();
    re1.setName("RE1A");
    ProductToFeature re2 = new ProductToFeature();
    re1.setName("RE2A");
    ProductToFeature re3 = new ProductToFeature();
    re1.setName("RE3A");

    List<ProductToFeature> featureList = new LinkedList<>();
    re1.setStartProduct(product3);
    re1.setEndFeature(feature1);

    re2.setStartProduct(product3);
    re2.setEndFeature(feature2);

    re3.setStartProduct(product3);
    re3.setEndFeature(feature3);

    featureList.add(re1);
    featureList.add(re2);
    featureList.add(re3);
    product3.setAssociatedTo(featureList);
    productService.createOrUpdate(product3);
    System.out.println("");
    System.out.println("END");
  }
}
