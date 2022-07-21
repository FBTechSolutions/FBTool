package ic.unicamp.bm.cli.cmd;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

@Command(
    name = BMProject.CMD_NAME,
    description = "This command will project several SPL source code related to a list of products.")
public class BMProject implements Runnable {

  public static final String CMD_NAME = "project";

  @Option(names = "-clean")     boolean clean;

  @Parameters(index = "0..*")
  String[] products;

  @Override
  public void run() {
    System.out.println("Option clean " + clean);
    System.out.println("List of products:");
    for (String feature : products) {
      System.out.println(feature);
    }
  }
}
/*  private final GraphAPI graph = GraphBuilder.createGraphInstance();
  private final IBlockAPI gitBlock = GitBlockManager.createGitBlockInstance();
  private final Git git = (Git) gitBlock.retrieveDirector();*/

// blocks tags(bm project features_list/ default)
// without blocks tags ( bm project -prod)

/*    try {
      //
      git.checkout().setName("BM_ALL").setOrphan(true).call();

    } catch (GitAPIException e) {
      throw new RuntimeException(e);
    }*/
//process blocks by Feature (query in the DB)
//create new branch
//project files and stuff

//set an ID for the fist commit