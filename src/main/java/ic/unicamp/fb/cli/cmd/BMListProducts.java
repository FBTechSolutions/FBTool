package ic.unicamp.fb.cli.cmd;

import ic.unicamp.fb.graph.neo4j.services.ProductService;
import ic.unicamp.fb.graph.neo4j.services.ProductServiceImpl;
import picocli.CommandLine.Command;

import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Command(
        name = BMListProducts.CMD_NAME,
        description = "It will list all the products")
public class BMListProducts implements Runnable {

    public static final String CMD_NAME = "list-products";

    @Override
    public void run() {
        System.out.println("Listing all products...");
        ProductService productService = new ProductServiceImpl();
        String productList = StreamSupport
                .stream(productService.findAll().spliterator(), false)
                .map(product -> String.format("id:%s  label:%s", product.getProductId(), product.getProductLabel()))
                .collect(Collectors.joining("\n"));
        System.out.println(productList);
    }
}
