package ir.ac.sbu.Semantics.ast.declaration.variable;

import ir.ac.sbu.Semantics.ast.Operation;
import ir.ac.sbu.Semantics.ast.declaration.Declaration;
import ir.ac.sbu.Semantics.ast.statement.loop.InitExp;
import org.objectweb.asm.Type;

public abstract class VarDCL implements Operation, InitExp, Declaration {
    protected String name;
    protected Type type = null;
    protected boolean global = true;
}
