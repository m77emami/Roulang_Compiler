package ir.ac.sbu.Semantics.ast.expression.constant;

import ir.ac.sbu.Semantics.ast.expression.Expression;
import org.objectweb.asm.Type;

public abstract class ConstantExpression extends Expression {
    protected ConstantExpression(Type type) {
        super(type);
    }

    public abstract Object getValue();
}
