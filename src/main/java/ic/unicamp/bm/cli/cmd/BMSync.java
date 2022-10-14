package ic.unicamp.bm.cli.cmd;

import ic.unicamp.bm.block.GitVCSManager;
import ic.unicamp.bm.block.IVCRepository;
import ic.unicamp.bm.block.IVCSAPI;
import ic.unicamp.bm.graph.neo4j.schema.Block;
import ic.unicamp.bm.graph.neo4j.schema.Feature;
import ic.unicamp.bm.graph.neo4j.schema.enums.BlockState;
import ic.unicamp.bm.graph.neo4j.schema.enums.DataState;
import ic.unicamp.bm.graph.neo4j.schema.relations.BlockToBlock;
import ic.unicamp.bm.graph.neo4j.schema.relations.BlockToFeature;
import ic.unicamp.bm.graph.neo4j.services.BlockService;
import ic.unicamp.bm.graph.neo4j.services.BlockServiceImpl;
import ic.unicamp.bm.graph.neo4j.services.FeatureService;
import ic.unicamp.bm.graph.neo4j.services.FeatureServiceImpl;
import ic.unicamp.bm.scanner.BlockScanner;
import ic.unicamp.bm.scanner.BlockSequenceNumber;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.treewalk.TreeWalk;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

@Command(
    name = BMSync.CMD_NAME,
    description = "This command will inform whether exits modifications in the blocks using as reference the DB")
public class BMSync implements Runnable {

  @Parameters(index = "0", description = "repository Name", defaultValue = "")
  String repositoryName;

  public static final String CMD_NAME = "sync";
  IVCSAPI temporalVC = GitVCSManager.createTemporalGitBlockInstance();

  @Override
  public void run() {
    if (StringUtils.isEmpty(repositoryName)) {
      System.out.println("You need to specify at repository name");
      return;
    }
    BlockScanner blockScanner = new BlockScanner();
    IVCRepository gitRepository = GitVCSManager.createGitRepositoryInstance();
    Path path = gitRepository.getOutDirectory(repositoryName);

    Path repositoryGit = Paths.get(String.valueOf(path), ".git");
    File currentDirectoryGit = new File(String.valueOf(repositoryGit));
    if(currentDirectoryGit.exists()){
      FileRepositoryBuilder repositoryBuilder = new FileRepositoryBuilder();
      repositoryBuilder.setMustExist(true);
      repositoryBuilder.setGitDir(currentDirectoryGit);

      try {
        Repository repository = repositoryBuilder.build();

        //Git git = new Git(repository);
        Ref head = repository.findRef("HEAD");
        RevWalk walk = new RevWalk(repository);
        RevCommit commit = walk.parseCommit(head.getObjectId());
        TreeWalk treeWalk = new TreeWalk(repository);
        treeWalk.addTree(commit.getTree());
        treeWalk.setRecursive(false);
        BlockService blockService = new BlockServiceImpl();

        while (treeWalk.next()) {
          if (treeWalk.isSubtree()) {
            treeWalk.enterSubtree();
          } else {

            String pathFileString = treeWalk.getPathString();
            //Path pathFile = Paths.get(pathFileString);
            Path pathFileRepository = Paths.get(String.valueOf(path), pathFileString);

            Block  beginPivot = blockService.getFirstBlockByFile(pathFileString);
            String beginPivotId = beginPivot.getBlockId();
            Block endPivot;
            String endPivotId = null;
            if(beginPivot.getGoNextBlock()!=null){
              BlockToBlock blockToBlock = beginPivot.getGoNextBlock();
              endPivot = blockToBlock.getEndBlock();
              endPivotId = endPivot.getBlockId();
            }

            Map<String, String> updatedBlocks = blockScanner.retrieveAllBlocks(pathFileRepository); //by file
            List<String> updatedBlocksReverse = new ArrayList<>(updatedBlocks.keySet());

            LinkedHashMap<String, String> temporalNewBlocks = new LinkedHashMap<>(); //news
            for (String key : updatedBlocksReverse) {
              if(key.charAt(0) == 'N'){
                temporalNewBlocks.put(key, updatedBlocks.get(key));
              }else{
                if(!temporalNewBlocks.isEmpty()){
                  processNewBlocks(blockService, beginPivotId, endPivotId, temporalNewBlocks);
                }else{
                  Block updatedBlock = blockService.getBlockByID(key);
                  updatedBlock.setBlockState(BlockState.TO_UPDATE);
                  updatedBlock.setVcBlockState(DataState.TEMPORAL);
                  beginPivotId = updatedBlock.getBlockId();
                  BlockToBlock relationUpdated = updatedBlock.getGoNextBlock();
                  if(relationUpdated != null){
                    endPivotId = relationUpdated.getEndBlock().getBlockId();
                  }else{
                    endPivotId = null;
                  }
                  String content = updatedBlocks.get(key);
                  String cleanContent = blockScanner.cleanTagMarks(content);
                  //treat the size
                  temporalVC.upsertContent(key, cleanContent);
                  blockService.createOrUpdate(updatedBlock);
                }
              }
            }
            if(!temporalNewBlocks.isEmpty()){
              processNewBlocks(blockService, beginPivotId, endPivotId, temporalNewBlocks);
            }
          }

        }

      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }


    //from first commit to last commit differences
    //read files
    //read blocks tags
    //compare blocks with db
    //show review of blocks
  }

  private String cleanTags(String content) {
    return null;
  }

  private void processNewBlocks(BlockService blockService, String beginPivotId, String endPivotId,
      LinkedHashMap<String, String> temporalNewBlocks) {
    List<String> reverseNewBlocks = new ArrayList<>(temporalNewBlocks.keySet());
    Collections.reverse(reverseNewBlocks);
    for (String key : reverseNewBlocks) {
      //create a new block
      Block newBlock = new Block();
      String newBlockId = BlockSequenceNumber.getNextStringCode();
      newBlock.setBlockState(BlockState.TO_INSERT);
      newBlock.setVcBlockState(DataState.TEMPORAL);
      newBlock.setBlockId(newBlockId);

      //adding default feature
      FeatureService featureService = new FeatureServiceImpl();
      Feature feature = featureService.getFeatureByID(BMConfigure.BMFEATURE);
      BlockToFeature blockToFeature = new BlockToFeature();
      blockToFeature.setStartBlock(newBlock);
      blockToFeature.setEndFeature(feature);
      newBlock.setAssociatedTo(blockToFeature);

      //create relation
      if(endPivotId != null){
        BlockToBlock relation = new BlockToBlock();
        relation.setEndBlock(blockService.getBlockByID(endPivotId));
        relation.setStartBlock(newBlock);
        //update relation
        newBlock.setGoNextBlock(relation);
      }
      //update db
      blockService.createOrUpdate(newBlock);
      String content = temporalNewBlocks.get(key);
      // treat the size
      //update VC
      temporalVC.upsertContent(newBlockId, content);
      //update pivots
      endPivotId = newBlockId;
    }
    temporalNewBlocks.clear();
    Block pivotBlock = blockService.getBlockByID(beginPivotId);
    BlockToBlock relationPivot = new BlockToBlock();
    relationPivot.setStartBlock(pivotBlock);
    relationPivot.setEndBlock(blockService.getBlockByID(endPivotId));
    pivotBlock.setGoNextBlock(relationPivot);
    blockService.createOrUpdate(pivotBlock);
  }
}
