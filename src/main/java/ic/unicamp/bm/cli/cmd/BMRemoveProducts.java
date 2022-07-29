package ic.unicamp.bm.cli.cmd;

import ic.unicamp.bm.graph.neo4j.schema.Feature;
import ic.unicamp.bm.graph.neo4j.schema.Product;
import ic.unicamp.bm.graph.neo4j.services.FeatureService;
import ic.unicamp.bm.graph.neo4j.services.FeatureServiceImpl;
import ic.unicamp.bm.graph.neo4j.services.ProductService;
import ic.unicamp.bm.graph.neo4j.services.ProductServiceImpl;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

@Command(
    name = BMRemoveProducts.CMD_NAME,
    description = "This command will remove a list of products")
public class BMRemoveProducts implements Runnable {

  public static final String CMD_NAME = "remove-products";

  @Parameters(index = "0..*")
  String[] productIds;

  @Override
  public void run() {
    System.out.println("Remove Products");
    ProductService productService = new ProductServiceImpl();
    for (String productId : productIds) {
      Product feature = productService.getProductByID(productId);
      productService.delete(feature.getId());
    }
  }
}
