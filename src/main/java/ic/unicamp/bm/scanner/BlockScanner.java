package ic.unicamp.bm.scanner;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Stack;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BlockScanner implements IBlockScanner {

  public static int BLOCK_CONTENT_MAX_SIZE = 40000;//10;
  public static int BLOCK_ID_SIZE = 16;

  @Override
  public Map<String, String> retrieveUpdatedBlocks(Path pathFile) throws Exception {
    Map<String, Block> blocks = new HashMap<>();
    Block block = new Block();
    Map<String, String> currentBlocks = retrieveAllValidBlocks(pathFile);
    Map<String, String> originalBlocks = retrieveOriginalBlocks(pathFile);
    for (String currentKey : currentBlocks.keySet()) {
      if (originalBlocks.get(currentKey) != null) {
        if (compareContent(currentBlocks.get(currentKey), originalBlocks.get(currentKey))) {
          block.setCode(currentKey);
          block.setContent(currentBlocks.get(currentKey));
          block.setState(BlockState.NORMAL);
        }
      } else {

      }

    }
    return null;
  }

  private boolean compareContent(String updated, String original) {
    String sha256hex1 = DigestUtils.sha256Hex(updated);
    String sha256hex2 = DigestUtils.sha256Hex(original);
    return sha256hex1.equals(sha256hex2);
  }

  private Map<String, String> retrieveOriginalBlocks(Path pathFile) {
    return new HashMap<>();
  }

  @Override
  public Map<String, String> createInitialBlocks(Path pathFile) {
    Map<String, String> blocks = new LinkedHashMap<>();
    if (Files.exists(pathFile)) {
      LineIterator it = null;
      try {
        it = FileUtils.lineIterator(pathFile.toFile(), "UTF-8");
        try {
          StringBuffer blockBuffer = new StringBuffer();
          int currentBlockFilled = 0;
          while (it.hasNext()) {
            String line = it.nextLine();
            int blockFreeSpace = BLOCK_CONTENT_MAX_SIZE - currentBlockFilled;
            while (line.length() >= blockFreeSpace) {
              blockBuffer.append(line.substring(0, blockFreeSpace));
              addNewBlockToMap(blocks, blockBuffer);
              blockBuffer = new StringBuffer();
              line = line.substring(blockFreeSpace);
              blockFreeSpace = BLOCK_CONTENT_MAX_SIZE;
              currentBlockFilled = 0;
            }
            blockBuffer.append(line + System.lineSeparator()); //TO DELETE O CHECK
            currentBlockFilled = currentBlockFilled + line.length();
          }
          addNewBlockToMap(blocks, blockBuffer);
        } finally {
          it.close();
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    return blocks;
  }

  //constrainst that i tiwll work
  // it will be good part to put in the paper
  public Map<String, String> retrieveAllValidBlocks(Path pathFile) throws Exception {
    Map<String, String> blocks = new LinkedHashMap<>();
    if (Files.exists(pathFile)) {
      LineIterator it = null;
      try {
        it = FileUtils.lineIterator(pathFile.toFile(), "UTF-8");
        try {
          StringBuilder block = new StringBuilder();

          String expectedEndMarkId = null;
          boolean expectedEndMark = false;

          while (it.hasNext()) {
            String line = it.nextLine();
            boolean isLine = true;
            boolean thereIsLine = true;
            while (thereIsLine) {

              if (!expectedEndMark) {
                int startPos = getFirstBeginMark(line);
                if (startPos >= 0) {
                  String beginBlockId = getFirstBeginMarkId(line);
                  int endPos = getFirstEndMark(line);
                  if (endPos >= 0) {
                    String endBlockId = getFirstEndMarkId(line);
                    if (beginBlockId.equals(endBlockId)) {
                      if (isLine) {
                        block.append("\r\n");
                      }
                      block.append(line.substring(startPos, endPos + 4));
                      blocks.put(endBlockId, block.toString());
                      block = new StringBuilder();
                      line = line.substring(endPos + 4);
                      isLine = false;
                    } else {
                      throw new Exception();
                    }
                  } else {
                    expectedEndMarkId = beginBlockId;
                    expectedEndMark = true;
                    block.append(line);
                    //line = "";
                    thereIsLine = false;
                  }
                }
              } else {
                int endPos = getFirstEndMark(line);
                if (endPos >= 0) {
                  String endBlockId = getFirstEndMarkId(line);
                  if (endBlockId.equals(expectedEndMarkId)) {
                    if (isLine) {
                      block.append("\r\n");
                    }
                    block.append(line.substring(0, endPos + 4));
                    blocks.put(endBlockId, block.toString());
                    block = new StringBuilder();
                    line = line.substring(endPos + 4);
                    isLine = false;
                    expectedEndMarkId = null;
                    expectedEndMark = false;
                    if (line.length() == 0) {
                      thereIsLine = false;
                    }
                  } else {
                    throw new Exception();
                  }
                } else {
                  if (isLine) {
                    block.append("\r\n");
                  }
                  block.append(line);
                  //line = "";
                  thereIsLine = false;
                }
              }
            }
          }
        } finally {
          it.close();
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    return blocks;
  }

  /*  public Map<String, String> retrieveAllBlocks(Path pathFile) throws Exception {
      Map<String, String> blocks = new LinkedHashMap<>();
      if (Files.exists(pathFile)) {
        LineIterator it = null;
        try {
          it = FileUtils.lineIterator(pathFile.toFile(), "UTF-8");
          try {
            StringBuilder block = new StringBuilder();

            String expectedEndMarkId = null;
            boolean expectedEndMark = false;

            while (it.hasNext()) {
              String line = it.nextLine();
              boolean isLine = true;
              boolean thereIsLine = true;
              while (thereIsLine) {

                if (!expectedEndMark) {
                  int startPos = getFirstBeginMark(line);
                  if (startPos >= 0) {
                    if (startPos == 0) {
                      String beginBlockId = getFirstBeginMarkId(line);
                      int endPos = getFirstEndMark(line);
                      if (endPos >= 0) {
                        String endBlockId = getFirstEndMarkId(line);
                        if (beginBlockId.equals(endBlockId)) {
                          if (isLine) {
                            block.append("\r\n");
                          }
                          block.append(line.substring(startPos, endPos + 4));
                          blocks.put(endBlockId, block.toString());
                          block = new StringBuilder();
                          line = line.substring(endPos + 4);
                          isLine = false;
                        } else {
                          throw new Exception();
                        }
                      } else {
                        expectedEndMarkId = beginBlockId;
                        expectedEndMark = true;
                        block.append(line);
                        //line = "";
                        thereIsLine = false;
                      }
                    } else {
                      block.append(line.substring(0, startPos));
                      String key = "a" + SequenceAbstractBlockNumber.getNextStringCode();
                      blocks.put(key, block.toString());
                      line = line.substring(startPos);

                      block = new StringBuilder();
                      isLine = false;
                    }
                  } else {
                    block.append(line);
                    String key = "a" + SequenceAbstractBlockNumber.getNextStringCode();
                    blocks.put(key, block.toString());
                    thereIsLine = false;
                    block = new StringBuilder();
                  }
                } else {
                  int endPos = getFirstEndMark(line);
                  if (endPos >= 0) {
                    String endBlockId = getFirstEndMarkId(line);
                    if (endBlockId.equals(expectedEndMarkId)) {
                      if (isLine) {
                        block.append("\r\n");
                      }
                      block.append(line.substring(0, endPos + 4));
                      blocks.put(endBlockId, block.toString());
                      block = new StringBuilder();
                      line = line.substring(endPos + 4);
                      isLine = false;
                      expectedEndMarkId = null;
                      expectedEndMark = false;
                      if (line.length() == 0) {
                        thereIsLine = false;
                      }
                    } else {
                      throw new Exception();
                    }
                  } else {
                    if (isLine) {
                      block.append("\r\n");
                    }
                    block.append(line);
                    //line = "";
                    thereIsLine = false;
                  }
                }
              }
            }
          } finally {
            it.close();
          }
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
      return blocks;
    }*/
  public Map<String, String> retrieveAllBlocks(Path pathFile) throws Exception {
    Map<String, String> blocks = new LinkedHashMap<>();
    Stack<String> blockStack = new Stack<>();

    if (Files.exists(pathFile)) {
      LineIterator it = null;
      try {
        it = FileUtils.lineIterator(pathFile.toFile(), "UTF-8");
        try {
          StringBuilder block = new StringBuilder();

          String expectedEndMarkId = null;
          boolean expectedAnEndMark = false;

          while (it.hasNext()) {
            String line = it.nextLine(); //read line
            boolean isLine = true;
            boolean isCheckingLine = true;
            while (isCheckingLine) {
              int firstStartPos = getFirstBeginMark(line);
              int firstCutPos = getFirstCutMark(line);
              int firstEndPos = getFirstEndMark(line);

              if(firstStartPos>=0){
                if(firstCutPos>=0){
                  if(firstEndPos>=0){
                    // start - cut - end
                    int [] sorted = new int [] {firstStartPos, firstCutPos, firstEndPos};
                    Arrays.sort(sorted);
                    //switch
                    if(firstStartPos == sorted[0]){
                      //start
                      if(firstStartPos == 0){
                        //start initial
                        String beginBlockId = getFirstBeginMarkId(line);
                        //---wrap
                        if(firstCutPos == sorted[1]){
                          //cut
                          block.append(line, 0, firstCutPos);
                          line = line.substring(firstCutPos + size_cut);
                          blocks.put("New", block.toString());
                          block = new StringBuilder();
                          isCheckingLine = thereIsMoreChars(line);
                        }
                        if(firstEndPos == sorted[1]){
                          //end
                          String endBlockId = getFirstEndMarkId(line);
                          if(!blockStack.isEmpty() && blockStack.peek().equals(endBlockId)){
                            block.append(line, size_start_block, firstEndPos);
                            blocks.put(beginBlockId, block.toString());
                            block = new StringBuilder();
                            line = line.substring(firstEndPos + size_end_block);
                          }else{
                            blocks.put(endBlockId, block.toString());
                            line = line.substring(firstEndPos + size_end_block);
                            blockStack.pop();
                          }
                        }
                      }else{
                        // start not initial
                        block.append(line, 0, firstStartPos); //add part
                        blocks.put("New", block.toString());
                        block = new StringBuilder();
                        line = line.substring(firstStartPos);
                        isCheckingLine = thereIsMoreChars(line);
                      }
                    }
                    if(firstCutPos == sorted[0]){
                      //cut
                      if(firstCutPos == 0){
                        //==wrap
                        if(firstStartPos == sorted[1]){
                          //start
                          block.append(line, size_cut, firstStartPos);
                          blocks.put("New", block.toString());
                          block = new StringBuilder();
                          line = line.substring(firstStartPos);
                          isCheckingLine = thereIsMoreChars(line);
                        }
                        if(firstEndPos == sorted[1]){
                          //end
                          String endBlockId = getFirstEndMarkId(line);
                          if(!blockStack.isEmpty() && blockStack.peek().equals(endBlockId)){
                            blockStack.pop();
                            block.append(line, size_cut, firstEndPos);
                            blocks.put("New", block.toString());
                            block = new StringBuilder();
                            line = line.substring(firstEndPos);
                            isCheckingLine = thereIsMoreChars(line);
                          }else{
                            block.append(line, size_cut, firstEndPos);
                            blocks.put("New", block.toString());
                            block = new StringBuilder();
                            line = line.substring(firstEndPos);
                            isCheckingLine = thereIsMoreChars(line);
                          }
                        }
                      }else{
                        // cut not initial
                        block.append(line, 0, firstCutPos); //add part
                        blocks.put("New", block.toString());
                        block = new StringBuilder();
                        line = line.substring(firstCutPos);
                        isCheckingLine = thereIsMoreChars(line);
                      }
                    }
                    if(firstEndPos == sorted[0]){
                      //end
                      if(firstEndPos == 0){
                        //end initial
                        //==wrap
                        if(firstStartPos == sorted[1]){
                          //start
                          block.append(line, size_end_block, firstStartPos);
                          blocks.put("New", block.toString());
                          block = new StringBuilder();
                          line = line.substring(firstStartPos + size_start_block);
                          isCheckingLine = thereIsMoreChars(line);
                        }
                        if(firstCutPos == sorted[1]){
                          //cut
                          block.append(line, size_end_block, firstCutPos);
                          blocks.put("New", block.toString());
                          block = new StringBuilder();
                          line = line.substring(firstCutPos + size_cut);
                          isCheckingLine = thereIsMoreChars(line);
                        }
                      }else{
                        // end not initial
                        String endBlockId = getFirstEndMarkId(line);
                        if(!blockStack.isEmpty() && blockStack.peek().equals(endBlockId)){
                          blockStack.pop();
                          block.append(line, 0, firstEndPos);
                          blocks.put(endBlockId, block.toString());
                          block = new StringBuilder();
                          line = line.substring(firstEndPos + size_end_block);
                          isCheckingLine = thereIsMoreChars(line);
                        }else{
                          block.append(line, 0, firstEndPos); //add part
                          blocks.put("New", block.toString());
                          block = new StringBuilder();
                          line = line.substring(firstEndPos);
                          isCheckingLine = thereIsMoreChars(line);
                        }
                      }
                    }

                  }else{
                    // start - cut
                    int [] sorted = new int [] {firstStartPos, firstCutPos};
                    Arrays.sort(sorted);
                    //switch
                    if(firstStartPos == sorted[0]){
                      if(firstStartPos == 0){

                      }else{
                        // start not initial
                        block.append(line, 0, firstStartPos); //add part
                        blocks.put("New", block.toString());
                        block = new StringBuilder();
                        line = line.substring(firstStartPos);
                        isCheckingLine = thereIsMoreChars(line);
                      }
                    }
                    if(firstCutPos == sorted[0]){
                      if(firstCutPos == 0){

                      }else{
                        // cut not initial
                        block.append(line, 0, firstCutPos); //add part
                        blocks.put("New", block.toString());
                        block = new StringBuilder();
                        line = line.substring(firstCutPos + size_cut);
                        isCheckingLine = thereIsMoreChars(line);
                      }
                    }
                  }
                }else{
                  if(firstEndPos>0){
                    // start - end
                    int [] sorted = new int [] {firstStartPos, firstEndPos};
                    Arrays.sort(sorted);
                    //switch
                    if(firstStartPos == sorted[0]){
                      if(firstStartPos == 0){

                      }else{

                      }
                    }
                    if(firstEndPos == sorted[0]){
                      if(firstEndPos == 0){

                      }else{

                      }
                    }
                  }else{
                    // start
                  }
                }
              }else{
                if(firstCutPos>=0){
                  if(firstEndPos>=0){
                    // cut - end

                    int [] sorted = new int [] {firstCutPos, firstEndPos};
                    Arrays.sort(sorted);

                    //switch
                    if(firstCutPos == sorted[0]){
                      if(firstCutPos == 0){

                      }else{
                        if(!blockStack.isEmpty()){
                          block.append(line, 0, firstCutPos);
                          blocks.put(blockStack.pop(), block.toString());
                          block = new StringBuilder();
                          line = line.substring(firstCutPos + size_cut);
                          isCheckingLine = thereIsMoreChars(line);
                        }else{
                          block.append(line, 0, firstCutPos);
                          blocks.put("New", block.toString());
                          block = new StringBuilder();
                          line = line.substring(firstCutPos + size_cut);
                          isCheckingLine = thereIsMoreChars(line);
                        }
                      }
                    }
                    if(firstEndPos == sorted[0]){
                      if(firstEndPos == 0){


                      }else{
                        // end not initial
                        String endBlockId = getFirstEndMarkId(line);
                        if(!blockStack.isEmpty() && blockStack.peek().equals(endBlockId)){
                          blockStack.pop();
                          block.append(line, 0, firstEndPos);
                          blocks.put(endBlockId, block.toString());
                          block = new StringBuilder();
                          line = line.substring(firstEndPos + size_end_block);
                          isCheckingLine = thereIsMoreChars(line);
                        }else{
                          block.append(line, 0, firstEndPos);
                          blocks.put("New", block.toString());
                          block = new StringBuilder();
                          line = line.substring(firstEndPos + size_end_block);
                          isCheckingLine = thereIsMoreChars(line);
                        }
                      }
                    }
                  }else{
                    // cut

                    if(!blockStack.isEmpty()){
                      block.append(line, 0, firstCutPos);
                      blocks.put(blockStack.pop(), block.toString());
                      block = new StringBuilder();
                      line = line.substring(firstCutPos + size_cut);
                      isCheckingLine = thereIsMoreChars(line);
                    }else{
                      block.append(line, 0, firstCutPos);
                      blocks.put("New", block.toString());
                      block = new StringBuilder();
                      line = line.substring(firstCutPos + size_cut);
                      isCheckingLine = thereIsMoreChars(line);
                    }

                  }
                }else{
                  if(firstEndPos>=0){
                    // end
                    String endBlockId = getFirstEndMarkId(line);
                    if(!blockStack.isEmpty() && blockStack.peek().equals(endBlockId)){
                      blockStack.pop();
                      block.append(line, 0, firstEndPos);
                      blocks.put(endBlockId, block.toString());
                      block = new StringBuilder();
                      line = line.substring(firstEndPos + size_end_block);
                      isCheckingLine = thereIsMoreChars(line);
                    }else{
                      block.append(line, 0, firstEndPos);
                      blocks.put("New", block.toString());
                      block = new StringBuilder();
                      line = line.substring(firstEndPos + size_end_block));
                      isCheckingLine = thereIsMoreChars(line);
                    }
                  }else{
                    // empty
                    block.append(line); //add
                    block.append("\r\n"); //space
                    isCheckingLine = false;
                  }
                }
              }
            }
          }
        } finally {
          it.close();
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    return blocks;
  }

  private boolean thereIsMoreChars(String line) {
    return line.length() > 0;
  }

  private int getFirstCutMark(String line) {
    return 0;
  }

  private void addNewBlockToMap(Map<String, String> blocks, StringBuffer blockBuffer) {
/*    String blockData = blockBuffer.toString();
    String blockKey =  blockData + '-' + generateId();
    String sha256hex = DigestUtils.sha256Hex(blockKey);
    System.out.println(sha256hex);*/

    blocks.put(BlockSequenceNumber.getNextStringCode(), blockBuffer.toString());
  }

  private String generateId() {
    return UUID.randomUUID().toString();
  }

  private static String extractBlock(String line) {
    return null;
  }

  private static boolean containEBMark(String line) {
    return false;
  }

  private static boolean containBBMark(String line) {
    return false;
  }


  private String getFirstEndMarkId(String line) {
    int idx = getFirstEndMark(line);
    return line.substring(idx - BLOCK_ID_SIZE, idx);
  }

  private int getFirstEndMark(String line) {
    return line.indexOf("]<-b");
  }

  private String getFirstBeginMarkId(String line) {
    int idx = getFirstBeginMark(line) + 4;
    return line.substring(idx, idx + BLOCK_ID_SIZE);
  }

  private int getFirstBeginMark(String line) {
    return line.indexOf("b->[");
  }

  private String getPrefixesEBMark(String line) {
    return null;
  }

  private void addBlockToMapByString(Map<String, String> blocks, String blockData) {
    String sha256hex = DigestUtils.sha256Hex(blockData);
    blocks.put(sha256hex, blockData);
  }

  @Override
  public String cleanTagMarks(String content) {
    int idb = getFirstBeginMark(content) + 4 + BLOCK_ID_SIZE + 1;
    int ide = getFirstEndMark(content) - BLOCK_ID_SIZE - 1;
    return content.substring(idb, ide);
  }
}

/*              if (startPos >= 0) {
                if (cutPos >= 0) {
                  if (cutPos < startPos) {
                    //cut first
                    block.append(line.substring(0, cutPos));
                    line = line.substring(cutPos + size_cut); //4 is the length of the cut mark
                    String key = "New";
                    blocks.put(key, block.toString());
                    block = new StringBuilder();
                    isCheckingLine = thereIsMoreChars(line);
                  } else {
                    //start first
                    if(startPos!=0){
                      // there is something before
                      block.append(line, 0, startPos); //add part
                      String key = "New";
                      blocks.put(key, block.toString());
                      block = new StringBuilder();
                      line = line.substring(startPos);
                      isCheckingLine = thereIsMoreChars(line);
                    }else{
                      // there is nothing before start
                      String beginBlockId = getFirstBeginMarkId(line);
                      int endPos = getFirstEndMark(line);
                      if(endPos>0){
                        // there is end mark
                        if(cutPos < endPos){
                          // cut antes de end post
                          block.append(line, size_block, cutPos);
                          blocks.put(beginBlockId, block.toString());
                          block = new StringBuilder();
                          line = line.substring(cutPos + size_cut); //4 is the length of the cut mark
                        }else{

                        }
                      }else{
                        // there isn't end mark
                        block.append(line, size_block, cutPos);
                        blocks.put(beginBlockId, block.toString());
                        block = new StringBuilder();
                        line = line.substring(cutPos + size_cut); //4 is the length of the cut mark
                        //brokenBlocks.push(beginBlockId);
                      }

                    }
                  }
                } else {
                  if(endPos > 0){
                    if(startPos > endPos){
                      //end first
                    }else{
                      //start first
                      if(startPos!=0){
                        // there is something before
                        block.append(line, 0, startPos); //add part
                        String key = "New";
                        blocks.put(key, block.toString());
                        block = new StringBuilder();
                        line = line.substring(startPos);
                        isCheckingLine = thereIsMoreChars(line);
                      }else{
                        // there is nothing before start

                      }
                    }
                  }else{

                  }

                }
              } else {
                if (cutPos > 0) {
                  block.append(line, 0, cutPos); //add part
                  String key = "New";
                  blocks.put(key, block.toString());
                  block = new StringBuilder();
                  line = line.substring(cutPos + size_cut); //cut
                  isCheckingLine = thereIsMoreChars(line);
                } else {
                  if(endPos > 0){
                    String endBlockId = getFirstEndMarkId(line);
                    block.append(line, 0, endPos); //add part
                    if(!blockStack.isEmpty() && !blockStack.peek().equals(endBlockId)){
                      String key = "New";
                      blocks.put(key, block.toString());
                      line = line.substring(endPos + size_end_block);
                    }else{
                      blocks.put(endBlockId, block.toString());
                      line = line.substring(endPos + size_end_block);
                      blockStack.pop();
                    }
                  }else{
                    block.append(line); //add
                    block.append("\r\n"); //space
                    isCheckingLine = false;
                  }
                }
              }*/
