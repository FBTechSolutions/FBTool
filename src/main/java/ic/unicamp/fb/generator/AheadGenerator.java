package ic.unicamp.fb.generator;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class AheadGenerator implements IGenerator {

    private static final String CUT_TAG = "b>>[cut]";
    private static final String AHEAD_COMMENT = "//";
    private static final String AHEAD_TAG_IF = AHEAD_COMMENT + "#if";
    private static final String AHEAD_TAG_END = AHEAD_COMMENT + "#endif";
    private static final String AHEAD_TAG_ELSE = AHEAD_COMMENT + "#else";
    private static final String AHEAD_TAG_ELIF = AHEAD_COMMENT + "#elif";

    @Override
    public void generateCuts(String filePath) {
        File inputFile = new File(filePath);
        File outputFile = createTempFile(filePath);
        try{
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {
                try (LineIterator lineIterator = FileUtils.lineIterator(inputFile, "UTF-8")) {
                    while (lineIterator.hasNext()) {
                        String line = lineIterator.nextLine();
                        String modifiedLine = modifyLine(line).toString();
                        writer.write(modifiedLine);
                        writer.newLine();
                    }
                }
            }
        }catch (IOException e) {
            e.printStackTrace();
        }
        replaceFileWithUpdated(inputFile, outputFile);
    }

    private File createTempFile(String filePath) {
        try {
            return File.createTempFile(filePath + ".tmp", null);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void cleanUnnecessaryCuts(String filePath) {
        File inputFile = new File(filePath);
        File outputFile = new File(filePath + ".tmp");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {
            try (LineIterator lineIterator = FileUtils.lineIterator(inputFile, "UTF-8")) {
                boolean hasToClean = false;
                while (lineIterator.hasNext()) {
                    String line = lineIterator.nextLine();
                    if(isACutLine(line)){
                        if (!hasToClean) {
                            writer.write(line);
                            writer.newLine();
                            hasToClean = true;
                        }
                    }else{
                        writer.write(line);
                        writer.newLine();
                        hasToClean = false;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        replaceFileWithUpdated(inputFile, outputFile);
    }

    private StringBuilder modifyLine(String line) {
        StringBuilder modifiedContent = new StringBuilder();
        if (line.contains(AHEAD_TAG_IF)) {
            modifiedContent.append(CUT_TAG);
        } else if (line.contains(AHEAD_TAG_ELIF)) {
            modifiedContent.append(CUT_TAG);
        } else if (line.contains(AHEAD_TAG_ELSE)) {
            modifiedContent.append(CUT_TAG);
        } else if (line.contains(AHEAD_TAG_END)) {
            modifiedContent.append(CUT_TAG);
        } else{
            modifiedContent.append(line);
        }
        return modifiedContent;
    }

    private boolean isACutLine(String line) {
        StringBuilder cut = new StringBuilder();
        cut.append(CUT_TAG);
        return line.contentEquals(cut);
    }

    private void replaceFileWithUpdated(File originalFile, File updatedFile) {
        try {
            FileUtils.forceDelete(originalFile);
            FileUtils.moveFile(updatedFile, originalFile);
            System.out.println("File updated successfully!");
        } catch (IOException e) {
            System.err.println("Failed to update the file.");
            e.printStackTrace();
        }
    }
}
