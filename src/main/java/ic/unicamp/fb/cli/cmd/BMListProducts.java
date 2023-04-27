package ic.unicamp.fb.cli.cmd;

import ic.unicamp.fb.graph.neo4j.schema.Product;
import ic.unicamp.fb.graph.neo4j.schema.relations.ProductToFeature;
import ic.unicamp.fb.graph.neo4j.services.ProductService;
import ic.unicamp.fb.graph.neo4j.services.ProductServiceImpl;
import picocli.CommandLine;
import picocli.CommandLine.Command;

@Command(
        name = BMListProducts.CMD_NAME,
        description = "It will list all the products")
public class BMListProducts implements Runnable {

    public static final String CMD_NAME = "list-products";
    @CommandLine.Option(names = {"-f", "--features"}, description = "Add features to the list")
    private boolean isFeatureEnabled;

    @Override
    public void run() {
        System.out.println("Listing all products...");
        ProductService productService = new ProductServiceImpl();
        for (Product product : productService.findAll()) {
            System.out.printf("- id:%s  label:%s%n", product.getProductId(), product.getProductLabel());
            if (isFeatureEnabled) {
                for (ProductToFeature relation : product.getAssociatedTo()) {
                    System.out.println("  - " + relation.getEndFeature().getFeatureId() + "  " + relation.getEndFeature().getFeatureLabel());
                }
            }
        }
    }
}
