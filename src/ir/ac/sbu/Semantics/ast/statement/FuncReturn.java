package ir.ac.sbu.Semantics.ast.statement;

import ir.ac.sbu.Semantics.ast.declaration.function.FunctionDeclaration;
import ir.ac.sbu.Semantics.ast.expression.Expression;
import ir.ac.sbu.Semantics.ast.expression.unary.TypeCast;
import ir.ac.sbu.Semantics.ProgramStructure.SymbolTable;
import ir.ac.sbu.Semantics.ProgramStructure.ScopeHandler;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

import static org.objectweb.asm.Opcodes.*;

public class FuncReturn extends Statement {

    private Expression expression;
    private SymbolTable scope;

    public FuncReturn(Expression expression, FunctionDeclaration funcDcl) {
        this.expression = expression;
        funcDcl.addReturn(this);
        if((expression == null && !funcDcl.getType().equals(Type.VOID_TYPE)) ||
                (expression != null && (funcDcl.getType().equals(Type.VOID_TYPE) ||
                        !funcDcl.getType().equals(expression.getType()) )))
            throw new RuntimeException("Return type mismatch");
    }

    @Override
    public void codegen(ClassWriter cw, MethodVisitor mv) {
        FunctionDeclaration functionDcl = ScopeHandler.getInstance().getLastFunction();
        scope = ScopeHandler.getInstance().getLastScope();
        int index = functionDcl.getReturns().indexOf(this);
        for (int i = 0; i < index; i++)  {
            FuncReturn funcReturn = functionDcl.getReturns().get(i);
            if(funcReturn.scope.equals(scope)) {
                throw new RuntimeException("more than one return in single scope -__-");
            }
        }
        if(expression == null) {
            mv.visitInsn(RETURN);
        }
        else {
            expression.codegen(cw, mv);
            //mv.visitInsn(Cast.getOpcode(expression.getType(),functionDcl.getType()));
            if(!expression.getType().equals(functionDcl.getType()))
                throw new RuntimeException("Return types don't match");
            mv.visitInsn(expression.getType().getOpcode(IRETURN));
        }

    }
}
