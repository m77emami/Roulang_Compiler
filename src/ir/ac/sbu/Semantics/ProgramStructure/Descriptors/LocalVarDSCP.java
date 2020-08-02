package ir.ac.sbu.Semantics.ProgramStructure.Descriptors;

import org.objectweb.asm.Type;

public class LocalVarDSCP extends LocalDSCP  {

    boolean isConst;

    public LocalVarDSCP(Type type, boolean isValid, int index, boolean isConst) {
        super(type, isValid, index);
        this.isConst = isConst;
    }
}
