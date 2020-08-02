package ir.ac.sbu.Semantics.ast.declaration.variable;

import ir.ac.sbu.Semantics.ast.expression.Expression;
import ir.ac.sbu.Semantics.ast.expression.constant.IntegerConst;
import ir.ac.sbu.Semantics.ProgramStructure.Descriptors.*;
import ir.ac.sbu.Semantics.ProgramStructure.ScopeHandler;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

import java.util.ArrayList;
import java.util.List;

import static org.objectweb.asm.Opcodes.*;

public class ArrDcl extends VarDCL {

    private List<Expression> dimensions;
    private int dimNum;

    public ArrDcl(String name, Type type, boolean global, int dimNum) {
        this.name = name;
        this.type = type;
        this.global = global;
        dimensions = new ArrayList<>(dimNum);
        this.dimNum = dimNum;
    }

    public ArrDcl(String name, String stringType, boolean global, Integer dimNum, Type type, List<Expression> dimensions) {
        this.name = name;
        if (!stringType.equals("auto")) {
            if (!ScopeHandler.getTypeFromName(stringType).equals(type))
                throw new RuntimeException("the types of array doesn't match");
        } else if (dimensions == null)
            throw new RuntimeException("auto variables must have been initialized");
        if (dimNum != null) {
            if (dimNum != dimensions.size())
                throw new RuntimeException("dimensions are't correct");
            this.dimNum = dimNum;
        }
        this.type = type;
        this.global = global;
        this.dimensions = dimensions;
    }

    @Override
    public void codegen(ClassWriter cw, MethodVisitor mv) {
        if (global){
            executeGlobalExp(cw, mv);
            //TODO what is it?
            String repeatedArray = new String(new char[dimensions.size()]).replace("\0", "[");
            Type arrayType = Type.getType(repeatedArray + type.getDescriptor());
            cw.visitField(ACC_STATIC, name, arrayType.getDescriptor(), null, null).visitEnd();
        }
        else {
            for (Expression dim :
                    dimensions ) {
                dim.codegen(cw, mv);
            }
            if(dimensions.size() == 0){
                new IntegerConst(1000).codegen(cw, mv);
            }
            if (dimNum == 1) {
                if (type.getDescriptor().endsWith(";"))
                    mv.visitTypeInsn(ANEWARRAY, type.getElementType().getInternalName());
                else
                    mv.visitIntInsn(NEWARRAY, ScopeHandler.getTType(type.getElementType()));
            } else{
                String t = "";
                for (int i = 0; i < dimNum; i++) {
                    t += "[";
                }
                t += type.getDescriptor();
                mv.visitMultiANewArrayInsn(t, dimensions.size());
            }
            mv.visitVarInsn(ASTORE, ScopeHandler.getInstance().getIndex());
        }
    }

    private void executeGlobalExp(ClassWriter cw,MethodVisitor mv){
        for (Expression dim :
                dimensions) {
            dim.codegen(cw, mv);
        }
    }

    public static void declare(String name,Type type,List<Expression> dimensions,int dimNum,boolean global) {
        DSCP dscp;
        if (!global)
            dscp = new LocalArrDSCP(type, true, ScopeHandler.getInstance().getIndex(), dimensions, dimNum);
        else
            dscp = new GlobalArrDSCP(type, true, dimensions, dimNum);
        ScopeHandler.getInstance().addVariable(name, dscp);

    }

    public void setDimensions(List<Expression> dimensions) {
        this.dimensions = dimensions;
    }
}
