package ic.unicamp.fb.scanner;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AHEADProcessor {

    private static final String AHEAD_TAG_IF = "#if";
    private static final String AHEAD_TAG_SUFFIX = "#endif";
    private static final String AHEAD_TAG_ELSE = "#else";
    private static final String AHEAD_TAG_ELIF = "#elif";

    public static void main(String[] args) {
        Path resourcesPath = Paths.get("src", "test", "resources", "spl");
        Path filePath = resourcesPath.resolve("SimulationUnit.java");

        try (LineIterator lineIterator = FileUtils.lineIterator(filePath.toFile())) {
            AHEADNode root = processFile(lineIterator);
            printGraph(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static AHEADNode processFile(LineIterator lineIterator) {
        AHEADNode root = new AHEADNode(null, "root", "");
        AHEADNode currentParent = root;
        AHEADNode currentChild = null;
        boolean isInIfTag = false;
        boolean isInElseTag = false;
        boolean isInElifTag = false;

        while (lineIterator.hasNext()) {
            String line = lineIterator.nextLine();

            if (line.contains(AHEAD_TAG_IF)) {
                String tagExpression = extractTagExpression(line.trim());
                currentChild = new AHEADNode(currentParent, AHEAD_TAG_IF + " " + tagExpression, line);
                currentParent.addChild(currentChild);
                currentParent = currentChild;
                isInIfTag = true;
                isInElseTag = false;
                isInElifTag = false;
                continue;
            }

            if (line.contains(AHEAD_TAG_ELIF)) {
                currentParent = currentParent.getParent();
                currentChild = new AHEADNode(currentParent, AHEAD_TAG_ELIF + " " + extractTagExpression(line.trim()), line);
                currentParent.addChild(currentChild);
                currentParent = currentChild;
                isInElifTag = true;
                isInElseTag = false;
                continue;
            }

            if (line.contains(AHEAD_TAG_ELSE)) {
                currentParent = currentParent.getParent();
                currentChild = new AHEADNode(currentParent, AHEAD_TAG_ELSE, line);
                currentParent.addChild(currentChild);
                currentParent = currentChild;
                isInElseTag = true;
                isInElifTag = false;
                continue;
            }

            if (isInIfTag || isInElseTag || isInElifTag) {
                currentChild.addContent(line);
            }

            if (line.contains(AHEAD_TAG_SUFFIX)) {
                currentParent = currentParent.getParent();
                isInIfTag = false;
                isInElseTag = false;
                isInElifTag = false;
            }
        }

        return root;
    }

    private static String extractTagExpression(String line) {
        Pattern pattern = Pattern.compile("#(?:if|elif)\\s+(.+)");
        Matcher matcher = pattern.matcher(line);
        if (matcher.find()) {
            return matcher.group(1);
        }
        throw new IllegalArgumentException("Invalid expression: " + line);
    }

    private static void printGraph(AHEADNode node) {
        printNode(node, 0);
    }

    private static void printNode(AHEADNode node, int indentLevel) {
        StringBuilder indent = new StringBuilder();
        for (int i = 0; i < indentLevel; i++) {
            indent.append("  ");
        }

        System.out.println(indent.toString() + "Type: " + node.getType());
        System.out.println(indent.toString() + "Content: " + node.getContent());

        List<AHEADNode> children = node.getChildren();
        for (AHEADNode child : children) {
            printNode(child, indentLevel + 1);
        }
    }
}