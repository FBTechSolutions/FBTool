package ic.unicamp.fb.cli.cmd;

import ic.unicamp.fb.cli.util.logger.SplMgrLogger;
import ic.unicamp.fb.graph.neo4j.factory.Neo4jSessionFactory;
import picocli.CommandLine;

import static ic.unicamp.fb.cli.util.msg.InfoMessages.INF_0__SPLM_VERSION;

@CommandLine.Command(
        name = FBDBConnection.CMD_NAME,
        description = "With this command you can connect to the database")
public class FBDBConnection implements Runnable {

    public static final String CMD_NAME = "db-connection";

    @CommandLine.Parameters(index = "0", description = "user", defaultValue = "neo4j")
    String user;
    @CommandLine.Parameters(index = "1", description = "password", defaultValue = "password")
    String password;
    @CommandLine.Parameters(index = "2", description = "uri", defaultValue = "")
    String uri;

    @Override
    public void run() {
        SplMgrLogger.message_ln(INF_0__SPLM_VERSION, false);
        Neo4jSessionFactory.getInstance().reWriteCredentials(user, password, uri);
        System.out.println("DB credential setup dynamically");
    }
}
