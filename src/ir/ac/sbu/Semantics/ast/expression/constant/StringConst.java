package ir.ac.sbu.Semantics.ast.expression.constant;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

public class StringConst extends ConstantExpression {
    private String value;

    public StringConst(String value){
        super(Type.getType(String.class));
        this.value = value;
    }


    @Override
    public Object getValue() {
        return value;
    }

    @Override
    public void codegen(ClassWriter cw, MethodVisitor mv) {
        mv.visitLdcInsn(value);
    }
}
