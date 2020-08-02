package ir.ac.sbu.Semantics.ast.expression.unary;

import ir.ac.sbu.Semantics.ast.expression.Expression;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class Negative extends UnaryExpression {

    public Negative(Expression operand) {
        super(operand);
    }

    @Override
    public void codegen(ClassWriter cw, MethodVisitor mv) {
        getOperand().codegen(cw, mv);
        mv.visitInsn(type.getOpcode(Opcodes.INEG));
    }
}
