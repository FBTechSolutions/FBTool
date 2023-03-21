package ic.unicamp.bm.cli.cmd;

import static ic.unicamp.bm.block.GitVCS.BMBranchLabel;

import ic.unicamp.bm.block.utils.BMDirectoryUtil;
import ic.unicamp.bm.block.GitVCSManager;
import ic.unicamp.bm.block.utils.GitDirectoryUtil;
import ic.unicamp.bm.block.IVCSAPI;
import ic.unicamp.bm.cli.util.logger.SplMgrLogger;
import ic.unicamp.bm.graph.neo4j.schema.BMConfig;
import ic.unicamp.bm.graph.neo4j.schema.Feature;
import ic.unicamp.bm.graph.neo4j.schema.Fragment;
import ic.unicamp.bm.graph.neo4j.schema.Product;
import ic.unicamp.bm.graph.neo4j.schema.relations.FeatureToFragment;
import ic.unicamp.bm.graph.neo4j.schema.relations.ProductToFeature;
import ic.unicamp.bm.graph.neo4j.services.BMConfigService;
import ic.unicamp.bm.graph.neo4j.services.BMConfigServiceImpl;
import ic.unicamp.bm.graph.neo4j.services.FeatureService;
import ic.unicamp.bm.graph.neo4j.services.FeatureServiceImpl;
import ic.unicamp.bm.graph.neo4j.services.FragmentService;
import ic.unicamp.bm.graph.neo4j.services.FragmentServiceImpl;
import ic.unicamp.bm.graph.neo4j.services.ProductService;
import ic.unicamp.bm.graph.neo4j.services.ProductServiceImpl;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import picocli.CommandLine.Command;

@Command(
        name = BMConfigure.CMD_NAME,
        description = "This command will create the hidden required folders")
public class BMConfigure implements Runnable {

    public static final String CMD_NAME = "configure";
    public static final String FB_GENERIC_SPL = "FB Generic SPL";
    public static final String FB_GENERIC_FEATURE = "FB Generic Feature";
    public static final String FB_GENERIC_FRAGMENT = "FB Generic Fragment";

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
        ProductService productService = new ProductServiceImpl();
        FeatureService featureService = new FeatureServiceImpl();
        FragmentService fragmentService = new FragmentServiceImpl();

        Product product = productService.getProductByID(FB_GENERIC_SPL);
        if (product == null) {
            Feature feature = featureService.getFeatureByID(FB_GENERIC_FEATURE);
            if (feature == null) {
                Fragment fragment = fragmentService.getFragmentByID(FB_GENERIC_FRAGMENT);
                if(fragment == null){
                    fragment = new Fragment();
                    fragment.setFragmentId(FB_GENERIC_FRAGMENT);
                    fragment.setFragmentLabel(FB_GENERIC_FRAGMENT);
                }
                //data
                feature = new Feature();
                feature.setFeatureId(FB_GENERIC_FEATURE);
                feature.setFeatureLabel(FB_GENERIC_FEATURE);
                //relations
                List<FeatureToFragment> fragmentList = new LinkedList<>();
                FeatureToFragment relation = new FeatureToFragment();
                relation.setStartFeature(feature);
                relation.setEndFragment(fragment);
                fragmentList.add(relation);
                // update
                feature.setAssociatedTo(fragmentList);
                featureService.createOrUpdate(feature);
            }
            //data
            product = new Product();
            product.setProductId(FB_GENERIC_SPL);
            product.setProductLabel(FB_GENERIC_SPL);
            //relations
            List<ProductToFeature> featureList = new LinkedList<>();
            ProductToFeature relation = new ProductToFeature();
            relation.setStartProduct(product);
            relation.setEndFeature(feature);
            featureList.add(relation);
            //update
            product.setAssociatedTo(featureList);
            productService.createOrUpdate(product);
        }
        // bm config
        BMConfigService bmConfigService = new BMConfigServiceImpl();
        BMConfig bmConfig = bmConfigService.getBMConfigByDefaultID();
        if (bmConfig == null) {
            bmConfig = new BMConfig();
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
