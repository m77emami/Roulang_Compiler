package ir.ac.sbu.Semantics.ast.expression;

import ir.ac.sbu.Semantics.ast.Operation;
import ir.ac.sbu.Semantics.ProgramStructure.ScopeHandler;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

public class Input extends Expression implements Operation {


    public Input(Type type) {
        super(type);
    }

    @Override
    public void codegen(ClassWriter cw, MethodVisitor mv) {
        mv.visitTypeInsn(Opcodes.NEW, "java/util/Scanner");
        mv.visitInsn(Opcodes.DUP);
        mv.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "in", "Ljava/io/InputStream;");
        mv.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/util/Scanner", "<init>", "(Ljava/io/InputStream;)V", false);
        if(type == null){
            type = ScopeHandler.getTypeFromName("String");
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/util/Scanner", "nextLine", "()Ljava/lang/String;", false);

        }else{
            switch (type.getDescriptor()){
                case "I":
                    mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/util/Scanner", "nextInt", "()" + type.getDescriptor(), false);
                    break;
                case "J":
                    mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/util/Scanner", "nextLong", "()" + type.getDescriptor(), false);
                    break;
                case "F":
                    mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/util/Scanner", "nextFloat", "()" + type.getDescriptor(), false);
                    break;
                case "D":
                    mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/util/Scanner", "nextDouble", "()" + type.getDescriptor(), false);
                    break;
                case "Z":
                    mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/util/Scanner", "nextBoolean", "()" + type.getDescriptor(), false);
                    break;

            }
        }
    }
}
