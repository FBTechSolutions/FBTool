package ic.unicamp.bm.graph.neo4j.factory;

import org.neo4j.ogm.config.Configuration;
import org.neo4j.ogm.session.Session;
import org.neo4j.ogm.session.SessionFactory;

public class Neo4jSessionFactory {

/*  private final static Configuration configuration = new Configuration.Builder()
      .uri("neo4j+s://d5e5befc.databases.neo4j.io")
      .credentials("neo4j", "JVLfEGaDAfV3ziOHmrRSk4JNd3j-9XuOKQVXhHp-pz4")
      .build();*/
  private final static Configuration configuration = new Configuration.Builder()
      .uri("neo4j://localhost:7687")
      .credentials("neo4j", "test") // neo4j, test
      .build();
  private final static SessionFactory sessionFactory = new SessionFactory(configuration,
      "ic.unicamp.bm.graph.neo4j.schema");
  private static Neo4jSessionFactory factory = new Neo4jSessionFactory();

  public static Neo4jSessionFactory getInstance() {
    return factory;
  }

  // prevent external instantiation
  private Neo4jSessionFactory() {
  }

  public Session getNeo4jSession() {
    return sessionFactory.openSession();
  }
}
