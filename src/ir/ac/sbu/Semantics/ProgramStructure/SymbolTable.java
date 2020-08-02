package ir.ac.sbu.Semantics.ProgramStructure;

import ir.ac.sbu.Semantics.ProgramStructure.Descriptors.DSCP;
import java.util.HashMap;

public class SymbolTable extends HashMap<String, DSCP> {
    private int index = 0;
    private ScopeType scopeType;

    public void addIndex(int add){
        index++;
    }

    public int getIndex (){
        return index;
    }

    public ScopeType getScopeType() {
        return scopeType;
    }

    public void setScopeType(ScopeType scopeType) {
        this.scopeType = scopeType;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}
