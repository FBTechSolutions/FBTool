package ic.unicamp.bm.scanner;

import java.util.LinkedHashMap;
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

  public static int BLOCK_CONTENT_MAX_SIZE = 10;//40000;
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

  public Map<String, String> retrieveAllBlocks(Path pathFile) throws Exception {
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
}
