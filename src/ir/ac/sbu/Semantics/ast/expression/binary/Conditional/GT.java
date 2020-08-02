package ir.ac.sbu.Semantics.ast.expression.binary.Conditional;

import ir.ac.sbu.Semantics.ast.expression.Expression;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import static org.objectweb.asm.Opcodes.*;

public class GT extends ConditionalExpression {

    public GT(Expression firstOperand, Expression secondOperand) {
        super(firstOperand, secondOperand);
    }

    @Override
    public void codegen(ClassWriter cw, MethodVisitor mv) {
        cmp(IFLE, IF_ICMPLE, mv, cw);
    }
}
