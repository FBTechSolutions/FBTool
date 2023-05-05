package ic.unicamp.fb.graph.neo4j.utils;

import ic.unicamp.fb.graph.neo4j.schema.Fragment;
import ic.unicamp.fb.graph.neo4j.services.FragmentService;

import static ic.unicamp.fb.cli.cmd.FBConfigure.FB_GENERIC_FRAGMENT;

public class FragmentUtil {
    public static Fragment retrieveOrCreateGenericFragmentBean(FragmentService fragmentService) {
        Fragment fullFragment = fragmentService.getFragmentByID(FB_GENERIC_FRAGMENT);
        if (fullFragment == null) {
            fullFragment = new Fragment();
            fullFragment.setFragmentId(FB_GENERIC_FRAGMENT);
            fullFragment.setFragmentLabel(FB_GENERIC_FRAGMENT);
        }
        return fullFragment;
    }

    public static Fragment retrieveOrCreateAStandardFragmentBean(FragmentService fragmentService, String fragmentId, String fragmentLabel) {
        Fragment fullFragment = fragmentService.getFragmentByID(fragmentId);
        if (fullFragment == null) {
            fullFragment = new Fragment();
            fullFragment.setFragmentId(fragmentId);
            fullFragment.setFragmentLabel(fragmentLabel);
        }
        return fullFragment;
    }

    public static boolean exitsFragmentBean(FragmentService fragmentService, String fragmentId) {
        Fragment fullFragment = fragmentService.getFragmentByID(fragmentId);
        return fullFragment != null;
    }
}
