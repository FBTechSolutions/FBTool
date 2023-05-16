package ic.unicamp.fb.scanner;

import java.util.ArrayList;
import java.util.List;

public class AHEADNode {
    private AHEADNode parent;
    private String type;
    private String content;
    private List<AHEADNode> children;

    public AHEADNode(AHEADNode parent, String type, String content) {
        this.parent = parent;
        this.type = type;
        this.content = content;
        this.children = new ArrayList<>();
    }

    public AHEADNode getParent() {
        return parent;
    }

    public String getType() {
        return type;
    }

    public String getContent() {
        return content;
    }

    public List<AHEADNode> getChildren() {
        return children;
    }

    public void addChild(AHEADNode child) {
        children.add(child);
    }

    public void addContent(String line) {
        content += line + "\n";
    }
}