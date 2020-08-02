package ir.ac.sbu.Semantics.ProgramStructure.Descriptors;

import org.objectweb.asm.Type;

public abstract class LocalDSCP extends DSCP{

    int index;

    public LocalDSCP(Type type, boolean isValid, int index) {
        super(type, isValid);
        this.index = index;
    }

    public int getIndex() {
        return index;
    }
}
