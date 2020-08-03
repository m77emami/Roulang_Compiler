package ir.ac.sbu.Semantics;

import ir.ac.sbu.Semantics.ast.AST;
import ir.ac.sbu.Semantics.ast.expression.Expression;
import ir.ac.sbu.Semantics.ast.expression.binary.*;
import ir.ac.sbu.Semantics.ast.expression.constant.IntegerConst;
import ir.ac.sbu.Semantics.ast.expression.unary.Negative;
import ir.ac.sbu.Parser.Lexical;
import ir.ac.sbu.Semantics.ast.Operation;
import ir.ac.sbu.Semantics.ast.block.Block;
import ir.ac.sbu.Semantics.ast.block.GlobalBlock;
import ir.ac.sbu.Semantics.ast.declaration.Declaration;
import ir.ac.sbu.Semantics.ast.declaration.function.FunctionDeclaration;
import ir.ac.sbu.Semantics.ast.declaration.variable.ArrDcl;
import ir.ac.sbu.Semantics.ast.declaration.variable.SimpleVarDcl;
import ir.ac.sbu.Semantics.ast.expression.*;
import ir.ac.sbu.Semantics.ast.expression.binary.Conditional.*;
import ir.ac.sbu.Semantics.ast.expression.constant.*;
import ir.ac.sbu.Semantics.ast.expression.unary.*;
import ir.ac.sbu.Semantics.ast.expression.variable.ArrayVariable;
import ir.ac.sbu.Semantics.ast.expression.variable.RecordVariable;
import ir.ac.sbu.Semantics.ast.expression.variable.SimpleVariable;
import ir.ac.sbu.Semantics.ast.expression.variable.Variable;
import ir.ac.sbu.Semantics.ast.statement.Break;
import ir.ac.sbu.Semantics.ast.statement.Condition.Case;
import ir.ac.sbu.Semantics.ast.statement.Condition.If;
import ir.ac.sbu.Semantics.ast.statement.Condition.Switch;
import ir.ac.sbu.Semantics.ast.statement.Continue;
import ir.ac.sbu.Semantics.ast.statement.FuncReturn;
import ir.ac.sbu.Semantics.ast.statement.Println;
import ir.ac.sbu.Semantics.ast.statement.assignment.*;
import ir.ac.sbu.Semantics.ast.statement.loop.For;
import ir.ac.sbu.Semantics.ast.statement.loop.InitExp;
import ir.ac.sbu.Semantics.ast.statement.loop.Repeat;
import ir.ac.sbu.Semantics.ast.statement.loop.StepExp;
import ir.ac.sbu.Semantics.ProgramStructure.Descriptors.*;
import ir.ac.sbu.Semantics.ProgramStructure.SymbolTable;
import ir.ac.sbu.Semantics.ProgramStructure.ScopeHandler;
import ir.ac.sbu.Parser.Lexical;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

