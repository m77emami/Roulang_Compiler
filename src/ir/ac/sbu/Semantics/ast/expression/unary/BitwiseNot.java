package ir.ac.sbu.Semantics.ast.expression.unary;

import ir.ac.sbu.Semantics.ast.expression.Expression;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import static org.objectweb.asm.Opcodes.*;

public class BitwiseNot extends UnaryExpression{

    public BitwiseNot(Expression operand) {
        super(operand);
    }

    @Override
    public void codegen(ClassWriter cw, MethodVisitor mv) {
        getOperand().codegen(cw, mv);
        mv.visitInsn(ICONST_M1);
        mv.visitInsn(type.getOpcode(IXOR));
    }
}
