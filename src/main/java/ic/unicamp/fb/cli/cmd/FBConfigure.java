package ic.unicamp.fb.cli.cmd;

import ic.unicamp.fb.block.GitVCSManager;
import ic.unicamp.fb.block.IVCSAPI;
import ic.unicamp.fb.block.utils.FBDirectoryUtil;
import ic.unicamp.fb.block.utils.GitDirectoryUtil;
import ic.unicamp.fb.cli.util.logger.SplMgrLogger;
import ic.unicamp.fb.graph.neo4j.schema.Index;
import ic.unicamp.fb.graph.neo4j.schema.FBToolConfiguration;
import ic.unicamp.fb.graph.neo4j.schema.Feature;
import ic.unicamp.fb.graph.neo4j.schema.Fragment;
import ic.unicamp.fb.graph.neo4j.schema.Product;
import ic.unicamp.fb.graph.neo4j.schema.relations.FeatureToIndex;
import ic.unicamp.fb.graph.neo4j.schema.relations.FragmentToIndex;
import ic.unicamp.fb.graph.neo4j.schema.relations.ProductToFeature;
import ic.unicamp.fb.graph.neo4j.services.FBConfigService;
import ic.unicamp.fb.graph.neo4j.services.FBConfigServiceImpl;
import ic.unicamp.fb.graph.neo4j.services.IndexService;
import ic.unicamp.fb.graph.neo4j.services.IndexServiceImpl;
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

import static ic.unicamp.fb.block.GitVCS.FBBranchLabel;
import static ic.unicamp.fb.graph.neo4j.utils.IndexUtil.retrieveOrCreateGenericIndexBean;
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
        upsertFBDir();
    }

    private void upsertFBDir() {
        //set up environment
        setUpHiddenFolderAndFBBranch();
        //set up initial db state
        setUpDB();
    }

    private static void setUpDB() {
        // services
        ProductService productService = new ProductServiceImpl();
        FeatureService featureService = new FeatureServiceImpl();
        FragmentService fragmentService = new FragmentServiceImpl();
        IndexService IndexService = new IndexServiceImpl();

        // index
        Index index = retrieveOrCreateGenericIndexBean(IndexService);
        index = IndexService.createOrUpdate(index);

        // fragment
        Fragment fragment = retrieveOrCreateGenericFragmentBean(fragmentService);
        // fragment relation
        List<FragmentToIndex> indexList = new LinkedList<>();
        FragmentToIndex relation1 = new FragmentToIndex();
        relation1.setStartFragment(fragment);
        relation1.setEndIndex(index);
        indexList.add(relation1);
        // fragment update
        fragment.setAssociatedTo(indexList);
        fragmentService.createOrUpdate(fragment);

        // feature
        Feature feature = retrieveOrCreateGenericFeatureBean(featureService);
        // relations
        FeatureToIndex relation2 = new FeatureToIndex();
        relation2.setStartFeature(feature);
        relation2.setEndIndex(index);
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
        FBConfigService fbConfigService = new FBConfigServiceImpl();
        FBToolConfiguration fbConfig = fbConfigService.getFBConfigByDefaultID();
        if (fbConfig == null) {
            fbConfig = new FBToolConfiguration();
            fbConfig.setConfigId(FBConfigServiceImpl.FB_CONFIG_ID);
            fbConfig.setLastBlockId(0);
            fbConfigService.createOrUpdate(fbConfig);
        }
    }

    private void setUpHiddenFolderAndFBBranch() {
        IVCSAPI gitForBlocks = GitVCSManager.createInstance();
        if (!gitForBlocks.exitFBBranch()) {
            gitForBlocks.createFBBranch();
            SplMgrLogger.message_ln("- " + FBBranchLabel + " branch was created", false);
        }
        doCheckoutToFBBranch(gitForBlocks);
        if (!FBDirectoryUtil.existsFBDirectory()) {
            FBDirectoryUtil.createFBDirectory();
            SplMgrLogger.message_ln("- FB directory was created", false);
        }
        if (!FBDirectoryUtil.existsFBContactFile()) {
            FBDirectoryUtil.createFBContactFile();
            commitFBDirectory();
        }
    }

    private void doCheckoutToFBBranch(IVCSAPI gitBlock) {
        Git git = (Git) gitBlock.retrieveDirector();
        try {
            git.checkout().setName(FBBranchLabel).call();
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

    private void commitFBDirectory() {
        IVCSAPI gitBlockManager = GitVCSManager.createInstance();
        Git git = (Git) gitBlockManager.retrieveDirector();
        try {
            git.checkout().setName(FBBranchLabel).call();
            git.add().addFilepattern(".").call();
            git.commit().setMessage("FB Tool: Added FB directory.").call();
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
            git.commit().setMessage("FB Tool: Added Head pointer.").call();
        } catch (GitAPIException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String createGitIgnoreContent() {
        String msg = "# prod\n" +
                ".fb/logs/fb.log\n";
        return msg;
    }
}
