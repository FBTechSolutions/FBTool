import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.janusgraph.core.JanusGraph;
import org.janusgraph.core.schema.JanusGraphManagement;


//importing an static method?
import static org.apache.tinkerpop.gremlin.process.traversal.AnonymousTraversalSource.traversal;

public class HelloJGraph {
  public static void main(String[] args) {

    try {
      GraphTraversalSource g = traversal().withRemote("conf/remote-graph.properties");

     // Object herculesAge = g.V().has("name", "hercules").values("age").next();
      //System.out.println("Hercules is " + herculesAge + " years old.");
      g.addV("dsads");

      System.out.println(g.toString());
      g.close();
    } catch (Exception e) {
      e.printStackTrace();
    }

  }
  public void createSchema(JanusGraph graph){

    JanusGraphManagement mgmt = graph.openManagement();
    mgmt.printSchema();
  }

}
