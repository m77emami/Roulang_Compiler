package ir.ac.sbu.Semantics.ast.expression;

import ir.ac.sbu.Semantics.ast.Operation;
import ir.ac.sbu.Semantics.ast.declaration.function.FunctionDeclaration;
import ir.ac.sbu.Semantics.ProgramStructure.ScopeHandler;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;
import java.util.ArrayList;
import java.util.List;
import static org.objectweb.asm.Opcodes.INVOKESTATIC;

public class FunctionCall extends Expression implements Operation {

    private String id;
    private List<Expression> parameters;

    private FunctionDeclaration func;

    public FunctionCall(String id, ArrayList<Expression> parameters) {
        super(null);
        this.id = id;
        this.parameters = parameters;
    }

    public void addParam(Expression exp){
        if(parameters == null)
            parameters = new ArrayList<>();
        parameters.add(exp);

    }


    @Override
    public void codegen(ClassWriter cw, MethodVisitor mv) {
        for (Expression parameter : parameters) {
            parameter.codegen(cw, mv);
        }
        ArrayList<Type> paramTypes = new ArrayList<>();
        for (Expression exp :
                parameters) {
            paramTypes.add(exp.getType());
        }
        this.func = ScopeHandler.getInstance().getFunction(id, paramTypes);
        this.type = func.getType();
        if (parameters.size() != func.getParameters().size())
            throw new RuntimeException("error in func parameter");
        mv.visitMethodInsn(INVOKESTATIC, "Main", func.getName(), func.getSignature(), false);
    }
}
