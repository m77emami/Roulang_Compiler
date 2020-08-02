package ir.ac.sbu.Semantics.ast.block;

import ir.ac.sbu.Semantics.ast.AST;
import ir.ac.sbu.Semantics.ast.declaration.Declaration;
import ir.ac.sbu.Semantics.ast.expression.FunctionCall;
import ir.ac.sbu.Semantics.ast.expression.binary.Sum;
import ir.ac.sbu.Semantics.ast.expression.constant.IntegerConst;
import ir.ac.sbu.Semantics.ProgramStructure.ScopeHandler;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;

import java.util.ArrayList;
import java.util.List;

import static org.objectweb.asm.Opcodes.ACC_STATIC;

public class GlobalBlock implements AST {

    private List<AST> declarationList;
    private static GlobalBlock instance = new GlobalBlock();


    public static GlobalBlock getInstance(){
        return instance;
    }

    private GlobalBlock() {
        this.declarationList = new ArrayList<>();
    }

    public void addDeclaration(Declaration declaration){
        declarationList.add(declaration);
    }

    @Override
    public void codegen(ClassWriter cw, MethodVisitor mv) {
        new FunctionCall("start",new ArrayList<>()).codegen(cw, mv);
        mv = cw.visitMethod(ACC_STATIC, "<clinit>",
                "()V", null, null);
        mv.visitCode();
        for (AST dec :
                declarationList) {
            dec.codegen(cw, mv);
        }
        mv.visitMaxs(1, 1);
        mv.visitEnd();
    }

    public List<AST> getDeclarationList() {
        return declarationList;
    }
}
