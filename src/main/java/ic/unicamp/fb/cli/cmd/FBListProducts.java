package ic.unicamp.fb.cli.cmd;

import ic.unicamp.fb.graph.neo4j.schema.Feature;
import ic.unicamp.fb.graph.neo4j.schema.Fragment;
import ic.unicamp.fb.graph.neo4j.schema.Product;
import ic.unicamp.fb.graph.neo4j.schema.relations.ProductToFeature;
import ic.unicamp.fb.graph.neo4j.services.FragmentService;
import ic.unicamp.fb.graph.neo4j.services.FragmentServiceImpl;
import ic.unicamp.fb.graph.neo4j.services.ProductService;
import ic.unicamp.fb.graph.neo4j.services.ProductServiceImpl;
import ic.unicamp.fb.graph.neo4j.utils.FeatureUtil;
import picocli.CommandLine;
import picocli.CommandLine.Command;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

@Command(
        name = FBListProducts.CMD_NAME,
        description = "It will list all the products")
public class FBListProducts implements Runnable {

    public static final String CMD_NAME = "list-products";
    @CommandLine.Option(names = {"--f", "--features"}, description = "Add features to the list")
    private boolean isFeatureEnabled;

    @CommandLine.Option(names = {"--frag", "--fragments"}, description = "Add fragments to the list")
    private boolean isFragmentEnabled;

    @Override
    public void run() {
        System.out.println("Listing all products...");
        ProductService productService = new ProductServiceImpl();
        FragmentService fragmentService = new FragmentServiceImpl();

        for (Product product : productService.findAll()) {
            System.out.printf("- id:%s  label:%s%n", product.getProductId(), product.getProductLabel());
            if (isFeatureEnabled) {
                System.out.println("  features ...");
                List<Feature> features = new LinkedList<>();
                for (ProductToFeature relation : product.getAssociatedTo()) {
                    features.add(relation.getEndFeature());
                }
                Collections.sort(features);
                for (Feature feature : features) {
                    System.out.println("  - " + feature.getFeatureId() + "  " + feature.getFeatureLabel());
                }
            }
            if (isFragmentEnabled) {
                System.out.println("  calculating fragments ...");
                List<ProductToFeature> featureList = product.getAssociatedTo();
                List<String> featureIds = FeatureUtil.retrieveFeatureIdsByProductToFeatureRelation(featureList);
                //Collections.sort(featureIds);
                List<Fragment> fragmentList = fragmentService.calculateFragmentsByFeatureList(featureIds);
                Collections.sort(fragmentList);
                for (Fragment fragment : fragmentList) {
                    System.out.println("  - " + fragment.getFragmentId() + "  " + fragment.getFragmentLabel());
                }
            }
        }
    }
}
