package ic.unicamp.fb.scanner;

public class ScannerBlock {

    private String code; // id sequencial
    private String content;
    //pos
    //sha
    private BlockState state;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public BlockState getState() {
        return state;
    }

    public void setState(BlockState state) {
        this.state = state;
    }
}
