package ir.ac.sbu.Semantics.ast.statement.assignment;

import ir.ac.sbu.Semantics.ast.expression.Expression;
import ir.ac.sbu.Semantics.ast.expression.variable.SimpleVariable;
import ir.ac.sbu.Semantics.ast.expression.variable.Variable;
import ir.ac.sbu.Semantics.ast.statement.Statement;
import ir.ac.sbu.Semantics.ast.statement.loop.InitExp;
import ir.ac.sbu.Semantics.ast.statement.loop.StepExp;
import ir.ac.sbu.Semantics.ProgramStructure.Descriptors.DSCP;
import ir.ac.sbu.Semantics.ProgramStructure.Descriptors.GlobalVarDSCP;
import ir.ac.sbu.Semantics.ProgramStructure.Descriptors.LocalVarDSCP;
import ir.ac.sbu.Semantics.ProgramStructure.ScopeHandler;


public abstract class Assignment extends Statement implements InitExp, StepExp {
    protected Expression expression;
    protected Variable variable;

    Assignment(Expression expression, Variable variable) {
        this.expression = expression;
        this.variable = variable;
    }

    protected void checkConst() {
        boolean isConst = false;
        if (variable instanceof SimpleVariable) {
            DSCP dscp = ScopeHandler.getInstance().getDescriptor(variable.getName());
            isConst = (dscp instanceof GlobalVarDSCP) ? ((GlobalVarDSCP) dscp).isConst() : ((LocalVarDSCP) dscp).isConst();
        }
        if (isConst)
            throw new RuntimeException("Const variables can't assign");
    }

}
