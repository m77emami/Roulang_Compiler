package ir.ac.sbu.Parser;

import java.util.List;

class LLCell {
    private Action action;
    private int target;
    private List<String> functions;

    public LLCell(Action action, int target, List<String> functions) {
        this.action = action;
        this.target = target;
        this.functions = functions;
    }

    public Action getAction() {
        return action;
    }

    public int getTarget() {
        return target;
    }

    public List<String> getFunction() {
        return functions;
    }
}
