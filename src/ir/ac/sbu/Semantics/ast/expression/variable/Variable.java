package ir.ac.sbu.Semantics.ast.expression.variable;

import ir.ac.sbu.Semantics.ProgramStructure.ScopeHandler;
import ir.ac.sbu.Semantics.ast.expression.Expression;
import ir.ac.sbu.Semantics.ProgramStructure.Descriptors.DSCP;
import org.objectweb.asm.Type;

public abstract class Variable extends Expression {

    String name;

    protected Variable(Type type) {
        super(type);
    }

    public String getName(){
        return name;
    }

    @Override
    public Type getType() {
        return getDSCP().getType();
    }

    public DSCP getDSCP() {
        return ScopeHandler.getInstance().getDescriptor(name);
    }
}
