import java.util.*;
import syntaxtree.*;
import visitor.*;

/**
 * TranslatorVisitor.java
 *
 * Description: Traverses the AST of the program and prints the Vapor code to the terminal (or output file if provided).
 *
 * @author Diya Lakhani
 * @version 1.0
 * @since 2025-09-24
 */

public class TranslatorVisitor extends GJDepthFirst<String, MethodClass> {
    private SymbolTable symTable;
    private VMT vmt;
    private int regCount;
    private String spaceCount;

    private final String THIS = "this";
    private final String REGISTER = "t.";

    /**
     * Constructor
     * @param symTable Symbol Table for program.
     * @param vmt Virtual Method Table for each class in the program.
     */
    public TranslatorVisitor(SymbolTable symTable, VMT vmt) {
        this.symTable = symTable;
        this.vmt = vmt;
        regCount = 0;
        spaceCount = "";
    }

    /**
     * Goal Visitor
     * @param n Goal node
     * @param mc
     * @return null
     */
    public String visit(Goal n, MethodClass mc) {
        n.f0.accept(this, null);
        if (n.f1.present()) {
            for (Node td : n.f1.nodes) {
                TypeDeclaration typeDec = (TypeDeclaration) td;
                typeDec.accept(this, null);
            }
        }
        return null;
    }


    /**
     * TypeDeclaration Visitor
     * @param n TypeDeclaration node
     * @param mc
     * @return null
     */
    public String visit(TypeDeclaration n, MethodClass mc) {
        n.f0.choice.accept(this, null);
        return null;
    }


    /**
     * ClassDeclaration Visitor
     * @param n ClassDeclaration node
     * @param mc
     * @return null
     */
    public String visit(ClassDeclaration n, MethodClass mc) {
        String className = n.f1.f0.tokenImage;
        ClassInfo c = symTable.getClass(className);
        MethodClass newmc = new MethodClass(c, null);
        if (n.f4.present()) {
            for (Node m : n.f4.nodes) {
                MethodDeclaration md = (MethodDeclaration) m;
                md.accept(this, newmc);
            }
        }
        return null;
    }


    /**
     * ClassExtendsDeclaration Visitor
     * @param n ClassExtendsDeclaration node
     * @param mc
     * @return null
     */
    public String visit(ClassExtendsDeclaration n, MethodClass mc) {
        String className = n.f1.f0.tokenImage;
        ClassInfo c = symTable.getClass(className);
        MethodClass newmc = new MethodClass(c, null);
        if (n.f6.present()) {
            for (Node m : n.f6.nodes) {
                MethodDeclaration md = (MethodDeclaration) m;
                md.accept(this, newmc);
            }
        }
        return null;
    }


    /**
     * MethodDeclaration Visitor
     * @param n MethodDeclaration node
     * @param mc
     * @return null
     */
    public String visit(MethodDeclaration n, MethodClass mc) {
        String methodName = n.f2.f0.tokenImage;
        ClassInfo c = mc.getClassInfo();
        MethodInfo m = c.getMethod(methodName);
        MethodClass newmc = new MethodClass(c, m);

        System.out.print("func " + c.getName() + "." + methodName + "(this");
        for (Map.Entry<String, String> ParamType : m.getAllParam().entrySet()) {
            System.out.print(" " + ParamType.getKey());
        }
        System.out.println(")");

        spaceCount = "    ";

        if (n.f8.present()) {
            for (Node s : n.f8.nodes) {
                Statement sta = (Statement) s;
                sta.accept(this, newmc);
            }
        }

        String retExp = n.f10.accept(this, mc);
        System.out.println(spaceCount + "ret " + retExp);


        // reset counters after method code produced
        spaceCount = "";
        regCount = 0;

        return null;
    }


    /**
     * MainClass Visitor
     * @param n MainClass node
     * @param mc
     * @return null
     */
    public String visit(MainClass n, MethodClass mc) {
        String mainClassName = n.f1.f0.tokenImage;
        ClassInfo mainClass = this.symTable.getClass(mainClassName);
        MethodInfo mainMethod = mainClass.getMethod("main");
        MethodClass mainMC = new MethodClass(mainClass, mainMethod);

        System.out.println("func Main()");
        spaceCount = "    ";

        if (n.f15.present()) {
            for (Node s : n.f15.nodes) {
                Statement st = (Statement) s;
                st.accept(this, mainMC);
            }
        }

        System.out.println(spaceCount + "ret");

        // reset counters after main method code produced
        spaceCount = "";
        regCount = 0;

        return null;
    }


    /**
     * Statement Visitor
     * @param n
     * @param mc
     * @return
     */
    public String visit(Statement n, MethodClass mc) {
        Node choice = n.f0.choice;

        if (choice instanceof AssignmentStatement) {
            AssignmentStatement as = (AssignmentStatement) choice;
            String exprRet = as.f2.accept(this, mc);
            String varName = as.f0.f0.tokenImage;
            System.out.println(spaceCount + varName + " = " + exprRet);
            return null;
        }

        if (choice instanceof PrintStatement) {
            PrintStatement ps = (PrintStatement) choice;
            String exprRet = ps.f2.accept(this, mc);
            System.out.println(spaceCount + "PrintIntS(" + exprRet + ")");
            return null;
        }

        // TODO: Complete other Statement cases
        
        return null;
    }


