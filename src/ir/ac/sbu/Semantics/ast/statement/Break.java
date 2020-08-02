package ir.ac.sbu.Semantics.ast.statement;

import ir.ac.sbu.Semantics.ProgramStructure.ScopeType;
import ir.ac.sbu.Semantics.ProgramStructure.SymbolTable;
import ir.ac.sbu.Semantics.ProgramStructure.ScopeHandler;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;

import static org.objectweb.asm.Opcodes.GOTO;

public class Break extends Statement {

    public Break() {
    }

    @Override
    public void codegen(ClassWriter cw, MethodVisitor mv) {
        if (ScopeHandler.getInstance().canHaveBreak()) {
            int i = ScopeHandler.getInstance().getStackScopes().size() - 1;
            for (; i >= 0; i--) {
                SymbolTable scope = ScopeHandler.getInstance().getStackScopes().get(i);
                if (scope.getScopeType() == ScopeType.LOOP) {
                    mv.visitJumpInsn(GOTO, ScopeHandler.getInstance().getInnerLoop().getEnd());
                    return;
                } else if (scope.getScopeType() == ScopeType.SWITCH) {
                    mv.visitJumpInsn(GOTO, ScopeHandler.getInstance().getLastSwitch().getEnd());
                    return;
                }
            }
        } else
            throw new RuntimeException("This part is not switch nor Loop");
    }
}
