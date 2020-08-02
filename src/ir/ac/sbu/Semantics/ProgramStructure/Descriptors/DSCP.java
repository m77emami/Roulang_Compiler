package ir.ac.sbu.Semantics.ProgramStructure.Descriptors;

import org.objectweb.asm.Type;

public abstract class DSCP {
    protected Type type;
    protected boolean isValid;
    boolean isConst;

    public boolean isConst() {
        return isConst;
    }

    public void setConst(boolean aConst) {
        isConst = aConst;
    }

    public DSCP(Type type, boolean isValid) {
        this.type = type;
        this.isValid = isValid;
    }

    public boolean isValid(){
        return isValid;
    }

    public void setValid(boolean valid) {
        isValid = valid;
    }

    public Type getType() {
        return type;
    }
}
