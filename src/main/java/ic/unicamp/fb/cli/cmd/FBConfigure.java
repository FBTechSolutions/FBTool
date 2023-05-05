package ic.unicamp.fb.cli.cmd;

import ic.unicamp.fb.block.GitVCSManager;
import ic.unicamp.fb.block.IVCSAPI;
import ic.unicamp.fb.block.utils.BMDirectoryUtil;
import ic.unicamp.fb.block.utils.GitDirectoryUtil;
import ic.unicamp.fb.cli.util.logger.SplMgrLogger;
import ic.unicamp.fb.graph.neo4j.schema.BitOrder;
import ic.unicamp.fb.graph.neo4j.schema.FBToolConfiguration;
import ic.unicamp.fb.graph.neo4j.schema.Feature;
import ic.unicamp.fb.graph.neo4j.schema.Fragment;
import ic.unicamp.fb.graph.neo4j.schema.Product;
import ic.unicamp.fb.graph.neo4j.schema.relations.FeatureToBitOrder;
import ic.unicamp.fb.graph.neo4j.schema.relations.FragmentToBitOrder;
import ic.unicamp.fb.graph.neo4j.schema.relations.ProductToFeature;
import ic.unicamp.fb.graph.neo4j.services.BMConfigService;
import ic.unicamp.fb.graph.neo4j.services.BMConfigServiceImpl;
import ic.unicamp.fb.graph.neo4j.services.BitOrderService;
import ic.unicamp.fb.graph.neo4j.services.BitOrderServiceImpl;
import ic.unicamp.fb.graph.neo4j.services.FeatureService;
import ic.unicamp.fb.graph.neo4j.services.FeatureServiceImpl;
import ic.unicamp.fb.graph.neo4j.services.FragmentService;
import ic.unicamp.fb.graph.neo4j.services.FragmentServiceImpl;
import ic.unicamp.fb.graph.neo4j.services.ProductService;
import ic.unicamp.fb.graph.neo4j.services.ProductServiceImpl;
import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import picocli.CommandLine.Command;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import static ic.unicamp.fb.block.GitVCS.BMBranchLabel;
import static ic.unicamp.fb.graph.neo4j.utils.BitOrderUtil.retrieveOrCreateGenericBitOrderBean;
import static ic.unicamp.fb.graph.neo4j.utils.FeatureUtil.retrieveOrCreateGenericFeatureBean;
import static ic.unicamp.fb.graph.neo4j.utils.FragmentUtil.retrieveOrCreateGenericFragmentBean;
import static ic.unicamp.fb.graph.neo4j.utils.ProductUtil.retrieveOrCreateGenericProductBean;

@Command(
        name = FBConfigure.CMD_NAME,
        description = "It will set up the initial configuration required to use the tool, including the default values")
public class FBConfigure implements Runnable {

    public static final String CMD_NAME = "configure";
    public static final String FB_GENERIC_SPL_PROD = "FB_Generic_SPL_Prod";
    public static final String FB_GENERIC_FEATURE = "FB_Generic_Feature";
    public static final String FB_GENERIC_FRAGMENT = "FB_Generic_Fragment";
    public static final String FB_GENERIC_BIT_ORDER = "-1";

    @Override
    public void run() {
        upsertGitDir();
        upsertBMDir();
    }

    private void upsertBMDir() {
        //set up environment
        setUpHiddenFolderAndBMBranch();
        //set up initial db state
        setUpDB();
    }

