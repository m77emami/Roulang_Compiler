package ir.ac.sbu.Semantics.ast.statement.loop;

import ir.ac.sbu.Semantics.ast.block.Block;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;

public class Foreach extends Loop {

    public Foreach(Block block) {
        super(block);
    }

    @Override
    public void codegen(ClassWriter cw, MethodVisitor mv) {

    }
}
