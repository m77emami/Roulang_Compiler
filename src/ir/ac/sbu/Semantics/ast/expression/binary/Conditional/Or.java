package ir.ac.sbu.Semantics.ast.expression.binary.Conditional;

import ir.ac.sbu.Semantics.ast.expression.Expression;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;

public class Or extends ConditionalExpression {

    public Or(Expression firstOperand, Expression secondOperand) {
        super(firstOperand, secondOperand);
    }

    @Override
    public void codegen(ClassWriter cw, MethodVisitor mv) {
        AndOr(false, mv, cw);
    }
}
