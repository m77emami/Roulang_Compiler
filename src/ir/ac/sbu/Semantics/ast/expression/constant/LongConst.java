package ir.ac.sbu.Semantics.ast.expression.constant;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import  org.objectweb.asm.Type;
import static org.objectweb.asm.Opcodes.*;

public class LongConst extends ConstantExpression {
    private Long value;

    public LongConst(Long value) {
        super(Type.LONG_TYPE);
        this.value = value;
    }

    @Override
    public void codegen(ClassWriter cw, MethodVisitor mv) {
        if (value == 0)
            mv.visitInsn(LCONST_0);
        else if (value == 1)
            mv.visitInsn(LCONST_1);
        else
            mv.visitLdcInsn(value);
    }

    @Override
    public Object getValue() {
        return this.value;
    }
}
