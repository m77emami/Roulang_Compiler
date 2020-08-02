package ir.ac.sbu.Semantics.ast.expression.constant;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

public class CharConst extends ConstantExpression {

    private Character value;

    public CharConst(Character value){
        super(Type.CHAR_TYPE);
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
