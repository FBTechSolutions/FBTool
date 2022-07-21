package ic.unicamp.bm.graph;

public class GraphDBBuilder {

  private static GraphDBManager graphManager;

  public static GraphDBManager createGraphInstance() {
    if (graphManager == null) {
      graphManager = new GraphDBManager();
    }
    return graphManager;
  }
}
