package ic.unicamp.bm.graph.schema;

public class GraphBuilder {

  private GraphManager graphManager;

  public GraphManager createGraphInstance() {
    if (graphManager == null) {
      graphManager = new GraphManager();
    }
    return graphManager;
  }
}
