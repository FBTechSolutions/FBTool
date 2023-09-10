package ic.unicamp.fb.graph.neo4j.utils;

import ic.unicamp.fb.graph.neo4j.schema.Feature;
import ic.unicamp.fb.graph.neo4j.schema.Fragment;
import ic.unicamp.fb.graph.neo4j.schema.relations.FragmentToIndex;
import ic.unicamp.fb.graph.neo4j.services.FragmentService;
import ic.unicamp.fb.graph.neo4j.services.IndexServiceImpl;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import static ic.unicamp.fb.cli.cmd.FBConfigure.FB_GENERIC_FRAGMENT;

public class FragmentUtil {
    public static Fragment retrieveOrCreateGenericFragmentBean(FragmentService fragmentService) {
        Fragment fullFragment = fragmentService.getFragmentByID(FB_GENERIC_FRAGMENT);
        if (fullFragment == null) {
            System.out.println("Creating new Fragment Bean" + FB_GENERIC_FRAGMENT);
            fullFragment = new Fragment();
            fullFragment.setFragmentId(FB_GENERIC_FRAGMENT);
            fullFragment.setFragmentLabel(FB_GENERIC_FRAGMENT);
        }
        return fullFragment;
    }

    public static Fragment retrieveOrCreateAStandardFragmentBean(FragmentService fragmentService, String fragmentId, String fragmentLabel) {
        Fragment fullFragment = fragmentService.getFragmentByID(fragmentId);
        if (fullFragment == null) {
            System.out.println("Creating new Fragment Bean");
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

    public static List<Fragment> orderFragments(Iterable<Fragment> fragmentIterable){
        List<Fragment> orderedFragments = new LinkedList<>();
        for (Fragment obj : fragmentIterable) {
            orderedFragments.add(obj);
        }
        Collections.sort(orderedFragments);
        return orderedFragments;
    }
}
