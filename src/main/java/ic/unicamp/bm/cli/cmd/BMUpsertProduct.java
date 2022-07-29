package ic.unicamp.bm.cli.cmd;

import ic.unicamp.bm.graph.neo4j.schema.Feature;
import ic.unicamp.bm.graph.neo4j.schema.Product;
import ic.unicamp.bm.graph.neo4j.schema.relations.ProductToFeature;
import ic.unicamp.bm.graph.neo4j.services.ProductService;
import ic.unicamp.bm.graph.neo4j.services.ProductServiceImpl;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.jgit.util.StringUtils;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;
//“update” and “insert.”
@Command(
    name = BMUpsertProduct.CMD_NAME,
    description = "This command will create or update a product")
public class BMUpsertProduct implements Runnable {

  public static final String CMD_NAME = "upsert-product";

  @Parameters(index = "0", description = "productId (required)", defaultValue = "")
  String productId;

  @Parameters(index = "1..*")
  String[] featureIds;


  @Override
  public void run() {
    System.out.println("Enter UPSERT");
    if(StringUtils.isEmptyOrNull(productId)){
      System.out.println("This command requires an productId");
    }
    ProductService productService = new ProductServiceImpl();
    Product product = productService.getProductByID(productId);
    if(product == null){
      product = new Product();
      product.setProductId(productId);
      product.setProductLabel(productId);
    }
    List<ProductToFeature> featureList = new LinkedList<>();
    //iter instead of foreach
    for (String featureId : featureIds) {
      ProductToFeature relation = new ProductToFeature();
      relation.setName("relation");

      Feature feature = new Feature();
      feature.setFeatureId(featureId);
      feature.setFeatureLabel(featureId);

      relation.setStartProduct(product);
      relation.setEndFeature(feature);
      featureList.add(relation);
    }
    product.setAssociatedTo(featureList);
    productService.createOrUpdate(product);
  }
}
