package ir.ac.sbu.Semantics.ast.expression.unary;

import ir.ac.sbu.Semantics.ast.expression.Expression;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;
import org.objectweb.asm.Opcodes;

public class TypeCast extends UnaryExpression {

    private Type cast;

    public TypeCast(Expression operand, Type cast) {
        super(operand);
        this.cast = cast;
    }

    @Override
    public void codegen(ClassWriter cw, MethodVisitor mv) {
        getOperand().codegen(cw, mv);

        if(type == cast)
            return;

        if(cast != Type.INT_TYPE && cast != Type.LONG_TYPE && cast != Type.DOUBLE_TYPE && cast != Type.FLOAT_TYPE)
            throw new RuntimeException("Not able to cast to " + cast.getClassName());

        mv.visitInsn(castingOpCode(type, cast));
        type = cast;
    }

    private int castingOpCode(Type type, Type cast) {
        int opcode;
        if(type == Type.INT_TYPE){
            if(cast == Type.LONG_TYPE)
                opcode = Opcodes.I2L;
            else if(cast == Type.FLOAT_TYPE)
                opcode = Opcodes.I2F;
            else
                opcode = Opcodes.I2D;
        }
        else if(type == Type.LONG_TYPE){
            if(cast == Type.INT_TYPE)
                opcode = Opcodes.L2I;
            else if(cast == Type.DOUBLE_TYPE)
                opcode = Opcodes.L2D;
            else
                opcode = Opcodes.L2F;
        }
        else if(type == Type.DOUBLE_TYPE){
            if(cast == Type.INT_TYPE)
                opcode = Opcodes.D2I;
            else if(cast == Type.LONG_TYPE)
                opcode = Opcodes.D2L;
            else
                opcode = Opcodes.D2F;
        }
        else{
            if(type == Type.LONG_TYPE)
                opcode = Opcodes.F2L;
            else if (cast == Type.INT_TYPE)
                opcode = Opcodes.F2I;
            else
                opcode = Opcodes.F2D;
        }
        return opcode;
    }
}