    /**
     * Expression Visitor
     * @param n
     * @param mc
     * @return
     */
    public String visit(Expression n, MethodClass mc) {
        Node choice = n.f0.choice;

        if (choice instanceof PlusExpression) {
            PlusExpression pe = (PlusExpression) choice;
            String prime1 = pe.f0.accept(this, mc);
            String prime2 = pe.f2.accept(this, mc);
            String outReg = REGISTER + regCount;

            System.out.println(spaceCount + outReg + " = AddS(" + prime1 + " " + prime2 + ")");

            regCount = regCount + 1;

            return outReg;
        }

        if (choice instanceof MinusExpression) {
            PlusExpression pe = (PlusExpression) choice;
            String prime1 = pe.f0.accept(this, mc);
            String prime2 = pe.f2.accept(this, mc);
            String outReg = REGISTER + regCount;

            System.out.println(spaceCount + outReg + " = MinusS(" + prime1 + " " + prime2 + ")");

            regCount = regCount + 1;

            return outReg;
        }
        
        if (choice instanceof PrimaryExpression) {
            PrimaryExpression pe = (PrimaryExpression) choice;
            return pe.accept(this, mc);
        }

        if (choice instanceof MessageSend) {
            MessageSend ms = (MessageSend) choice;
            String objName = ms.f0.accept(this, mc);
            String objType = findVarType(objName, mc);
            String methodName = ms.f2.f0.tokenImage;
            int methodOffset = vmt.getVClass(objType).getOffset(methodName);

            String funcReg = REGISTER + regCount;

            System.out.println(spaceCount + funcReg + " = [" + objName + "]");
            System.out.println(spaceCount + funcReg + " = [" + funcReg + "+" + methodOffset + "]");

            regCount = regCount + 1;

            String funcRet = REGISTER + regCount;

            System.out.print(spaceCount + funcRet + " = call " + funcReg + "(" + objName);
            
            // process function arguments
            if (ms.f4.node != null) {
                ExpressionList expl = (ExpressionList) ms.f4.node;
                Expression exp = expl.f0;
                String expRet = exp.accept(this, mc);
                System.out.print(" " + expRet);

                if (expl.f1.present()) {
                    for (Node expR : expl.f1.nodes) {
                        ExpressionRest expr = (ExpressionRest) expR;
                        Expression exprE = expr.f1;
                        String exprEReturn = exprE.accept(this, mc);
                        System.out.print(" " + exprEReturn);
                    }
                }
            }

            System.out.println(")");

            regCount = regCount + 1;

            return funcRet;
            
        }

        // TODO: Complete other Expression cases

        return null;
    }


    /**
     * PrimaryExpression Visitor
     * @param n
     * @param mc
     * @return
     */
    public String visit(PrimaryExpression n, MethodClass mc) {
        Node choice = n.f0.choice;

        if (choice instanceof IntegerLiteral) {
            IntegerLiteral i = (IntegerLiteral) choice;
            return i.f0.tokenImage;
        }

        if (choice instanceof TrueLiteral) {
            return "true";
        }

        if (choice instanceof FalseLiteral) {
            return "false";
        }

        if (choice instanceof Identifier) {
            Identifier i = (Identifier) choice;
            return i.f0.tokenImage;
        }

        if (choice instanceof AllocationExpression) {
            AllocationExpression ae = (AllocationExpression) choice;
            String className = ae.f1.f0.tokenImage;
            int numFields = symTable.getClass(className).getNumFields();
            int memoryAmt = 4 + (4 * numFields);
            int reg = regCount;

            System.out.println(spaceCount + "t." + reg + " = " + "HeapAllocZ(" + memoryAmt + ")");
            System.out.println(spaceCount + "[t." + reg + "] = :vmt_" + className);

            // update MethodClass MethodInfo register file
            mc.getMethodInfo().addType(reg, className);

            regCount = regCount + 1;

            return "t." + reg;
        }

        return null;
    }




    // HELPER FUNCTIONS


    /*
     * Helper function that returns type of variable.
     * Checks method parameters, local variables, class fields, and parent class fields.
     */
    private String findVarType(String varName, MethodClass mc) {

        if (varName.equals(THIS)) {
            return mc.getClassInfo().getName();
        }

        if (varName.length() >= 2 && varName.substring(0, 2).equals(REGISTER)) {
            int regIdx = Integer.parseInt(varName.substring(2));
            return mc.getMethodInfo().regType(regIdx);
        }

        MethodInfo currMethod = mc.getMethodInfo();

        if (currMethod.hasLocal(varName)) {
            return currMethod.getLocalType(varName);
        }

        if (currMethod.hasParam(varName)) {
            return currMethod.getParamType(varName);
        }

        ClassInfo currClass = mc.getClassInfo();
        while (currClass != null) {
            if (currClass.hasField(varName)) {
                return currClass.getFieldType(varName);
            }
            else {
                currClass = symTable.getClass(currClass.getParent());
            }
        }

        return null;
    }

    
}
