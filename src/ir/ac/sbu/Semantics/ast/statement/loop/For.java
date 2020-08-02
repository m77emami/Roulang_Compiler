package ir.ac.sbu.Semantics.ast.statement.loop;

import ir.ac.sbu.Semantics.ast.block.Block;
import ir.ac.sbu.Semantics.ast.expression.Expression;
import ir.ac.sbu.Semantics.ast.expression.binary.Conditional.NE;
import ir.ac.sbu.Semantics.ast.expression.constant.IntegerConst;
import ir.ac.sbu.Semantics.ast.expression.unary.PostfixMinusMinus;
import ir.ac.sbu.Semantics.ast.expression.unary.PostfixPlusPlus;
import ir.ac.sbu.Semantics.ast.expression.unary.PrefixMinusMinus;
import ir.ac.sbu.Semantics.ast.expression.unary.PrefixPlusPlus;
import ir.ac.sbu.Semantics.ProgramStructure.ScopeType;
import ir.ac.sbu.Semantics.ProgramStructure.ScopeHandler;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

import static org.objectweb.asm.Opcodes.*;

public class For extends Loop {

    private InitExp init;
    private Expression expression;
    private StepExp step;
    private Label expLabel = new Label();
    private Label stepLabel = new Label();
    private Label blockLabel = new Label();


    public For(Block block, InitExp init, Expression expression, StepExp step) {
        super(block);
        this.init = init;
        this.expression = expression;
        this.step = step;
    }

    @Override
    public void codegen(ClassWriter cw, MethodVisitor mv) {
        ScopeHandler.getInstance().addScope(ScopeType.LOOP);
        ScopeHandler.getInstance().setInnerLoop(this);
        // ST init
        if (init != null) {
            init.codegen(cw, mv);
            if (init instanceof PostfixPlusPlus || init instanceof PrefixPlusPlus
                    || init instanceof PostfixMinusMinus || init instanceof PrefixMinusMinus)
                mv.visitInsn(POP);
        }
        // Boolean Expression
        mv.visitLabel(expLabel);

        // jz, BE, end
        // jnz, BE, blockLabel
        NE notEqual = new NE(expression, new IntegerConst(0));
        notEqual.codegen(cw, mv);
        mv.visitJumpInsn(IFEQ, end);
        mv.visitJumpInsn(GOTO, blockLabel);

        // ST step
        mv.visitLabel(stepLabel);
        mv.visitLabel(startLoop);
        if (step != null) {
            step.codegen(cw, mv);
            if (step instanceof PostfixMinusMinus || step instanceof PrefixMinusMinus
                    || step instanceof PrefixPlusPlus || step instanceof PostfixPlusPlus)
                mv.visitInsn(POP);
        }

        mv.visitJumpInsn(GOTO, expLabel);

        // ST body
        mv.visitLabel(blockLabel);
        block.codegen(cw, mv);
        mv.visitJumpInsn(GOTO, stepLabel);

        mv.visitLabel(end);

        ScopeHandler.getInstance().popScope();
        ScopeHandler.getInstance().setInnerLoop(null);
    }
}
