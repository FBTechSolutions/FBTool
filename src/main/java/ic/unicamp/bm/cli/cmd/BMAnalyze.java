package ic.unicamp.bm.cli.cmd;

import ic.unicamp.bm.block.BMDirUtil;
import ic.unicamp.bm.block.BMTemporalDirUtil;
import ic.unicamp.bm.block.GitBlock;
import ic.unicamp.bm.block.GitBlockManager;
import ic.unicamp.bm.block.GitDirUtil;
import ic.unicamp.bm.block.IBlockAPI;
import ic.unicamp.bm.cli.util.logger.SplMgrLogger;
import ic.unicamp.bm.graph.NodePart;
import ic.unicamp.bm.graph.neo4j.schema.Container;
import ic.unicamp.bm.graph.neo4j.schema.enums.ContainerType;

import ic.unicamp.bm.graph.GraphDBAPI;
import ic.unicamp.bm.graph.GraphDBBuilder;

import ic.unicamp.bm.graph.neo4j.schema.enums.DataState;
import ic.unicamp.bm.graph.neo4j.schema.relations.ContainerToContainer;
import ic.unicamp.bm.graph.neo4j.services.ProductService;
import ic.unicamp.bm.graph.neo4j.services.ProductServiceImpl;
import ic.unicamp.bm.scanner.BlockScanner;
import ic.unicamp.bm.scanner.BlockState;
import ic.unicamp.bm.scanner.IBlockScanner;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.swing.text.AbstractDocument.Content;
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
      IBlockAPI temporalGitBlock = GitBlockManager.createTemporalGitBlockInstance();
      Git git = (Git) temporalGitBlock.retrieveDirector();
      git.checkout().setName(GitBlock.BMBlockMasterLabel).call();
      if (!BMTemporalDirUtil.existsBmTemporalDirectory()) {
        BMTemporalDirUtil.createBMTemporalDirectory();
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
      main.setContainerId(GitDirUtil.getGitDirAsPath().toString());
      main.setContainerType(ContainerType.MAIN);
      changeTreeToSchemaForm(treeWalk, main);

      createContainers(main);


      //blocks
      createBlocksByFile(main);

      showTemporalData();


    } catch (IOException | GitAPIException e) {
      throw new RuntimeException(e);
    }
  }

  private void showTemporalData( GraphDBAPI graph) {
    List<DataRaw> data = graph.retrieveDataByState(DataState.TEMPORAL);
    System.out.println("Block List:");
    for(Data record:data){
      Content block = record.getBelongsTo();
      String blockId = block.getContentId();
      System.out.println("blockId - " + blockId + "  state - "+ DataState.TEMPORAL+ " FROM "+block.getBelongsTo().getContainerId());
    }
  }

  private void createBlocksByFile(Container container, GraphDBAPI graph) {
    if (container.getContainerType() == ContainerType.FILE) {
      IBlockScanner blockScanner = new BlockScanner();
      Path path = Paths.get(container.getContainerId());
      Map<String, String> scannedBlocks = blockScanner.createInitialBlocks(path); //id and data
      IBlockAPI temporalGitBlock = GitBlockManager.createTemporalGitBlockInstance();

      //previous
      Content contentPrevious = null;
      Content contentMain = null;
      for (String key : scannedBlocks.keySet()) {
        String content = scannedBlocks.get(key);
        //path
        temporalGitBlock.upsertContent(key, content);
        //db
        Content block = new Content();
        DataRaw data = new DataRaw();
        data.setDataId(key);
        //data.setSha(content);
        data.setBelongsTo(block);
        data.setCurrentState(DataState.TEMPORAL);
        graph.upsertData(data, NodePart.VERTEX);

        block.setGoData(data);
        block.setBelongsTo(container);
        block.setContentId(key);
        block.setCurrentState(BlockState.TO_INSERT);
        if (contentPrevious != null) {
          block.setGoPrevious(contentPrevious);
          contentPrevious.setGoNext(block);
        } else {
          contentMain = block;
        }
        contentPrevious = block;
        graph.upsertContent(block, NodePart.VERTEX);
      }
      container.setGoContent(contentMain);
      graph.upsertContainer(container, NodePart.EDGES);

      graph.upsertContent(contentMain, NodePart.EDGES);

      if (contentMain != null) {
        Content next = contentMain.getGoNext();
        while (next != null) {
          graph.upsertContent(next, NodePart.EDGES);
          next = next.getGoNext();
        }
      }
    } else {
      for (Container Container : container.getGoChildren()) {
        createBlocksByFile(Container, graph);
      }
    }

  }

  private void createContainers(Container container) {
    ContainerService containerService = new ContainerServiceImpl();
    containerService.createOrUpdate(container);
  }

  private void createContainerRelations(Container container, GraphDBAPI graph) {

    graph.upsertContainer(container, NodePart.EDGES);
    for (Container Container : container.getGoChildren()) {
      createContainerRelations(Container, graph);
    }
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
          parentPivot = stack.peek();
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
      result = walk.filter(Files::isRegularFile).filter(aPath -> !GitDirUtil.existNameInPath(aPath))
          .filter(aPath -> !BMDirUtil.existNameInPath(aPath))
          .collect(Collectors.toList());
    }
    return result;

  }
}
