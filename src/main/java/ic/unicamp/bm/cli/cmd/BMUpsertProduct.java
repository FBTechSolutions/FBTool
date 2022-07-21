package ic.unicamp.bm.cli.cmd;

import ic.unicamp.bm.graph.GraphDBAPI;
import ic.unicamp.bm.graph.GraphDBBuilder;
import ic.unicamp.bm.graph.NodePart;
import ic.unicamp.bm.graph.schema.Feature;
import ic.unicamp.bm.graph.schema.Product;
import java.util.LinkedList;
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

  private final GraphDBAPI graphDB = GraphDBBuilder.createGraphInstance();

  @Override
  public void run() {
    if(StringUtils.isEmptyOrNull(productId)){
      System.out.println("This command requires an productId");
    }
    Product product = graphDB.retrieveProductWithFeatures(productId);
    if(product != null){
      //add product to JSON,
      //add features to JSON
      // commit all
    }else{
      LinkedList<Object> objects = new LinkedList<>();
      product = new Product();
      product.setProductId(productId);
      product.setLabel(productId);
      objects.add(product);
      for (String featureId : featureIds) {
        Feature feature = new Feature();
        feature.setFeatureId(featureId);
        feature.setLabel(featureId);
        objects.add(feature);
      }
      graphDB.insertObjects(objects, NodePart.VERTEX);
    }
    // preparateProduct
    // preparateFeatures

    //DB exists a product
    // if exits update features (remove not used)
    // if not create and update features

  }
}
