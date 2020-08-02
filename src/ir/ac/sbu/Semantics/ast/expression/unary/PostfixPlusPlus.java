package ir.ac.sbu.Semantics.ast.expression.unary;

import ir.ac.sbu.Semantics.ast.Operation;
import ir.ac.sbu.Semantics.ast.expression.Expression;
import ir.ac.sbu.Semantics.ast.expression.constant.IntegerConst;
import ir.ac.sbu.Semantics.ast.expression.variable.SimpleVariable;
import ir.ac.sbu.Semantics.ast.expression.variable.Variable;
import ir.ac.sbu.Semantics.ast.statement.assignment.SumAssign;
import ir.ac.sbu.Semantics.ast.statement.loop.InitExp;
import ir.ac.sbu.Semantics.ast.statement.loop.StepExp;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

public class PostfixPlusPlus extends UnaryExpression implements InitExp, StepExp, Operation {

    public PostfixPlusPlus(Expression operand) {
        super(operand);
    }

    @Override
    public void codegen(ClassWriter cw, MethodVisitor mv) {
        type = getOperand().getType();
        if (!(getOperand() instanceof Variable) || (type != Type.INT_TYPE && type != Type.DOUBLE_TYPE && type != Type.LONG_TYPE && type != Type.FLOAT_TYPE))
            throw new RuntimeException("the operand is wrong");
        Variable var = (Variable)getOperand();
        checkConst(var);
        // storing the last value in temporary variable
        new SimpleVariable(var.getName(), var.getType()).codegen(cw, mv);
        new SumAssign(new IntegerConst(1), var).codegen(cw, mv);
    }
}
