package ir.ac.sbu.Semantics.ProgramStructure.Descriptors;

import org.objectweb.asm.Type;

public abstract class GlobalDSCP extends DSCP {

    public GlobalDSCP(Type type, boolean isValid) {
        super(type, isValid);
    }
}
