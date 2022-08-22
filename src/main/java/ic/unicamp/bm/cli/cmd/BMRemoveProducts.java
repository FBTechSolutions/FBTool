package ic.unicamp.bm.cli.cmd;

import ic.unicamp.bm.graph.neo4j.schema.Product;
import ic.unicamp.bm.graph.neo4j.services.ProductService;
import ic.unicamp.bm.graph.neo4j.services.ProductServiceImpl;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

@Command(
    name = BMRemoveProducts.CMD_NAME,
    description = "This command will remove a list of products")
public class BMRemoveProducts implements Runnable {

  public static final String CMD_NAME = "rm-products";

  @Parameters(index = "0..*")
  String[] productIds;

  @Override
  public void run() {
    if (productIds == null) {
      System.out.println("You need to specify at least one Product Id");
      return;
    }
    ProductService productService = new ProductServiceImpl();
    for (String productId : productIds) {
      Product feature = productService.getProductByID(productId);
      productService.delete(feature.getId());
    }
  }
}
