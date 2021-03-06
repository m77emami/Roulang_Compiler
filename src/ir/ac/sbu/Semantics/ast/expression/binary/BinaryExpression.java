package ir.ac.sbu.Semantics.ast.expression.binary;

import ir.ac.sbu.Semantics.ast.expression.Expression;

public abstract class BinaryExpression extends Expression {
    private Expression firstOperand;
    private Expression secondOperand;

    public BinaryExpression(Expression firstOperand, Expression secondOperand) {
        super(firstOperand.getType());
//        if (firstOperand.getType() != secondOperand.getType())
//            throw new IllegalArgumentException("First operand and second operand do not have matching types.");
        this.firstOperand = firstOperand;
        this.secondOperand = secondOperand;
    }

    public Expression getFirstOperand() {
        return firstOperand;
    }

    public Expression getSecondOperand() {
        return secondOperand;
    }
}
