package ic.unicamp.fb.cli.cmd;

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
                for (ProductToFeature relation : product.getAssociatedTo()) {
                    System.out.println("  - " + relation.getEndFeature().getFeatureId() + "  " + relation.getEndFeature().getFeatureLabel());
                }
            }
            if (isFragmentEnabled) {
                List<ProductToFeature> featureList = product.getAssociatedTo();
                List<String> featureIds = FeatureUtil.retrieveFeatureIdsByRelation(featureList);
                List<Fragment> fragmentList = fragmentService.calculateFragmentsByFeatureList(featureIds);
                for (Fragment fragment : fragmentList) {
                    System.out.println("  - " + fragment.getFragmentId() + "  " + fragment.getFragmentLabel());
                }
            }
        }
    }
}
