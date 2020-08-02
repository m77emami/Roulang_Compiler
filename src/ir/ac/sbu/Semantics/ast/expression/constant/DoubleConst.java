package ir.ac.sbu.Semantics.ast.expression.constant;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;
import static org.objectweb.asm.Opcodes.*;

public class DoubleConst extends ConstantExpression {

    private Double value;

    public DoubleConst(Double value){
        super(Type.DOUBLE_TYPE);
        this.value = value;
    }

    @Override
    public Object getValue() {
        return value;
    }

    @Override
    public void codegen(ClassWriter cw, MethodVisitor mv) {
        if (value == 0)
            mv.visitInsn(DCONST_0);
        else if (value == 1)
            mv.visitInsn(DCONST_1);
        else
            mv.visitLdcInsn(value);
    }
}
