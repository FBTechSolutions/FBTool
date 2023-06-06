package ic.unicamp.fb.graph.neo4j.factory;

import org.eclipse.jgit.util.StringUtils;
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
        this.uri = "neo4j://localhost:7687";
        this.user = "neo4j";
        this.password = "password";
    }

    public void reWriteCredentials(String user, String password, String uri) {
        if (StringUtils.isEmptyOrNull(uri)) {
            this.uri = "neo4j://localhost:7687";
        } else {
            this.uri = uri;
        }
        this.user = user;
        this.password = password;
        Configuration configuration = new Configuration.Builder()
                .uri(uri)
                .credentials(user, password)
                .build();
        sessionFactory = new SessionFactory(configuration,
                "ic.unicamp.fb.graph.neo4j.schema");
    }

    public Session getNeo4jSession() {
        return sessionFactory.openSession();
    }
}
