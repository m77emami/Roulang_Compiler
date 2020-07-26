package ir.sbu.ac.Parser;

public class TableElement {
    private Action action;
    private int nextNode;
    private String semantic;

    public TableElement(Action action, int nextNode, String semantic) {
        this.action = action;
        this.nextNode = nextNode;
        this.semantic = semantic;
    }

    public Action getAction() {
        return action;
    }

    public int getNextNode() {
        return nextNode;
    }

    public String getSemantic() {
        return semantic;
    }
}
