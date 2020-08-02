package ir.ac.sbu.Semantics.ProgramStructure.Descriptors;

import org.objectweb.asm.Type;

public class GlobalVarDSCP extends GlobalDSCP{

    public GlobalVarDSCP(Type type, boolean isValid, boolean isConst) {
        super(type, isValid);
        this.isConst = isConst;
    }
}
