package ir.ac.sbu.Semantics.ast.statement.assignment;

import ir.ac.sbu.Semantics.ast.expression.Expression;
import ir.ac.sbu.Semantics.ast.expression.variable.Variable;
import ir.ac.sbu.Semantics.ProgramStructure.Descriptors.DSCP;
import ir.ac.sbu.Semantics.ProgramStructure.Descriptors.LocalDSCP;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;

import static org.objectweb.asm.Opcodes.*;

public class RmnAssign extends Assignment {

    public RmnAssign(Expression expression, Variable variable) {
        super(expression, variable);
    }

    @Override
    public void codegen(ClassWriter cw, MethodVisitor mv) {
        checkConst();

        DSCP dscp = variable.getDSCP();
        variable.codegen(cw, mv);
        expression.codegen(cw, mv);

        if (variable.getType() != expression.getType())
            throw new RuntimeException("you should cast expression!");

        mv.visitInsn(variable.getType().getOpcode(IREM));

        if(dscp instanceof LocalDSCP) {
            int index = ((LocalDSCP) dscp).getIndex();
            mv.visitVarInsn(variable.getType().getOpcode(ISTORE), index);
        }
        else
            mv.visitFieldInsn(PUTSTATIC, "Main", variable.getName(), dscp.getType().toString());

    }
}