import javax.print.attribute.standard.NumberUp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CodeGenerator implements ir.ac.sbu.Parser.CodeGenerator {
    private Lexical lexical;
    private SemanticStack semanticStack;

    public CodeGenerator(Lexical lexical) {
        this.lexical = lexical;
        semanticStack = new SemanticStack();
        semanticStack.push(GlobalBlock.getInstance());
    }

    public AST getResult() {
        return (AST) semanticStack.getFirst();
    }

    public void doSemantic(String sem) {
        switch (sem) {
            /* --------------------- global --------------------- */
            case "push": {
                semanticStack.push(lexical.currentToken().getValue());
                break;
            }
            case "pop": {
                semanticStack.pop();
                break;
            }
            case "createFlag": {
                Byte flag = 0;
                semanticStack.push(flag);
                break;
            }
            case "createBlock": {
                semanticStack.push(new Block(new ArrayList<>()));
                break;
            }

            /* --------------------- declarations --------------------- */
            case "mkFuncDCL": {
                Type type = ScopeHandler.getTypeFromName((String) semanticStack.pop());
                FunctionDeclaration functionDcl = new FunctionDeclaration(type,
                        (String) lexical.currentToken().getValue(), null, new ArrayList<>());
                semanticStack.push(functionDcl);
                ScopeHandler.getInstance().setLastFunction(functionDcl);
                break;
            }
            case "addParameter": {
                String name = ((NOP) semanticStack.pop()).name;
                LocalDSCP dscp = (LocalDSCP) ScopeHandler.getInstance().getDescriptor(name);
                FunctionDeclaration function = (FunctionDeclaration) semanticStack.pop();
                function.addParameter(name,dscp);
                semanticStack.push(function);
                break;
            }
            case "completeFuncDCL": {
                Block block = (Block) semanticStack.pop();
                FunctionDeclaration function = (FunctionDeclaration) semanticStack.pop();
                function.setBlock(block);
                semanticStack.push(function);
                ScopeHandler.getInstance().setLastFunction(null);
                break;
            }
            case "addFuncDCL": {
                FunctionDeclaration function = (FunctionDeclaration) semanticStack.pop();
                function.declare();
                semanticStack.push(function);
                break;
            }
            case "mkSimpleVarDCL": {
                String name = (String) lexical.currentToken().getValue();
                Type type = ScopeHandler.getTypeFromName((String) semanticStack.pop());
                if (semanticStack.peek() instanceof GlobalBlock)
                    ScopeHandler.getInstance().addVariable(name,new GlobalVarDSCP(type,false,false));
                else
                    ScopeHandler.getInstance().addVariable(name,new LocalVarDSCP(type,false,
                            ScopeHandler.getInstance().getIndex(),false));
                semanticStack.push(new NOP(name));
                break;
            }
            case "constTrue": {
                String varName = ((NOP) semanticStack.pop()).name;
                DSCP dscp = ScopeHandler.getInstance().getDescriptor(varName);
                dscp.setConst(true);
                semanticStack.push(new NOP(varName));
                break;
            }
            case "pushBlock": {  //begin
                semanticStack.push(new Block(new ArrayList<>()));
                break;
            }
            case "addBlock": { //fill function's block
                Operation operation = (Operation) semanticStack.pop();
                Block block = (Block) semanticStack.pop();
                block.addOperation(operation);
                semanticStack.push(block);
                break;
            }
            case "addGlobalBlock": {
                Declaration declaration = (Declaration) semanticStack.pop();
                if (declaration instanceof FunctionDeclaration)
                    addFuncToGlobalBlock((FunctionDeclaration) declaration);
                else
                    GlobalBlock.getInstance().addDeclaration(declaration);
                break;
            }
            case "setValueToVar": {
                Expression exp = (Expression) semanticStack.pop();
                String name = ((NOP) semanticStack.pop()).name;
                DSCP dscp = ScopeHandler.getInstance().getDescriptor(name);
                SimpleVarDcl varDcl = new SimpleVarDcl(name,dscp.getType(),dscp.isConst(),dscp instanceof GlobalDSCP);
                varDcl.setExp(exp);
                semanticStack.push(varDcl);
                break;
            }
            case "mkSimpleAutoVarDCL": {
                Expression exp = (Expression) semanticStack.pop();
                String varName = (String) semanticStack.pop();
                SimpleVarDcl varDcl;
                if (semanticStack.peek() instanceof GlobalBlock)
                    varDcl = new SimpleVarDcl(varName, "auto", false, true, exp);
                else
                    varDcl = new SimpleVarDcl(varName, "auto", false, false, exp);
                varDcl.declare();
                semanticStack.push(varDcl);
                break;
            }
            case "dimpp": {
                Byte flag = (Byte) semanticStack.pop();
                flag++;
                semanticStack.push(flag);
                break;
            }
            case "arrDCL": {
                String name = (String) lexical.currentToken().getValue();
                Byte flag = (Byte) semanticStack.pop();
                Type type = ScopeHandler.getTypeFromName((String) semanticStack.pop());
                if (semanticStack.peek() instanceof GlobalBlock)
                    ArrDcl.declare(name, type, new ArrayList<>(), flag, true);
                else
                    ArrDcl.declare(name, type, new ArrayList<>(), flag, false);
                semanticStack.push(new NOP(name));
                break;
            }
            case "mkArrayVarDCL": {
                Byte flag = (Byte) semanticStack.pop();
                List<Expression> expressionList = new ArrayList<>();
                int i = flag;
                while (i > 0) {
                    expressionList.add((Expression) semanticStack.pop());
                    i--;
                }
                Type type = ScopeHandler.getTypeFromName((String) semanticStack.pop());
                String name = ((NOP) semanticStack.pop()).name;
                DSCP dscp = ScopeHandler.getInstance().getDescriptor(name);
                if(!dscp.getType().equals(type))
                    throw new RuntimeException("Types don't match");
                ArrDcl arrDcl;
                if (semanticStack.peek() instanceof GlobalBlock){
                    if(((GlobalArrDSCP)dscp).getDimension() != flag)
                        throw new RuntimeException("Number of dimensions doesn't match");
                    arrDcl = new ArrDcl(name, type, true, flag);
                    ((GlobalArrDSCP)dscp).setListOfLengths(expressionList);
                }
                else{
                    if(((LocalArrDSCP)dscp).getDimension() != flag)
                        throw new RuntimeException("Number of dimensions doesn't match");
                    arrDcl = new ArrDcl(name, type, false, flag);
                    ((LocalArrDSCP)dscp).setListOfLengths(expressionList);
                }
                semanticStack.push(arrDcl);
                break;
            }
            case "mkAutoArrVarDCL": {
                Byte flag = (Byte) semanticStack.pop();
                List<Expression> expressionList = new ArrayList<>();
                while (flag > 0) {
                    expressionList.add((Expression) semanticStack.pop());
                    flag--;
                }
                Type type = ScopeHandler.getTypeFromName((String) semanticStack.pop());
                String name = (String) semanticStack.pop();
                ArrDcl arrDcl;
                if (semanticStack.peek() instanceof GlobalBlock){
                    arrDcl = new ArrDcl(name, type, true, expressionList.size());
                    arrDcl.declare(name,type,expressionList,expressionList.size(),true);
                }
                else{
                    arrDcl = new ArrDcl(name, type, false, expressionList.size());
                    arrDcl.declare(name,type,expressionList,expressionList.size(),false);
                }
                arrDcl.setDimensions(expressionList);
                semanticStack.push(arrDcl);
                break;
            }
            /* --------------------- binary expressions --------------------- */
            /* ---------------------- Arithmetic ------------------------ */
            case "div": {
                Expression second = (Expression) semanticStack.pop();
                Expression first = (Expression) semanticStack.pop();
                semanticStack.push(new Divide(first, second));
                break;
            }
            case "minus": {
                Expression second = (Expression) semanticStack.pop();
                Expression first = (Expression) semanticStack.pop();
                semanticStack.push(new Subtract(first, second));
                break;
            }
            case "mult": {
                Expression second = (Expression) semanticStack.pop();
                Expression first = (Expression) semanticStack.pop();
                semanticStack.push(new Multiply(first, second));
                break;
            }
            case "rmn": {
                Expression second = (Expression) semanticStack.pop();
                Expression first = (Expression) semanticStack.pop();
                semanticStack.push(new Mod(first, second));
                break;
            }
            case "sum": {
                Expression second = (Expression) semanticStack.pop();
                Expression first = (Expression) semanticStack.pop();
                semanticStack.push(new Sum(first, second));
                break;
            }
            /* ---------------------- conditional ------------------------- */
            case "and": {
                Expression second = (Expression) semanticStack.pop();
                Expression first = (Expression) semanticStack.pop();
                semanticStack.push(new And(first, second));
                break;
            }
            case "andBit": {
                Expression second = (Expression) semanticStack.pop();
                Expression first = (Expression) semanticStack.pop();
                semanticStack.push(new BitwiseAnd(first, second));
                break;
            }
            case "biggerAndEqual": {
                Expression second = (Expression) semanticStack.pop();
                Expression first = (Expression) semanticStack.pop();
                semanticStack.push(new GE(first, second));
                break;
            }
            case "biggerThan": {
                Expression second = (Expression) semanticStack.pop();
                Expression first = (Expression) semanticStack.pop();
                semanticStack.push(new GT(first, second));
                break;
            }
            case "equal": {
                Expression second = (Expression) semanticStack.pop();
                Expression first = (Expression) semanticStack.pop();
                semanticStack.push(new EQ(first, second));
                break;
            }
            case "lessAndEqual": {
                Expression second = (Expression) semanticStack.pop();
                Expression first = (Expression) semanticStack.pop();
                semanticStack.push(new LE(first, second));
                break;
            }
            case "lessThan": {
                Expression second = (Expression) semanticStack.pop();
                Expression first = (Expression) semanticStack.pop();
                semanticStack.push(new LT(first, second));
                break;
            }
            case "notEqual": {
                Expression second = (Expression) semanticStack.pop();
                Expression first = (Expression) semanticStack.pop();
                semanticStack.push(new NE(first, second));
                break;
            }
            case "or": {
                Expression second = (Expression) semanticStack.pop();
                Expression first = (Expression) semanticStack.pop();
                semanticStack.push(new Or(first, second));
                break;
            }
            case "orBit": {
                Expression second = (Expression) semanticStack.pop();
                Expression first = (Expression) semanticStack.pop();
                semanticStack.push(new BitwiseOr(first, second));
                break;
            }
            case "xorBit": {
                Expression second = (Expression) semanticStack.pop();
                Expression first = (Expression) semanticStack.pop();
                semanticStack.push(new BitwiseXor(first, second));
                break;
            }
            /* -------------------------- Unary   ---------------------------- */
            case "bitwiseNot": {
                Expression exp = (Expression) semanticStack.pop();
                semanticStack.push(new BitwiseNot(exp));
                break;
            }
            case "cast": {
                Expression exp = (Expression) semanticStack.pop();
                Type newType = ScopeHandler.getTypeFromName((String) semanticStack.pop());
                semanticStack.push(new TypeCast(exp, newType));
                break;
            }
            case "negative": {
                Expression exp = (Expression) semanticStack.pop();
                semanticStack.push(new Negative(exp));
                break;
            }
            case "not": {
                Expression exp = (Expression) semanticStack.pop();
                semanticStack.push(new Not(exp));
                break;
            }
            case "postmm": {
                Variable var = (Variable) semanticStack.pop();
                if (var instanceof RecordVariable)
                    throw new RuntimeException("Undefined operand for record type");
                checkAssign(var);
                semanticStack.push(new PostfixMinusMinus(var));
                break;
            }
            case "postpp": {
                Variable var = (Variable) semanticStack.pop();
                if (var instanceof RecordVariable)
                    throw new RuntimeException("Undefined operand for record type");
                checkAssign(var);
                semanticStack.push(new PostfixPlusPlus(var));
                break;
            }
            case "premm": {
                Variable var = (Variable) semanticStack.pop();
                if (var instanceof RecordVariable)
                    throw new RuntimeException("Undefined operand for record type");
                checkAssign(var);
                semanticStack.push(new PrefixMinusMinus(var));
                break;
            }
            case "prepp": {
                Variable var = (Variable) semanticStack.pop();
                if (var instanceof RecordVariable)
                    throw new RuntimeException("Undefined operand for record type");
                checkAssign(var);
                semanticStack.push(new PrefixPlusPlus(var));
                break;
            }
            /* -------------------------- Const ---------------------------- */
            case "pushReal": {
                Object realNum = lexical.currentToken().getValue();
                if (realNum instanceof Float)
                    semanticStack.push(new FloatConst((Float) realNum));
                else
                    semanticStack.push(new DoubleConst((Double) realNum));
                break;
            }
            case "pushInt": {
                Object integerNum = lexical.currentToken().getValue();
                if (integerNum instanceof Integer)
                    semanticStack.push(new IntegerConst((Integer) integerNum));
                else
                    semanticStack.push(new LongConst((Long) integerNum));
                break;
            }
            case "pushBool": {
                String value = (String) lexical.currentToken().getValue();
                semanticStack.push(new BooleanConst(Boolean.parseBoolean(value)));
                break;
            }
            case "pushChar": {
                semanticStack.push(new CharConst((Character) lexical.currentToken().getValue()));
                break;
            }
            case "pushString": {
                semanticStack.push(new StringConst((String) lexical.currentToken().getValue()));
                break;
            }
            /* -------------------------- variable ---------------------------- */
            case "pushVar": {
                String name = (String) lexical.currentToken().getValue();
                if(ScopeHandler.getInstance().getFuncNames().contains(name)){
                    semanticStack.push(name);
                    break;
                }
                DSCP dscp = ScopeHandler.getInstance().getDescriptor(name);
                if (dscp instanceof GlobalVarDSCP || dscp instanceof LocalVarDSCP)
                    semanticStack.push(new SimpleVariable(name, dscp.getType()));
                else if (dscp instanceof GlobalArrDSCP || dscp instanceof LocalArrDSCP)
                    semanticStack.push(new ArrayVariable(name, new ArrayList<>(), dscp.getType()));
                break;
            }
            case "flagpp": {
                Expression exp = (Expression) semanticStack.pop();
                Byte flag = (Byte) semanticStack.pop();
                flag++;
                semanticStack.push(exp);
                semanticStack.push(flag);
                break;
            }
            case "pushArrayVar": {
                Byte flag = (Byte) semanticStack.pop();
                List<Expression> expressionList = new ArrayList<>();
                while (flag > 0) {
                    expressionList.add((Expression) semanticStack.pop());
                    flag--;
                }
                ArrayVariable var = (ArrayVariable) semanticStack.pop();
                semanticStack.push(new ArrayVariable(var.getName(), expressionList, var.getType()));
                break;
            }
            /* -------------------------- Assignment -------------------------- */
            case "assign": {
                Expression exp = (Expression) semanticStack.pop();
                Variable var = (Variable) semanticStack.pop();
                checkAssign(var);
                semanticStack.push(new Assign(exp, var));
                break;
            }
            case "sumAssign": {
                Expression exp = (Expression) semanticStack.pop();
                Variable var = (Variable) semanticStack.pop();
                checkAssign(var);
                semanticStack.push(new SumAssign(exp, var));
                break;
            }
            case "minAssign": {
                Expression exp = (Expression) semanticStack.pop();
                Variable var = (Variable) semanticStack.pop();
                checkAssign(var);
                semanticStack.push(new MinAssign(exp, var));
                break;
            }
            case "divAssign": {
                Expression exp = (Expression) semanticStack.pop();
                Variable var = (Variable) semanticStack.pop();
                checkAssign(var);
                semanticStack.push(new DivAssign(exp, var));
                break;
            }
            case "mulAssign": {
                Expression exp = (Expression) semanticStack.pop();
                Variable var = (Variable) semanticStack.pop();
                checkAssign(var);
                semanticStack.push(new MulAssign(exp, var));
                break;
            }
            case "rmnAssign": {
                Expression exp = (Expression) semanticStack.pop();
                Variable var = (Variable) semanticStack.pop();
                checkAssign(var);
                semanticStack.push(new RmnAssign(exp, var));
                break;
            }
            case "check2types": {
                Type type = ScopeHandler.getTypeFromName((String) semanticStack.pop());
                Variable variable = (Variable) semanticStack.pop();
                if (!(variable instanceof ArrayVariable))
                    throw new RuntimeException("You can't new a simple variable");
                if (variable.getType() != null && !type.equals(variable.getType()))
                    throw new RuntimeException("types don't match");
                semanticStack.push(variable);
                break;
            }
            case "setCheckDim": {
                Byte flag = (Byte) semanticStack.pop();
                List<Expression> expressionList = new ArrayList<>();
                int i = flag;
                while (i > 0) {
                    expressionList.add((Expression) semanticStack.pop());
                    i--;
                }
                ArrayVariable var = (ArrayVariable) semanticStack.pop();
                if (var.getDSCP() instanceof GlobalArrDSCP)
                    if (((GlobalArrDSCP) var.getDSCP()).getDimension() != flag)
                        throw new RuntimeException("Number of dimensions doesn't match");
                if (var.getDSCP() instanceof LocalArrDSCP)
                    if (((LocalArrDSCP) var.getDSCP()).getDimension() != flag)
                        throw new RuntimeException("Number of dimensions doesn't match");
                var.setDimensions(expressionList);
                semanticStack.push(new NOP());
                break;
            }
            /* ---------------------- functions ---------------------------- */
            case "voidReturn": {
                Block block = (Block) semanticStack.pop();
                FunctionDeclaration functionDcl = ScopeHandler.getInstance().getLastFunction();
                FuncReturn funcReturn = new FuncReturn(null, functionDcl);
                functionDcl.addReturn(funcReturn);
                block.addOperation(funcReturn);
                semanticStack.push(block);
                break;
            }
            case "return": {
                Expression exp = (Expression) semanticStack.pop();
                Block block = (Block) semanticStack.pop();
                FunctionDeclaration functionDcl = ScopeHandler.getInstance().getLastFunction();
                FuncReturn funcReturn = new FuncReturn(exp, functionDcl);
                functionDcl.addReturn(funcReturn);
                block.addOperation(funcReturn);
                semanticStack.push(block);
                break;
            }
            case "break": {
                semanticStack.push(new Break());
                break;
            }
            case "continue": {
                semanticStack.push(new Continue());
                break;
            }
            case "funcCall": {
                String  name = (String) semanticStack.pop();
                semanticStack.push(new FunctionCall(name, new ArrayList<>()));
                break;
            }
            case "addParam": {
                Expression exp = (Expression) semanticStack.pop();
                FunctionCall funcCall = (FunctionCall) semanticStack.pop();
                funcCall.addParam(exp);
                semanticStack.push(funcCall);
                break;
            }
            /* --------------------- loops --------------------- */
            /* --------------------- for --------------------- */
            case "changeTop": {
                Expression exp = (Expression) semanticStack.pop();
                Byte flag = (Byte) semanticStack.pop();
                semanticStack.push(exp);
                semanticStack.push(flag);
                break;
            }
            case "trueInitFlag": {
                InitExp initExp = (InitExp) semanticStack.pop();
                semanticStack.pop();
                Byte flag = 1;
                semanticStack.push(initExp);
                semanticStack.push(flag);
                break;
            }
            case "trueStepFlag": {
                StepExp stepExp = (StepExp) ((AST) semanticStack.pop());
                Byte flag = (Byte) semanticStack.pop();
                if (flag == 0)
                    flag = 2;
                else
                    flag = 3;
                semanticStack.push(stepExp);
                semanticStack.push(flag);
                break;
            }
            case "for": {
                Block block = (Block) semanticStack.pop();
                Byte flag = (Byte) semanticStack.pop();
                InitExp initExp = null;
                Expression exp = null;
                StepExp stepExp = null;
                if (flag == 0) {
                    exp = (Expression) semanticStack.pop();
                } else if (flag == 1) {
                    exp = (Expression) semanticStack.pop();
                    initExp = (InitExp) semanticStack.pop();
                } else if (flag == 2) {
                    stepExp = (StepExp) semanticStack.pop();
                    exp = (Expression) semanticStack.pop();
                } else {
                    stepExp = (StepExp) semanticStack.pop();
                    exp = (Expression) semanticStack.pop();
                    initExp = (InitExp) semanticStack.pop();
                }
                semanticStack.push(new For(block, initExp, exp, stepExp));
                break;
            }
            /* --------------------- repeat --------------------- */
            case "repeat": {
                Expression exp = (Expression) semanticStack.pop();
                Block block = (Block) semanticStack.pop();
                semanticStack.push(new Repeat(block, exp));
                break;
            }
            /* --------------------- conditions --------------------- */
            /* --------------------- if --------------------- */
            case "if": {
                Block block = (Block) semanticStack.pop();
                Expression exp = (Expression) semanticStack.pop();
                semanticStack.push(new If(exp, block, null));
                break;
            }
            case "else": {
                Block block = (Block) semanticStack.pop();
                If ifSt = (If) semanticStack.pop();
                ifSt.setElseBlock(block);
                semanticStack.push(ifSt);
                break;
            }
            /* --------------------- switch --------------------- */
            case "switch": {
                Expression exp = (Expression) semanticStack.pop();
                semanticStack.push(new Switch(exp, new ArrayList<>(), null));
                break;
            }
            case "addCase": {
                Block block = (Block) semanticStack.pop();
                IntegerConst intConst = (IntegerConst) semanticStack.pop();
                Switch switchSt = (Switch) semanticStack.pop();
                Case caseSt = new Case(intConst, block);
                switchSt.addCase(caseSt);
                semanticStack.push(switchSt);
                break;
            }
            case "addDefault": {
                Block defaultBlock = (Block) semanticStack.pop();
                Switch switchSt = (Switch) semanticStack.pop();
                switchSt.setDefaultBlock(defaultBlock);
                semanticStack.push(switchSt);
                break;
            }
            /* --------------------- special method calls --------------------- */
            case "print": {
                Expression expression = (Expression) semanticStack.pop();
                semanticStack.push(new Println(expression));
                break;
            }
            case "printLine": {
                semanticStack.push(new Println(null));
                break;
            }
            case "input": {
                String type = (String) lexical.currentToken().getValue();
                semanticStack.push(new Input(ScopeHandler.getTypeFromName(type)));
                break;
            }
            case "inputLine": {
                semanticStack.push(new Input(null));
                break;
            }
            case "len": {
                Expression expression = (Expression) semanticStack.pop();
                semanticStack.push(new Len(expression));
                break;
            }
            case "sizeof": {
                String baseType = (String) semanticStack.pop();
                semanticStack.push(new SizeOf(baseType));
                break;
            }
            default:
                throw new RuntimeException("Illegal semantic function: " + sem);

        }
    }


    private void addFuncToGlobalBlock(FunctionDeclaration function) {
        if (GlobalBlock.getInstance().getDeclarationList().contains(function)) {
            int index = GlobalBlock.getInstance().getDeclarationList().indexOf(function);
            FunctionDeclaration lastFunc = (FunctionDeclaration) GlobalBlock.getInstance().getDeclarationList().get(index);
            if (lastFunc.getBlock() == null && function.getBlock() != null) {
                GlobalBlock.getInstance().getDeclarationList().remove(lastFunc);
                GlobalBlock.getInstance().addDeclaration(function);
            } else if (lastFunc.getBlock() != null && lastFunc.getBlock() == null) {
            } else
                throw new RuntimeException("the function is duplicate!!!");
        } else {
            GlobalBlock.getInstance().addDeclaration(function);
        }

    }

    private void checkAssign(Variable variable) {
        if (variable instanceof ArrayVariable) {
            ArrayVariable var = (ArrayVariable) variable;
            int numberOfExp = var.getDimensions().size();
            DSCP dscp = ScopeHandler.getInstance().getDescriptor(var.getName());
            if (dscp instanceof GlobalArrDSCP) {
                if (((GlobalArrDSCP) dscp).getDimension() != numberOfExp)
                    throw new RuntimeException("you can't assign an expression to array");
            }
            if (dscp instanceof LocalArrDSCP) {
                if (((LocalArrDSCP) dscp).getDimension() != numberOfExp)
                    throw new RuntimeException("you can't assign an expression to array");
            }
        }
    }
}

class NOP implements Operation {

    String name;

    public NOP(String name) {
        this.name = name;
    }
    public NOP() {
    }

    @Override
    public void codegen(ClassWriter cw, MethodVisitor mv) {

    }
}
