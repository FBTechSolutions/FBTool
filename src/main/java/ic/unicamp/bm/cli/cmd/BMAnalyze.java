package ic.unicamp.bm.cli.cmd;

import ic.unicamp.bm.block.BMDirUtil;
import ic.unicamp.bm.block.BMTemporalDirUtil;
import ic.unicamp.bm.block.GitBlock;
import ic.unicamp.bm.block.GitBlockManager;
import ic.unicamp.bm.block.GitDirUtil;
import ic.unicamp.bm.block.IBlockAPI;
import ic.unicamp.bm.cli.util.logger.SplMgrLogger;
import ic.unicamp.bm.graph.RecordState;
import ic.unicamp.bm.graph.schema.ContainerBlock;
import ic.unicamp.bm.graph.GraphAPI;
import ic.unicamp.bm.graph.GraphBuilder;
import ic.unicamp.bm.graph.schema.ContentBlock;
import ic.unicamp.bm.graph.schema.Data;
import ic.unicamp.bm.graph.schema.enums.ContainerType;
import ic.unicamp.bm.graph.schema.enums.DataState;
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
      GraphAPI graph = GraphBuilder.createGraphInstance();
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
      ContainerBlock main = new ContainerBlock();
      main.setContainerId(GitDirUtil.getGitDirAsPath().toString());
      main.setContainerType(ContainerType.MAIN);
      changeTreeToSchemaForm(treeWalk, main);
      createContainers(main, graph);
      createRelations(main, graph);

      //blocks
      createBlocksByFile(main, graph);

    } catch (IOException | GitAPIException e) {
      throw new RuntimeException(e);
    }
  }

  private void createBlocksByFile(ContainerBlock container, GraphAPI graph) {
    if(container.getContainerType() == ContainerType.FILE){
      IBlockScanner blockScanner = new BlockScanner();
      Path path = Paths.get(container.getContainerId());
      Map<String, String> blocks = blockScanner.createInitialBlocks(path); //id and data
      IBlockAPI temporalGitBlock = GitBlockManager.createTemporalGitBlockInstance();

      //previous
      ContentBlock previous = null;
      ContentBlock main = null;
      for(String key:blocks.keySet()){
        String content = blocks.get(key);
        temporalGitBlock.upsertContentBlock(key,content);

        ContentBlock block = new ContentBlock();
        Data data = new Data();
        data.setDataId(key);
        //data.setSha(content);
        data.setBelongsTo(block);
        data.setCurrentState(DataState.NORMAL);

        block.setGoData(data);
        block.setBelongsTo(container);
        block.setContentId(key);
        block.setCurrentState(BlockState.TO_INSERT);
        if(previous!=null){
          block.setGoPrevious(previous);
          previous.setGoNext(block);
        }else{
          main = block;
        }
        previous = block;
        graph.upsertContent(block, RecordState.NORMAL);
      }
      container.setGoContent(main);
      graph.upsertContent(main, RecordState.RELATIONS);
      
      graph.upsertContainer(container, RecordState.RELATIONS);

      if(main!=null){
        ContentBlock next = main.getGoNext();
        while(next!=null){
          graph.upsertContent(next, RecordState.RELATIONS);
          next = next.getGoNext();
        }
      }
    }else{
      for (ContainerBlock containerBlock : container.getGoChildren()) {
        createBlocksByFile(containerBlock, graph);
      }
    }

  }

  private void createContainers(ContainerBlock container, GraphAPI graph) {
    graph.upsertContainer(container, RecordState.NORMAL);
    for (ContainerBlock containerBlock : container.getGoChildren()) {
      createContainers(containerBlock, graph);
    }
  }

  /*  private void createRelationDown(ContainerBlock container, GraphAPI graph) {
      System.out.println("DOWN");
      graph.upsertContainer(container, RecordOrientation.DOWN);
  *//*    for (ContainerBlock containerBlock : container.getGoChildren()) {
      createRelationDown(containerBlock, graph);
    }*//*
  }
  private void createRelationUp(ContainerBlock container, GraphAPI graph) {
    System.out.println("UP");
    graph.upsertContainer(container, RecordOrientation.UP);
    for (ContainerBlock containerBlock : container.getGoChildren()) {
      createRelationUp( containerBlock, graph);
    }
  }*/
  private void createRelations(ContainerBlock container, GraphAPI graph) {

    graph.upsertContainer(container, RecordState.RELATIONS);
    for (ContainerBlock containerBlock : container.getGoChildren()) {
      createRelations(containerBlock, graph);
    }
  }

  private void changeTreeToSchemaForm(TreeWalk treeWalk, ContainerBlock main) throws IOException {

    boolean isMainLoaded = false;
    ContainerBlock parentPivot = main;
    while (treeWalk.next()) {
      if (treeWalk.isSubtree()) {
        //folder
        ContainerBlock currentContainerBlock = new ContainerBlock();
        currentContainerBlock.setContainerId(treeWalk.getPathString());
        currentContainerBlock.setContainerType(ContainerType.FOLDER);
        //main
        if (isMainLoaded) {
          //backing
          boolean back = true;
          while (back) {
            File exists =
                new File(parentPivot.getContainerId(), treeWalk.getNameString());
            if (exists.exists()) {
              back = false;
            } else {
              if (parentPivot.getContainerType() != ContainerType.MAIN) {
                parentPivot = parentPivot.getGoParent();
              } else {
                back = false;
              }
            }
          }

          currentContainerBlock.setGoParent(parentPivot);
          List<ContainerBlock> list = parentPivot.getGoChildren();
          list.add(currentContainerBlock);
          parentPivot.setGoChildren(list);

          parentPivot = currentContainerBlock;
          System.out.println("dir: " + treeWalk.getPathString());
          treeWalk.enterSubtree();
        } else {
          //loading main
          List<ContainerBlock> list = main.getGoChildren();
          list.add(currentContainerBlock);
          main.setGoChildren(list);

          currentContainerBlock.setGoParent(main);

          parentPivot = currentContainerBlock;
          isMainLoaded = true;
          System.out.println("dir: " + treeWalk.getPathString());
          treeWalk.enterSubtree();
        }
      } else {
        //file
        ContainerBlock currentContainerBlock = new ContainerBlock();
        currentContainerBlock.setContainerId(treeWalk.getPathString());
        currentContainerBlock.setContainerType(ContainerType.FILE);

        if (isMainLoaded) {
          //backing
          boolean back = true;
          while (back) {
            File exists =
                new File(parentPivot.getContainerId(), treeWalk.getNameString());
            if (exists.exists()) {
              back = false;
            } else {
              if (parentPivot.getContainerType() != ContainerType.MAIN) {
                parentPivot = parentPivot.getGoParent();
              } else {
                back = false;
              }
            }
          }
          List<ContainerBlock> list = parentPivot.getGoChildren();
          list.add(currentContainerBlock);
          parentPivot.setGoChildren(list);

          currentContainerBlock.setGoParent(parentPivot);
          System.out.println("file: " + treeWalk.getPathString());
        } else {
          //loading main
          List<ContainerBlock> list = main.getGoChildren();
          list.add(currentContainerBlock);
          main.setGoChildren(list);

          currentContainerBlock.setGoParent(main);

          isMainLoaded = true;
          System.out.println("file: " + treeWalk.getPathString());
        }
      }
    }
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