    private static void setUpDB() {
        // services
        ProductService productService = new ProductServiceImpl();
        FeatureService featureService = new FeatureServiceImpl();
        FragmentService fragmentService = new FragmentServiceImpl();
        BitOrderService BitOrderService = new BitOrderServiceImpl();

        // bitOrder
        BitOrder bitOrder = retrieveOrCreateGenericBitOrderBean(BitOrderService);
        bitOrder = BitOrderService.createOrUpdate(bitOrder);

        // fragment
        Fragment fragment = retrieveOrCreateGenericFragmentBean(fragmentService);
        // fragment relation
        List<FragmentToBitOrder> bitOrderList = new LinkedList<>();
        FragmentToBitOrder relation1 = new FragmentToBitOrder();
        relation1.setStartFragment(fragment);
        relation1.setEndBitOrder(bitOrder);
        bitOrderList.add(relation1);
        // fragment update
        fragment.setAssociatedTo(bitOrderList);
        fragmentService.createOrUpdate(fragment);

        // feature
        Feature feature = retrieveOrCreateGenericFeatureBean(featureService);
        // relations
        FeatureToBitOrder relation2 = new FeatureToBitOrder();
        relation2.setStartFeature(feature);
        relation2.setEndBitOrder(bitOrder);
        // update
        feature.setAssociatedTo(relation2);
        feature = featureService.createOrUpdate(feature);

        // product
        Product product = retrieveOrCreateGenericProductBean(productService);
        // relations
        List<ProductToFeature> featureList = new LinkedList<>();
        ProductToFeature relation3 = new ProductToFeature();
        relation3.setStartProduct(product);
        relation3.setEndFeature(feature);
        featureList.add(relation3);
        // update
        product.setAssociatedTo(featureList);
        productService.createOrUpdate(product);

        // bm config
        BMConfigService bmConfigService = new BMConfigServiceImpl();
        FBToolConfiguration bmConfig = bmConfigService.getBMConfigByDefaultID();
        if (bmConfig == null) {
            bmConfig = new FBToolConfiguration();
            bmConfig.setConfigId(BMConfigServiceImpl.BM_CONFIG_ID);
            bmConfig.setLastBlockId(0);
            bmConfigService.createOrUpdate(bmConfig);
        }
    }

    private void setUpHiddenFolderAndBMBranch() {
        IVCSAPI gitForBlocks = GitVCSManager.createInstance();
        if (!gitForBlocks.exitBMBranch()) {
            gitForBlocks.createBMBranch();
            SplMgrLogger.message_ln("- " + BMBranchLabel + " branch was created", false);
        }
        doCheckoutToBMBranch(gitForBlocks);
        if (!BMDirectoryUtil.existsBmDirectory()) {
            BMDirectoryUtil.createBMDirectory();
            SplMgrLogger.message_ln("- BM directory was created", false);
        }
        if (!BMDirectoryUtil.existsBMContactFile()) {
            BMDirectoryUtil.createBMContactFile();
            commitBMDirectory();
        }
    }

    private void doCheckoutToBMBranch(IVCSAPI gitBlock) {
        Git git = (Git) gitBlock.retrieveDirector();
        try {
            git.checkout().setName(BMBranchLabel).call();
        } catch (GitAPIException e) {
            throw new RuntimeException(e);
        }
    }

    private void upsertGitDir() {
        if (!GitDirectoryUtil.existsGitDir()) {
            GitDirectoryUtil.createGitDir();
            SplMgrLogger.message_ln("- Git directory was created", false);
            commitGitIgnoreAsFirstCommit();
        }
    }

    private void commitBMDirectory() {
        IVCSAPI gitBlockManager = GitVCSManager.createInstance();
        Git git = (Git) gitBlockManager.retrieveDirector();
        try {
            git.checkout().setName(BMBranchLabel).call();
            git.add().addFilepattern(".").call();
            git.commit().setMessage("BM: Adding BM directory").call();
        } catch (GitAPIException e) {
            throw new RuntimeException(e);
        }
    }

    private void commitGitIgnoreAsFirstCommit() {
        IVCSAPI gitBlockManager = GitVCSManager.createInstance();
        Git git = (Git) gitBlockManager.retrieveDirector();
        File myFile =
                new File(git.getRepository().getDirectory().getParent(), ".gitignore");
        try {
            FileUtils.writeStringToFile(myFile, createGitIgnoreContent(), "ISO-8859-1");
            git.add().addFilepattern(".gitignore").call();
            git.add().addFilepattern(".").call();
            git.commit().setMessage("BM: Adding Head with a commit").call();
        } catch (GitAPIException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String createGitIgnoreContent() {
        String msg = "# prod\n" +
                ".bm/logs/bm.log\n";
        return msg;
    }
}
