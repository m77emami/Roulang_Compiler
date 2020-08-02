package ir.ac.sbu.Semantics.ast.expression.variable;

import ir.ac.sbu.Semantics.ast.expression.Expression;
import ir.ac.sbu.Semantics.ast.expression.binary.Conditional.GE;
import ir.ac.sbu.Semantics.ProgramStructure.Descriptors.DSCP;
import ir.ac.sbu.Semantics.ProgramStructure.Descriptors.GlobalArrDSCP;
import ir.ac.sbu.Semantics.ProgramStructure.ScopeHandler;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;
import java.util.List;
import static org.objectweb.asm.Opcodes.*;

public class ArrayVariable extends Variable {

    private List<Expression> dimensions;

    public List<Expression> getDimensions() {
        return dimensions;
    }

    public ArrayVariable(String name, List<Expression> dimensions, Type type) {
        super(type);
        this.name = name;
        this.dimensions = dimensions;
    }

    @Override
    public void codegen(ClassWriter cw, MethodVisitor mv) {

        new SimpleVariable(name,type).codegen(cw, mv);
        Label exceptionLabel = new Label();
        Label endLabel = new Label();

        for (int i = 0; i < dimensions.size() - 1; i++) {
            dimensions.get(i).codegen(cw, mv);
            DSCP dscp = ScopeHandler.getInstance().getDescriptor(name);
            if (dscp instanceof GlobalArrDSCP){
                GE greaterEqual = new GE(dimensions.get(i), ((GlobalArrDSCP) dscp).getListOfLengths().get(i));
                greaterEqual.codegen(cw, mv);
                mv.visitJumpInsn(IFGE, exceptionLabel);
            }
            if (dimensions.get(i).getType().equals(Type.INT_TYPE))
                throw new RuntimeException("Index should be an integer number");
            mv.visitInsn(AALOAD);
        }
        // must load the last index separately
        dimensions.get(dimensions.size() - 1).codegen(cw, mv);
        if(type.getDescriptor().endsWith(";")) // we have array of records
            mv.visitInsn(AALOAD);
        else
            mv.visitInsn(type.getOpcode(IALOAD));

        mv.visitJumpInsn(GOTO, endLabel);
        mv.visitLabel(exceptionLabel);
        mv.visitTypeInsn(NEW, "java/lang/RuntimeException");
        mv.visitInsn(DUP);
        mv.visitMethodInsn(INVOKESPECIAL, "java/lang/RuntimeException", "<init>", "()V", false);
        mv.visitInsn(ATHROW);
        mv.visitLabel(endLabel);
    }

    public void setDimensions(List<Expression> dimensions) {
        this.dimensions = dimensions;
    }
}
