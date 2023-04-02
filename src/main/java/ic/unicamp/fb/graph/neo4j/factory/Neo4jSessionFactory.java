package ic.unicamp.fb.graph.neo4j.factory;

import org.neo4j.ogm.config.Configuration;
import org.neo4j.ogm.session.Session;
import org.neo4j.ogm.session.SessionFactory;

import java.io.IOException;

public class Neo4jSessionFactory {
    private String uri;
    private String user;
    private String password;
    private SessionFactory sessionFactory;
    private static final Neo4jSessionFactory factory = new Neo4jSessionFactory();

    public static Neo4jSessionFactory getInstance() {
        return factory;
    }

    // prevent external instantiation
    private Neo4jSessionFactory() {

        try {
            loadCredentialsFromFile();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        Configuration configuration = new Configuration.Builder()
                .uri(uri)
                .credentials(user, password) // neo4j, test
                .build();
        sessionFactory = new SessionFactory(configuration,
                "ic.unicamp.fb.graph.neo4j.schema");

    }

    private void loadCredentialsFromFile() throws IOException {
/*        System.out.println("Working Directory = " + System.getProperty("user.dir"));
        String configFilePath = "main/resources/config.properties";
        Properties prop = new Properties();
        FileInputStream fileInputStream = new FileInputStream(configFilePath);
        prop.load(fileInputStream);
        this.uri = prop.getProperty("neo4j_uri");
        this.user = prop.getProperty("neo4j_user");
        this.password = prop.getProperty("neo4j_password");*/
        // FIXME : Fix loading properties.
        this.uri = "neo4j://localhost:7687";
        this.user = "neo4j";
        this.password = "12345678";
    }

    public Session getNeo4jSession() {
        return sessionFactory.openSession();
    }
}
