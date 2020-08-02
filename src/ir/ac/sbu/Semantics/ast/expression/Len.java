package ir.ac.sbu.Semantics.ast.expression;

import ir.ac.sbu.Semantics.ast.Operation;
import ir.ac.sbu.Semantics.ast.expression.variable.ArrayVariable;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;

import static org.objectweb.asm.Opcodes.*;

public class Len extends Expression implements Operation{

    private Expression expression;

    public Len(Expression expression) {
        super(expression.getType());
        this.expression = expression;
    }

    @Override
    public void codegen(ClassWriter cw, MethodVisitor mv) {
        expression.codegen(cw, mv);
        if (expression instanceof ArrayVariable) {
            mv.visitInsn(ARRAYLENGTH);
        }
        else
            throw new RuntimeException("input of len function is not iterable");
    }
}
