package ic.unicamp.bm.graph;

public class GraphBuilder {

  private static GraphManager graphManager;

  public static GraphManager createGraphInstance() {
    if (graphManager == null) {
      graphManager = new GraphManager();
    }
    return graphManager;
  }
}
