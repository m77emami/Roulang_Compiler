package ir.ac.sbu.Semantics.ast.statement.loop;

import ir.ac.sbu.Semantics.ast.block.Block;
import ir.ac.sbu.Semantics.ast.statement.Statement;
import org.objectweb.asm.Label;

public abstract class Loop extends Statement{

    protected Block block;
    Label startLoop = new Label();
    Label end = new Label();

    Loop(Block block) {
        this.block = block;
    }

    public Label getStartLoop() {
        return startLoop;
    }

    public Label getEnd() {
        return end;
    }
}
