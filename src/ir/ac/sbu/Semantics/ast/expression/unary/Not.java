package ir.ac.sbu.Semantics.ast.expression.unary;

import ir.ac.sbu.Semantics.ast.expression.Expression;
import ir.ac.sbu.Semantics.ast.expression.constant.ConstantExpression;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;
import static org.objectweb.asm.Opcodes.*;

public class Not extends UnaryExpression {

    public Not(Expression operand) {
        super(operand);
    }

    @Override
    public void codegen(ClassWriter cw, MethodVisitor mv) {
        getOperand().codegen(cw, mv);

        if(type != Type.INT_TYPE && type != Type.LONG_TYPE && type != Type.BOOLEAN_TYPE)
            throw new RuntimeException("Cannot apply NOT operation.");

        Object res = ((ConstantExpression)getOperand()).getValue();
        if(res instanceof Boolean)
            mv.visitInsn(((Boolean)res) ? ICONST_1:ICONST_0);
        if(res instanceof Integer)
            mv.visitInsn(((Integer)res) != 0 ? ICONST_1:ICONST_0);
        if(res instanceof Long)
            mv.visitInsn(((Long)res) != 0 ? ICONST_1:ICONST_0);
    }
}
