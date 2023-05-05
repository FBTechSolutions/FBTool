package ic.unicamp.fb.cli.cmd;

import ic.unicamp.fb.graph.neo4j.schema.Feature;
import ic.unicamp.fb.graph.neo4j.schema.Product;
import ic.unicamp.fb.graph.neo4j.schema.relations.ProductToFeature;
import ic.unicamp.fb.graph.neo4j.services.FeatureService;
import ic.unicamp.fb.graph.neo4j.services.FeatureServiceImpl;
import ic.unicamp.fb.graph.neo4j.services.ProductService;
import ic.unicamp.fb.graph.neo4j.services.ProductServiceImpl;
import org.eclipse.jgit.util.StringUtils;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

import java.util.LinkedList;
import java.util.List;

//ok
@Command(
        name = FBUpsertProduct.CMD_NAME,
        description = "This command will create or update a product")
public class FBUpsertProduct implements Runnable {

    public static final String CMD_NAME = "upsert-product";

    @CommandLine.Option(names = {"--add", "--adding"}, description = "Add feature to Product", defaultValue = "false")
    private boolean isAddingEnabled;

    @CommandLine.Option(names = {"--rm", "--removing"}, description = "remove feature to Product", defaultValue = "false")
    private boolean isRemovingEnabled;
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
        if (featureIds == null) {
            System.out.println("This command requires an at least a feature");
            return;
        }

        ProductService productService = new ProductServiceImpl();
        FeatureService featureService = new FeatureServiceImpl();
        Product product = productService.getProductByID(productId);
        if (!isRemovingEnabled) {
            if (product == null) {
                product = new Product();
                product.setProductId(productId);
                product.setProductLabel(productId);
            }
            List<ProductToFeature> updatedFeatures;
            if (isAddingEnabled) {
                updatedFeatures = product.getAssociatedTo();
            } else {
                updatedFeatures = new LinkedList<>();
            }

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
        } else {
            if (product == null) {
                System.out.println("Required a valid productID");
                return;
            }
            List<ProductToFeature> removeToList = new LinkedList<>();
            List<ProductToFeature> productToFeature = product.getAssociatedTo();
            for (String featureId : featureIds) {
                Feature feature = featureService.getFeatureByID(featureId);
                if (feature != null) {
                    if (productToFeature != null) {
                        for (ProductToFeature removeRelation : productToFeature) {
                            Feature toRemove = removeRelation.getEndFeature();
                            if (toRemove != null && feature.getFeatureId().equals(toRemove.getFeatureId())) {
                                removeToList.add(removeRelation);
                            }
                        }
                    }
                }
            }
            for (ProductToFeature relation : removeToList) {
                productToFeature.remove(relation);
            }
            product.setAssociatedTo(productToFeature);
            productService.createOrUpdate(product);
        }
    }
}
