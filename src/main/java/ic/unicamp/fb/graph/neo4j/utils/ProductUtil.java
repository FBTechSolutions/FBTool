package ic.unicamp.fb.graph.neo4j.utils;

import ic.unicamp.fb.graph.neo4j.schema.Product;
import ic.unicamp.fb.graph.neo4j.services.ProductService;

import static ic.unicamp.fb.cli.cmd.FBConfigure.FB_GENERIC_SPL_PROD;

public class ProductUtil {
    public static Product retrieveOrCreateGenericProductBean(ProductService productService) {
        Product fullProduct = productService.getProductByID(FB_GENERIC_SPL_PROD);
        if (fullProduct == null) {
            fullProduct = new Product();
            fullProduct.setProductId(FB_GENERIC_SPL_PROD);
            fullProduct.setProductLabel(FB_GENERIC_SPL_PROD);
        }
        return fullProduct;
    }

    public static Product retrieveOrCreateAStandardProductBean(ProductService productService, String productId, String productLabel) {
        Product fullProduct = productService.getProductByID(productId);
        if (fullProduct == null) {
            fullProduct = new Product();
            fullProduct.setProductId(productId);
            fullProduct.setProductLabel(productLabel);
        }
        return fullProduct;
    }
}
