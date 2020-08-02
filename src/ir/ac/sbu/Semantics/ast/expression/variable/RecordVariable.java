package ir.ac.sbu.Semantics.ast.expression.variable;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

public class RecordVariable extends Variable {

    protected RecordVariable(Type type) {
        super(type);
    }

    @Override
    public void codegen(ClassWriter cw, MethodVisitor mv) {
    }
}
