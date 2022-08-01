package ic.unicamp.bm.cli.cmd;

import ic.unicamp.bm.block.utils.BMDirectoryUtil;
import ic.unicamp.bm.block.utils.DirectoryUtil;
import ic.unicamp.bm.block.utils.TempBMDirectoryUtil;
import ic.unicamp.bm.block.GitVCS;
import ic.unicamp.bm.block.GitVCSManager;
import ic.unicamp.bm.block.utils.GitDirectoryUtil;
import ic.unicamp.bm.block.IVCSAPI;
import ic.unicamp.bm.cli.util.logger.SplMgrLogger;
import ic.unicamp.bm.graph.neo4j.schema.Block;
import ic.unicamp.bm.graph.neo4j.schema.Container;
import ic.unicamp.bm.graph.neo4j.schema.RawData;
import ic.unicamp.bm.graph.neo4j.schema.enums.BlockState;
import ic.unicamp.bm.graph.neo4j.schema.enums.ContainerType;

import ic.unicamp.bm.graph.neo4j.schema.enums.DataState;
import ic.unicamp.bm.graph.neo4j.schema.relations.BlockToBlock;
import ic.unicamp.bm.graph.neo4j.schema.relations.BlockToRawData;
import ic.unicamp.bm.graph.neo4j.schema.relations.ContainerToBlock;
import ic.unicamp.bm.graph.neo4j.schema.relations.ContainerToContainer;
import ic.unicamp.bm.graph.neo4j.services.ContainerService;
import ic.unicamp.bm.graph.neo4j.services.ContainerServiceImpl;

import ic.unicamp.bm.scanner.BlockScanner;
//import ic.unicamp.bm.scanner.BlockState;
import ic.unicamp.bm.scanner.BlockSequenceNumber;
import ic.unicamp.bm.scanner.IBlockScanner;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.treewalk.TreeWalk;
import picocli.CommandLine.Command;

@Command(
    name = BMAnalyze.CMD_NAME,
    description = "This command will Analyze all files")
public class BMAnalyze implements Runnable {

  public static final String CMD_NAME = "analyze";

  @Override
  public void run() {
    try {
      IVCSAPI temporalGitBlock = GitVCSManager.createTemporalGitBlockInstance();
      Git git = (Git) temporalGitBlock.retrieveDirector();
      git.checkout().setName(GitVCS.BMBranchLabel).call();
      if (!TempBMDirectoryUtil.existsBmTemporalDirectory()) {
        TempBMDirectoryUtil.createBMTemporalDirectory();
        SplMgrLogger.message_ln("- Temporal Directory for blocks was created", false);
      }

      Ref head = git.getRepository().findRef("HEAD");
      RevWalk walk = new RevWalk(git.getRepository());
      RevCommit commit = walk.parseCommit(head.getObjectId());

      TreeWalk treeWalk = new TreeWalk(git.getRepository());
      treeWalk.addTree(commit.getTree());
      treeWalk.setRecursive(false);

      //spl main container
      Container main = new Container();
      main.setContainerId(DirectoryUtil.getDirectoryAsPath().toString());
      main.setContainerType(ContainerType.MAIN);
      changeTreeToSchemaForm(treeWalk, main);



      System.out.println("End Containers");
      //blocks
      createBlocksByFile(main);
      createContainers(main);
      showTemporalData();


    } catch (IOException | GitAPIException e) {
      throw new RuntimeException(e);
    }
  }

  private void showTemporalData( ) {
    List<RawData> dataList = null;
    System.out.println("Block List:");
/*    for(RawData record:dataList){
      Content block = record.getBelongsTo();
      String blockId = block.getContentId();
      System.out.println("blockId - " + blockId + "  state - "+ DataState.TEMPORAL+ " FROM "+block.getBelongsTo().getContainerId());
    }*/
  }

