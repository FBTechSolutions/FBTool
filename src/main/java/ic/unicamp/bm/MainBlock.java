package ic.unicamp.bm;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileTime;
import java.nio.file.attribute.UserPrincipal;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class MainBlock {
  /*  public static void main(String[] args) {
      //https://www.marcobehler.com/guides/java-files
      //read
      Path path = Path.of("c:" , "dev", "licenses", "windows").resolve("readme.txt"); // resolve == getChild()
      boolean exists = Files.exists(path);
      System.out.println("exists = " + exists);
      System.out.println(path);

      //get modif time
      FileTime lastModifiedTime = null;
      try {
          lastModifiedTime = Files.getLastModifiedTime(path);
      } catch (IOException e) {
          e.printStackTrace();
      }
      System.out.println("lastModifiedTime = " + lastModifiedTime);

      //compare
      Path path2 = Path.of("c:\\dev\\licenses\\windows\\readme.txt");
      long mismatchIndex = 0;
      try {
          mismatchIndex = Files.mismatch(path2, Paths.get("c:\\dev\\whatever.txt"));
      } catch (IOException e) {
          e.printStackTrace();
      }
      System.out.println("mismatch = " + mismatchIndex);

      //owner
      Path path3 = Path.of("c:\\dev\\licenses\\windows\\readme.txt");
      UserPrincipal owner = null;
      try {
          owner = Files.getOwner(path3);
      } catch (IOException e) {
          e.printStackTrace();
      }
      System.out.println("owner = " + owner);

      Path path5 = Path.of("c:\\dev\\licenses\\windows\\readme.txt");
      LineIterator it = null;
      try {
          it = FileUtils.lineIterator(path5.toFile(), "UTF-8");
          try {
              while (it.hasNext()) {
                  String line = it.nextLine();
                  // do something with line
              }
          } finally {
              it.close();
          }
      } catch (IOException e) {
          e.printStackTrace();
      }
      //
      BufferedWriter bufferedWriter = null;
      String reportPath = "";
      String fileName = "";
      String content = "";
      try {
          FileWriter writer = new FileWriter(reportPath + File.separator + fileName, true);
          bufferedWriter = new BufferedWriter(writer);
          bufferedWriter.write(content);
          bufferedWriter.close();
      } catch (Exception e) {
         //
      } finally {
          if (bufferedWriter != null) {
              try {
                  bufferedWriter.close();
              } catch (IOException e) {
                  e.printStackTrace();
              }
          }
      }


  }*/
  /*public static void main(String[] args) {
      Path path = Path.of("c:" , "dev", "licenses", "windows").resolve("readme.txt"); // resolve == getChild()
      if(Files.exists(path)){
          LineIterator it = null;
          try {
              it = FileUtils.lineIterator(path.toFile(), "UTF-8");
              try {
                  Map<String, String> blocks = new HashMap<>();

                  StringBuffer stringBuffer = new StringBuffer();
                  while (it.hasNext()) {
                      String line = it.nextLine();
                      if(containBBlockMark(line) && containEBlockMark(line)){
                          String block  = extractBlock(line);
                      }
                      // do something with line
                  }
              } finally {
                  it.close();
              }
          } catch (IOException e) {
              e.printStackTrace();
          }
      }
  }*/


}
