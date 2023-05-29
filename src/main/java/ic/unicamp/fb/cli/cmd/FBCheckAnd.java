package ic.unicamp.fb.cli.cmd;

import ic.unicamp.fb.graph.neo4j.schema.BitOrder;
import ic.unicamp.fb.graph.neo4j.schema.Fragment;
import ic.unicamp.fb.graph.neo4j.services.BitOrderService;
import ic.unicamp.fb.graph.neo4j.services.BitOrderServiceImpl;
import ic.unicamp.fb.graph.neo4j.services.FeatureService;
import ic.unicamp.fb.graph.neo4j.services.FeatureServiceImpl;
import ic.unicamp.fb.graph.neo4j.services.FragmentService;
import ic.unicamp.fb.graph.neo4j.services.FragmentServiceImpl;
import ic.unicamp.fb.graph.neo4j.utils.FeatureUtil;
import picocli.CommandLine;
import picocli.CommandLine.Command;

import java.util.LinkedList;

@Command(
        name = FBCheckAnd.CMD_NAME,
        description = "This command will check if an and fragment between feature exits")
public class FBCheckAnd implements Runnable {


    public static final String CMD_NAME = "check-and";

    @CommandLine.Parameters(index = "0..*")
    String[] featuresIds;

    @Override
    public void run() {
        FeatureService featureService = new FeatureServiceImpl();
        LinkedList<String> featureList = new LinkedList<>();
        for (String featuresId : featuresIds) {
            if(FeatureUtil.exitsFeatureBean(featureService, featuresId)){
                System.out.println("Feature Found " + featuresId);
                featureList.add(featuresId);
            }else{
                System.out.println("Feature Not Found " + featuresId);
            }
        }
        LinkedList<Integer> bitOrderList = new LinkedList<>();
        BitOrderService bitOrderService = new BitOrderServiceImpl();
        for (String featureId : featureList) {
           BitOrder bitOrder =  bitOrderService.getBitOrderByFeature(featureId);
           if(bitOrder!=null){
               bitOrderList.add(bitOrder.getBitOrderId());
           }else{
               System.out.println("Bit order mot Found for feature " + featureId);
           }
        }
        FragmentService fragmentService = new FragmentServiceImpl();
        Fragment fragment =fragmentService.retrieveUniqueAndFragment(bitOrderList);
        if(fragment!=null){
            System.out.println("Fragment already exits:");
            System.out.println(fragment.getFragmentId() + " " + fragment.getFragmentLabel());
        }else{
            System.out.println("Fragment does not exits");
        }
    }
}
