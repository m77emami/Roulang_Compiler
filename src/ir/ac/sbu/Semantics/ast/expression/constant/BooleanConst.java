package ir.ac.sbu.Semantics.ast.expression.constant;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;
import static org.objectweb.asm.Opcodes.*;

public class BooleanConst extends ConstantExpression {

    private Boolean value;

    public BooleanConst(Boolean value){
        super(Type.BOOLEAN_TYPE);
        this.value = value;
    }

    @Override
    public Object getValue() {
        return value;
    }

    @Override
    public void codegen(ClassWriter cw, MethodVisitor mv) {
        mv.visitInsn(value ? ICONST_1:ICONST_0);
    }
}
