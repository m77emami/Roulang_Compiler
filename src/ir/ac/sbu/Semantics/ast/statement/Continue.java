package ir.ac.sbu.Semantics.ast.statement;

import ir.ac.sbu.Semantics.ProgramStructure.ScopeHandler;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;

import static org.objectweb.asm.Opcodes.GOTO;

public class Continue extends Statement {

    public Continue() {}

    @Override
    public void codegen(ClassWriter cw, MethodVisitor mv) {
        if(ScopeHandler.getInstance().getInnerLoop() != null)
            mv.visitJumpInsn(GOTO, ScopeHandler.getInstance().getInnerLoop().getStartLoop());
        else
            throw new RuntimeException("This part is not switch nor Loop");
    }
}
