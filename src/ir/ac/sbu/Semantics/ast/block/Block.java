package ir.ac.sbu.Semantics.ast.block;

import ir.ac.sbu.Semantics.ast.AST;
import ir.ac.sbu.Semantics.ast.Operation;
import ir.ac.sbu.Semantics.ast.declaration.variable.ArrDcl;
import ir.ac.sbu.Semantics.ast.expression.FunctionCall;
import ir.ac.sbu.Semantics.ast.statement.FuncReturn;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;

import java.util.ArrayList;

public class Block implements AST {

    private ArrayList<Operation> operations;

    public Block(ArrayList<Operation> operations){
        this.operations = operations;
    }


    public void addOperation(Operation operation){
        operations.add(operation);
    }

    @Override
    public void codegen(ClassWriter cw, MethodVisitor mv) {
        if(operations == null)
            throw new RuntimeException("No expression found!");
        for (Operation op : operations) {
            op.codegen(cw, mv);
        }
    }
}
