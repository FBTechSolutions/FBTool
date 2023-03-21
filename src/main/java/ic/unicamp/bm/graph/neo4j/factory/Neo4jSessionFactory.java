package ic.unicamp.bm.graph.neo4j.factory;

import org.neo4j.ogm.config.Configuration;
import org.neo4j.ogm.session.Session;
import org.neo4j.ogm.session.SessionFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

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
                "ic.unicamp.bm.graph.neo4j.schema");

    }

    private void loadCredentialsFromFile() throws IOException {
        String configFilePath = "conf/config.properties";
        Properties prop = new Properties();
        prop.load(new FileInputStream(configFilePath));
        this.uri = prop.getProperty("neo4j_uri");
        this.user = prop.getProperty("neo4j_user");
        this.password = prop.getProperty("neo4j_password");
    }

    public Session getNeo4jSession() {
        return sessionFactory.openSession();
    }
}
