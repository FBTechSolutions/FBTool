package ic.unicamp.fb.graph.neo4j.utils;

import ic.unicamp.fb.graph.neo4j.schema.Fragment;
import ic.unicamp.fb.graph.neo4j.services.FragmentService;

import static ic.unicamp.fb.cli.cmd.BMConfigure.FB_GENERIC_FRAGMENT;

public class FragmentUtil {
    public static Fragment retrieveOrCreateGenericFragment(FragmentService fragmentService) {
        Fragment fullFragment = fragmentService.getFragmentByID(FB_GENERIC_FRAGMENT);
        if (fullFragment == null) {
            fullFragment = new Fragment();
            fullFragment.setFragmentId(FB_GENERIC_FRAGMENT);
            fullFragment.setFragmentCode(FB_GENERIC_FRAGMENT);
            fullFragment.setFragmentLabel(FB_GENERIC_FRAGMENT);
        }
        return fullFragment;
    }

    public static Fragment retrieveOrCreateAStandardFragment(FragmentService fragmentService, String fragmentId, String fragmentCode, String fragmentLabel) {
        Fragment fullFragment = fragmentService.getFragmentByID(fragmentId);
        if (fullFragment == null) {
            fullFragment = new Fragment();
            fullFragment.setFragmentId(fragmentId);
            fullFragment.setFragmentCode(fragmentCode);
            fullFragment.setFragmentLabel(fragmentLabel);
        }
        return fullFragment;
    }
}
