package ir.ac.sbu.Semantics.ast.statement.Condition;

import ir.ac.sbu.Semantics.ast.block.Block;
import ir.ac.sbu.Semantics.ast.expression.Expression;
import ir.ac.sbu.Semantics.ast.statement.Statement;
import ir.ac.sbu.Semantics.ProgramStructure.ScopeType;
import ir.ac.sbu.Semantics.ProgramStructure.ScopeHandler;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

import java.util.ArrayList;

import static org.objectweb.asm.Opcodes.GOTO;

public class Switch extends Statement{
    private Expression expression;
    private ArrayList <Case> cases;
    private Block defaultBlock;
    private Label defaultLabel = new Label();
    private Label lookUpTable = new Label();
    private Label end = new Label();

    public Switch(Expression expression, ArrayList<Case> cases, Block defaultBlock){
        this.expression = expression;
        this.cases = cases;
        this.defaultBlock = defaultBlock;
    }

    public void addCase(Case caseSt){
        if(cases == null)
            cases = new ArrayList<>();
        cases.add(caseSt);
    }

    public Label getEnd() {
        return end;
    }

    @Override
    public void codegen(ClassWriter cw, MethodVisitor mv) {
        ScopeHandler.getInstance().addScope(ScopeType.SWITCH);
        ScopeHandler.getInstance().setLastSwitch(this);
        Label [] labels = new Label[cases.size()];
        int [] keys = new int[cases.size()];
        int i = 0 ;
        expression.codegen(cw, mv);
        mv.visitJumpInsn(GOTO, lookUpTable);
        for(Case c : cases){
            c.jump = end;
            c.codegen(cw, mv);
            labels[i] = c.StartCase;
            keys[i++] = (int) c.exp.getValue();
        }
        mv.visitLabel(defaultLabel);
        if (defaultBlock != null) {
            ScopeHandler.getInstance().addScope(ScopeType.SWITCH);
            defaultBlock.codegen(cw, mv);
            ScopeHandler.getInstance().popScope();
        }
        mv.visitJumpInsn(GOTO, end);
        mv.visitLabel(lookUpTable);
        mv.visitLookupSwitchInsn(defaultLabel, keys, labels);
        mv.visitLabel(end);
        ScopeHandler.getInstance().popScope();
        ScopeHandler.getInstance().setLastSwitch(null);
    }

    public void setDefaultBlock(Block defaultBlock) {
        this.defaultBlock = defaultBlock;
    }
}
