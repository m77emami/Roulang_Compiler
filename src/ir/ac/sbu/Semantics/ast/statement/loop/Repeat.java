package ir.ac.sbu.Semantics.ast.statement.loop;

import ir.ac.sbu.Semantics.ast.block.Block;
import ir.ac.sbu.Semantics.ast.expression.Expression;
import ir.ac.sbu.Semantics.ast.expression.binary.Conditional.NE;
import ir.ac.sbu.Semantics.ast.expression.constant.IntegerConst;
import ir.ac.sbu.Semantics.ProgramStructure.ScopeType;
import ir.ac.sbu.Semantics.ProgramStructure.SymbolTable;
import ir.ac.sbu.Semantics.ProgramStructure.ScopeHandler;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

import static org.objectweb.asm.Opcodes.IFNE;

public class Repeat extends Loop{
    private Expression expression;

    public Repeat(Block block, Expression expression) {
        super(block);
        this.expression = expression;
    }

    @Override
    public void codegen(ClassWriter cw, MethodVisitor mv) {
        ScopeHandler.getInstance().addScope(ScopeType.LOOP);
        ScopeHandler.getInstance().setInnerLoop(this);
        mv.visitLabel(startLoop);
        block.codegen(cw, mv);
        NE notEqual = new NE(expression, new IntegerConst(0));
        notEqual.codegen(cw, mv);
        mv.visitJumpInsn(IFNE, startLoop);
        mv.visitLabel(end);
        ScopeHandler.getInstance().popScope();
        ScopeHandler.getInstance().setInnerLoop(null);
    }
}
