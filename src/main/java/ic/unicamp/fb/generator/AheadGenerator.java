package ic.unicamp.fb.generator;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AheadGenerator implements IGenerator {

    private static final String CUT_TAG = "b>>[cut]";
    private static final Pattern AHEAD_TAG_IF_PATTERN = Pattern.compile("//\\s*#if|//#if");
    private static final Pattern AHEAD_TAG_ELIF_PATTERN = Pattern.compile("//\\s*#elif|//#elif");
    private static final Pattern AHEAD_TAG_ELSE_PATTERN = Pattern.compile("//\\s*#else|//#else");
    private static final Pattern AHEAD_TAG_END_PATTERN = Pattern.compile("//\\s*#endif|//#endif");


    @Override
    public void generateCuts(String filePath) {
        File inputFile = new File(filePath);
        File outputFile = createTempFile(filePath);
        try {
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
        } catch (IOException e) {
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
                    if (isACutLine(line)) {
                        if (!hasToClean) {
                            writer.write(line);
                            writer.newLine();
                            hasToClean = true;
                        }
                    } else {
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
        Matcher matcherIF = AHEAD_TAG_IF_PATTERN.matcher(line);
        Matcher matcherELIF = AHEAD_TAG_ELIF_PATTERN.matcher(line);
        Matcher matcherELSE = AHEAD_TAG_ELSE_PATTERN.matcher(line);
        Matcher matcherEND = AHEAD_TAG_END_PATTERN.matcher(line);

        if (matcherIF.find()) {
            modifiedContent.append(CUT_TAG);
        } else if (matcherELIF.find()) {
            modifiedContent.append(CUT_TAG);
        } else if (matcherELSE.find()) {
            modifiedContent.append(CUT_TAG);
        } else if (matcherEND.find()) {
            modifiedContent.append(CUT_TAG);
        } else {
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
