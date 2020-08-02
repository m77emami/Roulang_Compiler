package ir.ac.sbu.Semantics.ast.declaration.function;

import ir.ac.sbu.Semantics.ProgramStructure.ScopeHandler;
import ir.ac.sbu.Semantics.ProgramStructure.ScopeType;
import ir.ac.sbu.Semantics.ast.block.Block;
import ir.ac.sbu.Semantics.ast.declaration.Declaration;
import ir.ac.sbu.Semantics.ast.statement.FuncReturn;
import ir.ac.sbu.Semantics.ProgramStructure.Descriptors.LocalArrDSCP;
import ir.ac.sbu.Semantics.ProgramStructure.Descriptors.LocalDSCP;
import ir.ac.sbu.Semantics.ProgramStructure.Descriptors.LocalVarDSCP;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;
import java.util.*;
import static org.objectweb.asm.Opcodes.ACC_PUBLIC;
import static org.objectweb.asm.Opcodes.ACC_STATIC;

public class FunctionDeclaration implements Declaration {

    private Type type;
    private String name;
    private List<ParamPair> parameters = new ArrayList<>();
    private List<Type> paramTypes = new ArrayList<>();
    private String signature;
    private Block block;

    private List<FuncReturn> returns = new ArrayList<>();

    public void addReturn(FuncReturn funcReturn) {
        returns.add(funcReturn);
    }


    public void addParameter(String name,LocalDSCP dscp) {
        ParamPair param = new ParamPair(name,dscp);
        parameters.add(param);
        if (dscp instanceof LocalVarDSCP)
            paramTypes.add(dscp.getType());
        else if (dscp instanceof LocalArrDSCP)
            paramTypes.add(Type.getType("[" + dscp.getType()));
    }


    public FunctionDeclaration(Type type, String name, Block block, List<ParamPair> parameters) {
        this.type = type;
        this.name = name;
        this.block = block;
        this.parameters = parameters;

        // to fill paramTypes and make signature
        setSig();
    }

    public FunctionDeclaration(String name, String signature, Block block) {
        this.signature = signature;
        paramTypes = Arrays.asList(Type.getArgumentTypes(signature));
        this.type = Type.getType(signature.substring(signature.indexOf(')') + 1));
        this.name = name;
        this.block = block;

    }

    public void declare() {
        ScopeHandler.getInstance().addFunction(this);
    }


    @Override
    public void codegen(ClassWriter cw, MethodVisitor mv) {
        setSig();
        MethodVisitor methodVisitor = cw.visitMethod(ACC_PUBLIC + ACC_STATIC,
                name, this.signature, null, null);
        //Add current function's symbol table to stackScope
        ScopeHandler.getInstance().addScope(ScopeType.FUNCTION);
        ScopeHandler.getInstance().setLastFunction(this);
        methodVisitor.visitCode();
        block.codegen(cw, methodVisitor);
        if (returns.size() == 0)
            throw new RuntimeException("You must use at least one return statement in function!");
//        methodVisitor.visitMaxs(0, 0);
        methodVisitor.visitEnd();
        ScopeHandler.getInstance().popScope();
        ScopeHandler.getInstance().setLastFunction(null);
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof FunctionDeclaration && checkIfEqual(((FunctionDeclaration) o).name, ((FunctionDeclaration) o).paramTypes);
    }

    // check if two functions are the same
    public boolean checkIfEqual(String name, List<Type> paramTypes) {
        if (!this.name.equals(name))
            return false;
        if (paramTypes.size() != this.paramTypes.size())
            return false;
        for (int i = 0; i < paramTypes.size(); i++) {
            if (!this.paramTypes.get(i).equals(paramTypes.get(i)))
                return false;
        }

        return true;
    }

    private void setSig() {
        paramTypes = new ArrayList<>();
        // to fill paramTypes and make signature
        StringBuilder signature = new StringBuilder("(");
        for (ParamPair param:
                parameters) {
            Type type = param.dscp.getType();
            if (param.dscp instanceof LocalArrDSCP)
                type = Type.getType("[" + param.dscp.getType());
            paramTypes.add(type);
            signature.append(type);
        }
        signature.append(")");
        signature.append(type.toString());
        this.signature = signature.toString();
    }

    public String getName() {
        return name;
    }

    public Block getBlock() {
        return block;
    }

    public List<FuncReturn> getReturns() {
        return returns;
    }

    public Type getType() {
        return type;
    }

    public String getSignature() {
        return signature;
    }

    public List<ParamPair> getParameters() {
        return parameters;
    }

    public void setBlock(Block block) {
        this.block = block;
    }
}

class ParamPair{
    String name;
    LocalDSCP dscp;

    public ParamPair(String name, LocalDSCP dscp) {
        this.name = name;
        this.dscp = dscp;
    }
}
