package ic.unicamp.fb.graph.neo4j.utils;

import ic.unicamp.fb.graph.neo4j.schema.Fragment;
import ic.unicamp.fb.graph.neo4j.services.FragmentService;

import static ic.unicamp.fb.cli.cmd.BMConfigure.FB_GENERIC_FRAGMENT;

public class FragmentUtil {
    public static Fragment retrieveOrCreateGenericFragment(FragmentService fragmentService) {
        Fragment defaultFragment = fragmentService.getFragmentByID(FB_GENERIC_FRAGMENT);
        if (defaultFragment == null) {
            defaultFragment = new Fragment();
            defaultFragment.setFragmentId(FB_GENERIC_FRAGMENT);
            defaultFragment.setFragmentLabel(FB_GENERIC_FRAGMENT);
        }
        return defaultFragment;
    }

    public static Fragment retrieveOrCreateAStandardFragment(FragmentService fragmentService, String fragmentId, String fragmentLabel) {
        Fragment standardFragment = fragmentService.getFragmentByID(fragmentId);
        if (standardFragment == null) {
            standardFragment = new Fragment();
            standardFragment.setFragmentId(fragmentId);
            standardFragment.setFragmentLabel(fragmentLabel);
        }
        return standardFragment;
    }
}
