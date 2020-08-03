package ir.ac.sbu.Semantics.ast.statement.Condition;

import ir.ac.sbu.Semantics.ast.block.Block;
import ir.ac.sbu.Semantics.ast.expression.Expression;
import ir.ac.sbu.Semantics.ast.expression.binary.Conditional.EQ;
import ir.ac.sbu.Semantics.ast.expression.binary.Conditional.NE;
import ir.ac.sbu.Semantics.ast.expression.constant.IntegerConst;
import ir.ac.sbu.Semantics.ast.statement.Statement;
import ir.ac.sbu.Semantics.ProgramStructure.ScopeType;
import ir.ac.sbu.Semantics.ProgramStructure.ScopeHandler;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

import static org.objectweb.asm.Opcodes.GOTO;
import static org.objectweb.asm.Opcodes.IFEQ;

public class If extends Statement {

    private Expression expression;
    private Block ifBlock, elseBlock;
    private Label startElse = new Label();
    private Label endElse = new Label();

    public If(Expression expression, Block ifBlock, Block elseBlock) {
        this.expression = expression;
        this.ifBlock = ifBlock;
        this.elseBlock = elseBlock;
    }

    @Override
    public void codegen(ClassWriter cw, MethodVisitor mv) {
        ScopeHandler.getInstance().addScope(ScopeType.IF);
        EQ notEqual = new EQ(expression, new IntegerConst(0));
        notEqual.codegen(cw, mv);
        mv.visitJumpInsn(IFEQ, startElse);
        ifBlock.codegen(cw, mv);
        mv.visitJumpInsn(GOTO, endElse);
        ScopeHandler.getInstance().popScope();
        if (elseBlock != null) {
            ScopeHandler.getInstance().addScope(ScopeType.IF);
            mv.visitLabel(startElse);
            elseBlock.codegen(cw, mv);
            ScopeHandler.getInstance().popScope();
        }
        else
            mv.visitLabel(startElse);
        mv.visitLabel(endElse);
    }

    public void setElseBlock(Block elseBlock) {
        this.elseBlock = elseBlock;
    }
}
