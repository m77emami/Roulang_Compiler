package ir.ac.sbu.Semantics.ast.expression.unary;

import ir.ac.sbu.Semantics.ProgramStructure.Descriptors.DSCP;
import ir.ac.sbu.Semantics.ProgramStructure.Descriptors.GlobalVarDSCP;
import ir.ac.sbu.Semantics.ProgramStructure.Descriptors.LocalVarDSCP;
import ir.ac.sbu.Semantics.ProgramStructure.ScopeHandler;
import ir.ac.sbu.Semantics.ast.expression.Expression;
import ir.ac.sbu.Semantics.ast.expression.variable.SimpleVariable;
import ir.ac.sbu.Semantics.ast.expression.variable.Variable;

public abstract class UnaryExpression extends Expression {
    private Expression operand;

    public UnaryExpression(Expression operand) {
        super(operand.getType());
        this.operand = operand;
    }

    public Expression getOperand() {
        return operand;
    }

    //This is just for postpp,prepp,...
    protected void checkConst(Variable variable) {
        boolean isConst = false;
        if (variable instanceof SimpleVariable) {
            DSCP dscp = ScopeHandler.getInstance().getDescriptor(variable.getName());
            isConst = (dscp instanceof GlobalVarDSCP) ? ((GlobalVarDSCP) dscp).isConst() : ((LocalVarDSCP) dscp).isConst();
        }
        if (isConst)
            throw new RuntimeException("Const variables can't assign");
    }
}