  private void createBlocksByFile(Container container) {
    if (container.getContainerType() == ContainerType.FILE) {

      IBlockScanner blockScanner = new BlockScanner();
      Path path = Paths.get(container.getContainerId());
      Map<String, String> scannedBlocks = blockScanner.createInitialBlocks(path); //id and data
      IVCSAPI temporalGitBlock = GitVCSManager.createTemporalGitBlockInstance();

      //previous
      Block previousBlock = null;
      Block firstBlock = null;
      for (String key : scannedBlocks.keySet()) {
        System.out.println("key");
        String contentData = scannedBlocks.get(key);
        //path
        temporalGitBlock.upsertContent(key, contentData);
        //db

        RawData data = new RawData();
        data.setDataId(key);
        data.setCurrentState(DataState.TEMPORAL);


        Block block = new Block();
        block.setBlockId(key);
        System.out.println(block.getBlockId());
        block.setCurrentState(BlockState.TO_INSERT);

        BlockToRawData relationRawData = new BlockToRawData();
        relationRawData.setStartBlock(block);
        relationRawData.setEndRawData(data);
        block.setGetRawData(relationRawData);
        if(previousBlock == null){
          firstBlock = block;
          previousBlock = block;
        }else{
          BlockToBlock relation = new BlockToBlock();
          relation.setStartBlock(previousBlock);
          relation.setEndBlock(block);
          previousBlock.setGoNextBlock(relation);
          previousBlock = block;
        }
      }
      ContainerToBlock relation = new ContainerToBlock();
      relation.setStartContainer(container);
      System.out.println(firstBlock.getBlockId());
      relation.setEndBlock(firstBlock);
      container.setGetFirstBlock(relation);

    } else {
      for (ContainerToContainer Container : container.getGetContainers()) {
        createBlocksByFile(Container.getEndContainer());
      }
    }

  }

  private void createContainers(Container container) {
    ContainerService containerService = new ContainerServiceImpl();
    containerService.createOrUpdate(container);
  }

  private void changeTreeToSchemaForm(TreeWalk treeWalk, Container main) throws IOException {
    Stack<Container> stack = new Stack<Container>();
    stack.push(main);

    //boolean isMainLoaded = false;
    Container parentPivot = main;
    while (treeWalk.next()) {
      if (treeWalk.isSubtree()) {
        //backing
        parentPivot = backStack(treeWalk, stack, parentPivot);
        //folder
        Container container = new Container();
        container.setContainerId(treeWalk.getPathString());
        container.setContainerType(ContainerType.FOLDER);

        ContainerToContainer relation = new ContainerToContainer();
        relation.setStartContainer(parentPivot);
        relation.setEndContainer(container);
        List<ContainerToContainer> relations = parentPivot.getGetContainers();
        if(relations == null){
          relations = new LinkedList<>();
        }
        relations.add(relation);
        parentPivot.setGetContainers(relations);
        parentPivot = container;
        stack.push(container);
        treeWalk.enterSubtree();

      } else {
        //backing
        parentPivot = backStack(treeWalk, stack, parentPivot);
        //file
        Container container = new Container();
        container.setContainerId(treeWalk.getPathString());
        container.setContainerType(ContainerType.FILE);

        ContainerToContainer relation = new ContainerToContainer();
        relation.setStartContainer(parentPivot);
        relation.setEndContainer(container);
        List<ContainerToContainer> relations = parentPivot.getGetContainers();
        if(relations == null){
          relations = new LinkedList<>();
        }
        relations.add(relation);
        parentPivot.setGetContainers(relations);
      }
    }
  }

  private static Container backStack(TreeWalk treeWalk, Stack<Container> stack,
      Container parentPivot) {
    boolean back = true;
    while (back) {
      File exists =
          new File(parentPivot.getContainerId(), treeWalk.getNameString());
      if (exists.exists()) {
        back = false;
      } else {
        if (parentPivot.getContainerType() != ContainerType.MAIN) {
          parentPivot = stack.pop();
        } else {
          back = false;
        }
      }
    }
    return parentPivot;
  }
  public static List<Path> listFiles(Path path) throws IOException {
    List<Path> result;
    try (Stream<Path> walk = Files.walk(path)) {
      result = walk.filter(Files::isRegularFile).filter(aPath -> !GitDirectoryUtil.existNameInPath(aPath))
          .filter(aPath -> !BMDirectoryUtil.existNameInPath(aPath))
          .collect(Collectors.toList());
    }
    return result;

  }
}
