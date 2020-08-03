package ir.ac.sbu.Semantics.ast.declaration.variable;

import ir.ac.sbu.Semantics.ast.expression.Expression;
import ir.ac.sbu.Semantics.ast.expression.constant.ConstantExpression;
import ir.ac.sbu.Semantics.ast.expression.unary.TypeCast;
import ir.ac.sbu.Semantics.ast.expression.variable.SimpleVariable;
import ir.ac.sbu.Semantics.ast.expression.variable.Variable;
import ir.ac.sbu.Semantics.ast.statement.assignment.Assign;
import ir.ac.sbu.Semantics.ProgramStructure.Descriptors.DSCP;
import ir.ac.sbu.Semantics.ProgramStructure.Descriptors.GlobalVarDSCP;
import ir.ac.sbu.Semantics.ProgramStructure.Descriptors.LocalDSCP;
import ir.ac.sbu.Semantics.ProgramStructure.Descriptors.LocalVarDSCP;
import ir.ac.sbu.Semantics.ProgramStructure.ScopeHandler;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

import static org.objectweb.asm.Opcodes.*;

public class SimpleVarDcl extends VarDCL {


    private boolean constant;
    private Expression exp;
    private String stringType;

    public void setExp(Expression exp) {
        this.exp = exp;
        ScopeHandler.getInstance().getDescriptor(name).setValid(true);
    }


    public SimpleVarDcl(String varName, Type type, boolean constant, boolean global) {
        name = varName;
        this.type = type;
        this.constant = constant;
        this.global = global;
    }

    public SimpleVarDcl(String varName, String type, boolean constant, boolean global, Expression exp) {
        name = varName;
        stringType = type;
        if (!type.equals("auto"))
            this.type = ScopeHandler.getTypeFromName(type);
        else
            this.type = null;
        this.constant = constant;
        this.global = global;
        this.exp = exp;
        if (this.type == null)
            if (exp == null)
                throw new RuntimeException("the auto variable must be have expression");
            else
                phonyExpExe();
    }

    @Override
    public void codegen(ClassWriter cw, MethodVisitor mv) {
        if (global) {
            Expression value = null;
            int access = ACC_STATIC;
            access += constant ? ACC_FINAL : 0;
            cw.visitField(access, name, type.getDescriptor(),
                    null, value).visitEnd();
            if (exp != null) {
                executeGlobalExp(cw, mv);
            }
        } else if (exp != null) {
            exp.codegen(cw, mv);
            if (!exp.getType().equals(type)){
                System.out.println(exp.getType());
//                new TypeCast(exp, this.type).codegen(cw, mv);
                throw new RuntimeException("the type of variable and expression doesn't match" +
                        "   " + "the type of var " + type + "   " + "the type of exp " + exp.getType());
            }

            LocalVarDSCP dscp = (LocalVarDSCP) ScopeHandler.getInstance().getDescriptor(name);
            mv.visitVarInsn(type.getOpcode(ISTORE), dscp.getIndex());
        }
    }


    private void phonyExpExe() {
        TempMethodVisitor tempMV = new TempMethodVisitor();
        TempClassWriter tempCW = new TempClassWriter();
        exp.codegen(tempCW, tempMV);
        type = exp.getType();
    }

    private void executeGlobalExp(ClassWriter cw,MethodVisitor mv) {
        assign(new SimpleVariable(name, type), exp, mv, cw);
    }

    public void declare() {
        DSCP dscp;
        if (!global)
            dscp = new LocalVarDSCP(type, exp != null,
                    ScopeHandler.getInstance().getIndex(), constant);
        else
            dscp = new GlobalVarDSCP(type, exp != null, constant);

        ScopeHandler.getInstance().addVariable(name, dscp);
    }

    private void assign(Variable variable, Expression expression,
                        MethodVisitor mv, ClassWriter cw) {
        DSCP dscp = variable.getDSCP();
        expression.codegen(cw, mv);

        if (variable.getType() != expression.getType())
            throw new RuntimeException("you should cast expression!");
        if (dscp instanceof LocalDSCP) {
            int index = ((LocalDSCP) dscp).getIndex();
            mv.visitVarInsn(variable.getType().getOpcode(ISTORE), index);
        } else
            mv.visitFieldInsn(PUTSTATIC, "Test", variable.getName(), dscp.getType().toString());
        dscp.setValid(true);
    }
}


class TempMethodVisitor extends MethodVisitor {
    public TempMethodVisitor() {
        super(327680);
    }
}

class TempClassWriter extends ClassWriter {
    public TempClassWriter() {
        super(327680);
    }
}
