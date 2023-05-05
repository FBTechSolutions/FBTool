package ic.unicamp.fb.cli.cmd;

import ic.unicamp.fb.graph.neo4j.schema.Fragment;
import ic.unicamp.fb.graph.neo4j.services.FragmentService;
import ic.unicamp.fb.graph.neo4j.services.FragmentServiceImpl;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

@Command(
        name = FBRemoveFragments.CMD_NAME,
        description = "This command will remove a list of fragments")
public class FBRemoveFragments implements Runnable {

    public static final String CMD_NAME = "remove-fragments";

    @Parameters(index = "0..*")
    String[] fragmentIds;

    @Override
    public void run() {
        if (fragmentIds == null) {
            System.out.println("You need to specify at least one Fragment Id");
            return;
        }
        FragmentService fragmentService = new FragmentServiceImpl();
        for (String fragmentId : fragmentIds) {
            Fragment fullFragment = fragmentService.getFragmentByID(fragmentId);
            if (fullFragment != null) {
                fragmentService.delete(fullFragment.getId());
            }
        }
    }
}
