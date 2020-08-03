package ir.ac.sbu.Semantics.ast.expression;

import ir.ac.sbu.Semantics.ast.AST;
import org.objectweb.asm.Type;

abstract public class Expression implements AST {
    protected Type type;

    protected Expression(Type type){
        this.type = type;
    }

    protected Expression(){}

    public Type getType(){
        if (type == null)
            throw new RuntimeException("Type is undefined.");
        return type;
    }
}
