package ir.ac.sbu.Semantics.ProgramStructure.Descriptors;

import ir.ac.sbu.Semantics.ast.expression.Expression;
import org.objectweb.asm.Type;

import java.util.List;

public class GlobalArrDSCP extends GlobalDSCP{

    protected List<Expression> listOfLengths;
    protected int dimension;

    public GlobalArrDSCP(Type type, boolean isValid, List<Expression> listOfLengths, int dimension) {
        super(type, isValid);
        this.listOfLengths = listOfLengths;
        this.dimension = dimension;
    }

    public GlobalArrDSCP(Type type, boolean isValid, int dimension) {
        super(type, isValid);
        this.dimension = dimension;
    }

    public List<Expression> getListOfLengths() {
        return listOfLengths;
    }

    public int getDimension() {
        return dimension;
    }

    public void setListOfLengths(List<Expression> listOfLengths) {
        this.listOfLengths = listOfLengths;
    }
}
