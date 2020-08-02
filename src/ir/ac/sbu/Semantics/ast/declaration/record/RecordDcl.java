package ir.ac.sbu.Semantics.ast.declaration.record;

import ir.ac.sbu.Semantics.ast.declaration.Declaration;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;

public class RecordDcl implements Declaration {

    private String name;

    @Override
    public void codegen(ClassWriter cw, MethodVisitor mv) {
    }

    public String getName() {
        return name;
    }
}
