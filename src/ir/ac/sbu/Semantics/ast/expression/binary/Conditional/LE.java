package ir.ac.sbu.Semantics.ast.expression.binary.Conditional;

import ir.ac.sbu.Semantics.ast.expression.Expression;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import static org.objectweb.asm.Opcodes.*;

public class LE extends ConditionalExpression {

    public LE(Expression firstOperand, Expression secondOperand) {
        super(firstOperand, secondOperand);
    }

    @Override
    public void codegen(ClassWriter cw, MethodVisitor mv) {
        cmp(IFGT, IF_ICMPGT, mv, cw);
    }
}
