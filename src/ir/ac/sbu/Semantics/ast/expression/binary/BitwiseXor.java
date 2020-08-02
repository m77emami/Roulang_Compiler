package ir.ac.sbu.Semantics.ast.expression.binary;

import ir.ac.sbu.Semantics.ast.expression.Expression;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import static org.objectweb.asm.Opcodes.*;

public class BitwiseXor extends BinaryExpression {

    public BitwiseXor(Expression firstOperand, Expression secondOperand) {
        super(firstOperand, secondOperand);
    }

    @Override
    public void codegen(ClassWriter cw, MethodVisitor mv) {
        getFirstOperand().codegen(cw, mv);
        getSecondOperand().codegen(cw, mv);

        mv.visitInsn(type.getOpcode(IXOR));
    }
}
