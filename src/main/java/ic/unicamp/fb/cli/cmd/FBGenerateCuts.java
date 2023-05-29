package ic.unicamp.fb.cli.cmd;

import ic.unicamp.fb.block.GitVCS;
import ic.unicamp.fb.block.GitVCSManager;
import ic.unicamp.fb.block.IVCRepository;
import ic.unicamp.fb.block.IVCSAPI;
import ic.unicamp.fb.generator.AheadGenerator;
import ic.unicamp.fb.generator.IGenerator;
import ic.unicamp.fb.graph.neo4j.schema.Block;
import ic.unicamp.fb.graph.neo4j.schema.Container;
import ic.unicamp.fb.graph.neo4j.schema.Fragment;
import ic.unicamp.fb.graph.neo4j.schema.enums.BlockState;
import ic.unicamp.fb.graph.neo4j.schema.enums.ContainerType;
import ic.unicamp.fb.graph.neo4j.schema.enums.DataState;
import ic.unicamp.fb.graph.neo4j.schema.relations.BlockToBlock;
import ic.unicamp.fb.graph.neo4j.schema.relations.BlockToFragment;
import ic.unicamp.fb.graph.neo4j.schema.relations.ContainerToBlock;
import ic.unicamp.fb.graph.neo4j.schema.relations.ContainerToContainer;
import ic.unicamp.fb.graph.neo4j.services.BlockService;
import ic.unicamp.fb.graph.neo4j.services.BlockServiceImpl;
import ic.unicamp.fb.graph.neo4j.services.ContainerService;
import ic.unicamp.fb.graph.neo4j.services.ContainerServiceImpl;
import ic.unicamp.fb.graph.neo4j.services.FragmentService;
import ic.unicamp.fb.graph.neo4j.services.FragmentServiceImpl;
import org.apache.commons.codec.digest.DigestUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.treewalk.TreeWalk;
import picocli.CommandLine;
import picocli.CommandLine.Command;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static ic.unicamp.fb.graph.neo4j.utils.FragmentUtil.retrieveOrCreateGenericFragmentBean;

@Command(
        name = FBGenerateCuts.CMD_NAME,
        description = "This command will add the blocks from the temporal folder to the real folder")
public class FBGenerateCuts implements Runnable {

    @CommandLine.Parameters(index = "0", description = "repository Name", defaultValue = "")
    String repositoryName;

    public static final String CMD_NAME = "generate-cuts";

    @Override
    public void run() {
        IVCRepository gitRepository = GitVCSManager.createGitRepositoryInstance();
        Path path = gitRepository.getOutDirectory(repositoryName);
        File currentDirectoryGit=getGitDirectory(gitRepository, repositoryName);
        if (currentDirectoryGit.exists()) {
            FileRepositoryBuilder repositoryBuilder = new FileRepositoryBuilder();
            repositoryBuilder.setMustExist(true);
            repositoryBuilder.setGitDir(currentDirectoryGit);

            try {
                Repository repository = repositoryBuilder.build();
                Ref head = repository.findRef("HEAD");
                RevWalk walk = new RevWalk(repository);
                RevCommit commit = walk.parseCommit(head.getObjectId());
                TreeWalk treeWalk = new TreeWalk(repository);
                treeWalk.addTree(commit.getTree());
                treeWalk.setRecursive(false);
                ContainerService containerService = new ContainerServiceImpl();
                while (treeWalk.next()) {
                    if (treeWalk.isSubtree()) {
                        String pathFileString = treeWalk.getPathString();
                        Container container = containerService.getContainerByID(pathFileString);
                        treeWalk.enterSubtree();
                    } else {
                        //Path parentDirectory = gitRepository.getParentDirectory();
                        String pathFileString = treeWalk.getPathString();
                        File currentPathProjected =  new File(String.valueOf(path));
                        Container container = containerService.getContainerByID(pathFileString);
                        if (container != null) {
                            System.out.println(currentPathProjected.getAbsolutePath());
                            String fileRawPath = currentPathProjected.getAbsolutePath() +  File.separator +container.getContainerId();
                            System.out.println(fileRawPath);
                            IGenerator generator = new AheadGenerator();
                            generator.generateCuts(fileRawPath);
                            generator.cleanUnnecessaryCuts(fileRawPath);
                        }
                    }
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    File getGitDirectory(IVCRepository gitRepository, String repositoryName){
        Path path = gitRepository.getOutDirectory(repositoryName);
        Path repositoryGit = Paths.get(String.valueOf(path), ".git");
        return new File(String.valueOf(repositoryGit));
    }
}
