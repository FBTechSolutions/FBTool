package ic.unicamp.fb.cli.cmd;

import ic.unicamp.fb.graph.neo4j.schema.Feature;
import ic.unicamp.fb.graph.neo4j.schema.Product;
import ic.unicamp.fb.graph.neo4j.schema.relations.ProductToFeature;
import ic.unicamp.fb.graph.neo4j.services.FeatureService;
import ic.unicamp.fb.graph.neo4j.services.FeatureServiceImpl;
import ic.unicamp.fb.graph.neo4j.services.ProductService;
import ic.unicamp.fb.graph.neo4j.services.ProductServiceImpl;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.jgit.util.StringUtils;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

//ok
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
        if (StringUtils.isEmptyOrNull(productId)) {
            System.out.println("This command requires an productId");
            return;
        }

        ProductService productService = new ProductServiceImpl();
        FeatureService featureService = new FeatureServiceImpl();
        Product product = productService.getProductByID(productId);
        if (product == null) {
            product = new Product();
            product.setProductId(productId);
            product.setProductLabel(productId);
        }
        List<ProductToFeature> updatedFeatures = new LinkedList<>();
        for (String featureId : featureIds) {
            ProductToFeature relation = new ProductToFeature();
            Feature feature = featureService.getFeatureByID(featureId);
            if (feature == null) {
                feature = new Feature();
                feature.setFeatureId(featureId);
                feature.setFeatureLabel(featureId);
            }
            relation.setStartProduct(product);
            relation.setEndFeature(feature);
            updatedFeatures.add(relation);
        }
        product.setAssociatedTo(updatedFeatures);
        productService.createOrUpdate(product);
    }
}
