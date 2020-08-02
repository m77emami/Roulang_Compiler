package ir.ac.sbu.Semantics.ast.expression;

import ir.ac.sbu.Semantics.ast.Operation;
import ir.ac.sbu.Semantics.ast.expression.constant.IntegerConst;
import ir.ac.sbu.Semantics.ProgramStructure.ScopeHandler;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

public class SizeOf extends Expression implements Operation {

    private Integer value;

    public SizeOf(String baseType){
        super(Type.INT_TYPE);
        value = ScopeHandler.getSize(baseType);
    }

    public Object getValue() {
        return value;
    }

    @Override
    public void codegen(ClassWriter cw, MethodVisitor mv) {
        new IntegerConst(value).codegen(cw, mv);
    }
}