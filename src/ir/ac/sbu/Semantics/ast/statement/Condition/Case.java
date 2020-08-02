package ir.ac.sbu.Semantics.ast.statement.Condition;

import ir.ac.sbu.Semantics.ast.block.Block;
import ir.ac.sbu.Semantics.ast.expression.constant.IntegerConst;
import ir.ac.sbu.Semantics.ast.statement.Statement;
import ir.ac.sbu.Semantics.ProgramStructure.ScopeType;
import ir.ac.sbu.Semantics.ProgramStructure.ScopeHandler;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

import static org.objectweb.asm.Opcodes.GOTO;

public class Case extends Statement {
    IntegerConst exp;
    private Block block;
    Label StartCase = new Label();
    Label jump;
    public Case(IntegerConst exp, Block block){
        this.exp = exp;
        this.block = block;
    }
    @Override
    public void codegen(ClassWriter cw, MethodVisitor mv) {
        mv.visitLabel(StartCase);
        ScopeHandler.getInstance().addScope(ScopeType.SWITCH);
        block.codegen(cw, mv);
        ScopeHandler.getInstance().popScope();
        mv.visitJumpInsn(GOTO,jump);
    }
}
