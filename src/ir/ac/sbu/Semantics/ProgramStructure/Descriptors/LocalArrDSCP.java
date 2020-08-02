package ir.ac.sbu.Semantics.ProgramStructure.Descriptors;

import ir.ac.sbu.Semantics.ast.expression.Expression;
import org.objectweb.asm.Type;
import java.util.List;

public class LocalArrDSCP extends LocalDSCP {

    protected List<Expression> listOfLengths;
    protected int dimension;

    public LocalArrDSCP(Type type, boolean isValid, int index, List<Expression> listOfLengths, int dimension){
        super(type, isValid, index);
        this.listOfLengths = listOfLengths;
        this.dimension = dimension;
    }

    public LocalArrDSCP(Type type, boolean isValid, int index, int dimension){
        super(type, isValid, index);
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
