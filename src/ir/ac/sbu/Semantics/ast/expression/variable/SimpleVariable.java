package ir.ac.sbu.Semantics.ast.expression.variable;

import ir.ac.sbu.Semantics.ProgramStructure.Descriptors.DSCP;
import ir.ac.sbu.Semantics.ProgramStructure.Descriptors.LocalVarDSCP;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

import static org.objectweb.asm.Opcodes.GETSTATIC;
import static org.objectweb.asm.Opcodes.ILOAD;

public class SimpleVariable extends Variable{

    public SimpleVariable(String name, Type type){
        super(type);
        this.name = name;
    }

    @Override
    public void codegen(ClassWriter cw, MethodVisitor mv) {
        DSCP dscp = getDSCP();
        if(!dscp.isValid())
            throw new RuntimeException("you should set initial value to variable");
        if (dscp instanceof LocalVarDSCP) {
            int index = ((LocalVarDSCP) dscp).getIndex();
            mv.visitVarInsn(type.getOpcode(ILOAD), index);
        } else {
            mv.visitFieldInsn(GETSTATIC,"Main" , name, type.getDescriptor());
        }
    }
}
