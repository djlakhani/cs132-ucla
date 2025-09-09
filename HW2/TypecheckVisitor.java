import java.util.*;

import syntaxtree.*;
import visitor.*;

public class TypecheckVisitor extends GJDepthFirst<String, MethodClass> {

    private final SymbolTable symTable;
    private final String BOOLEAN = "boolean";
    private final String INT = "int";
    private final String INTARR = "int[]";

    public TypecheckVisitor(SymbolTable symTable) {
        this.symTable = symTable;
    }

    // Goal
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

    // MainClass
    public String visit(MainClass n, MethodClass mc) {
        String mainClassName = n.f1.f0.tokenImage;
        ClassInfo mainClass = this.symTable.getClass(mainClassName);
        MethodInfo mainMethod = mainClass.getMethod("main");
        MethodClass mainMC = new MethodClass(mainClass, mainMethod);

        if (n.f15.present()) {
            for (Node s : n.f15.nodes) {
                Statement st = (Statement) s;
                st.accept(this, mainMC);
            }
        }

        return null;
    }

    // TypeDeclaration
    public String visit(TypeDeclaration n, MethodClass mc) {
        n.f0.choice.accept(this, null);
        return null;
    }

    // ClassDeclaration
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


    // ClassExtendsDeclaration
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


    // MethodDeclaration
    public String visit(MethodDeclaration n, MethodClass mc) {
        String methodName = n.f2.f0.tokenImage;
        ClassInfo c = mc.getClassInfo();
        MethodInfo m = c.getMethod(methodName);
        MethodClass newmc = new MethodClass(c, m);
        if (n.f8.present()) {
            for (Node s : n.f8.nodes) {
                Statement sta = (Statement) s;
                sta.accept(this, newmc);
            }
        }

        String methodRetType = m.getReturnType();
        String methodRet = n.f10.accept(this, newmc);
        
        if (!methodRetType.equals(methodRet)) {
            System.out.println("Method return type does not match!");
            throw new RuntimeException("Method return type does not match!");
        }
        return null;
    }


    // Statement
    public String visit(Statement n, MethodClass mc) {
        Node choice = n.f0.choice;
        if (choice instanceof Block) {
            Block b = (Block) choice;
            b.accept(this, mc);
        }
        else if (choice instanceof AssignmentStatement) {
            AssignmentStatement as = (AssignmentStatement) choice;
            as.accept(this, mc);
        }
        else if (choice instanceof ArrayAssignmentStatement) {
            ArrayAssignmentStatement aas = (ArrayAssignmentStatement) choice;
            aas.accept(this, mc);
        }
        else if (choice instanceof IfStatement) {
            IfStatement ifs = (IfStatement) choice;
            ifs.accept(this, mc);
        }
        else if (choice instanceof WhileStatement) {
            WhileStatement ws = (WhileStatement) choice;
            ws.accept(this, mc);
        }
        else if (choice instanceof PrintStatement) {
            // PrintStatement ps = (PrintStatement) choice;
            // ps.accept(this, mc);
            return null;
        }
        else {
            System.out.println("Unreconized statement type in " + mc.getMethodInfo().getName() + " in " + mc.getClassInfo().getName() + "!");
            throw new RuntimeException("Unreconized statement type in " + mc.getMethodInfo().getName() + " in " + mc.getClassInfo().getName() + "!");
        }

        return null;
    }

    // Block
    public String visit(Block n, MethodClass mc) {
        if (n.f1.present()) {
            for (Node node : n.f1.nodes) {
                Statement s = (Statement) node;
                s.accept(this, mc);
            }
        }
        return null;
    }

    // identifier = expression
    public String visit(AssignmentStatement n, MethodClass mc) {
        String varName = n.f0.f0.tokenImage;
        String varType = findVarType(varName, mc);

        Expression exp = n.f2;
        String expRetType = exp.accept(this, mc);

        if (expRetType == null || varType == null || (expRetType.equals(varType) != true)) {
            System.out.println("Invalid assignment statement!");
            throw new RuntimeException("Invalid assignment statement!");
        }

        return null;
    }


    // identifier[expression] = expression
    public String visit(ArrayAssignmentStatement n, MethodClass mc) {
        String varName = n.f0.f0.tokenImage;
        String varType = findVarType(varName, mc);
        String RHSType = n.f5.accept(this, mc);
        String parType = n.f2.accept(this, mc);
        if (varType == null || RHSType == null || parType == null || 
        !varType.equals(INTARR) || !RHSType.equals(INT) || !parType.equals(INT)) {
            System.out.println("Invalid array assignment statement! varType = " + varType + " RHSType = " + RHSType + " parType = " + parType);
            System.out.println("varName = " + varName);
            throw new RuntimeException("Invalid array assignment statement!");
        }
        return null;
    }


    // If statement
    public String visit(IfStatement n, MethodClass mc) {
        String ifCond = n.f2.accept(this, mc);
        if (!ifCond.equals(BOOLEAN)) {
            System.out.println("Invalid If statement condition!");
            throw new RuntimeException("Invalid If statement condition!");
        }
        n.f4.accept(this, mc);
        n.f6.accept(this, mc);
        return null;
    } 


    // While Statement
    public String visit(WhileStatement n, MethodClass mc) {
        String whileCond = n.f2.accept(this, mc);
        if (!whileCond.equals(BOOLEAN)) {
            System.out.println("Invalid While statement condition!");
            throw new RuntimeException("Invalid While statement condition!");
        }
        n.f4.accept(this, mc);
        return null;
    }



    /*
     * Helper function that returns type of variable.
     * Checks method parameters, local variables, class fields, and parent class fields.
     */
    private String findVarType(String varName, MethodClass mc) {


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



    // Expression
    public String visit(Expression n, MethodClass mc) {

        Node choice = n.f0.choice;

        // p1 && p2
        if (choice instanceof AndExpression) {
            AndExpression e = (AndExpression) choice;
            PrimaryExpression p1 = e.f0;
            PrimaryExpression p2 = e.f2;
            
            String p1Type = p1.accept(this, mc);
            String p2Type = p2.accept(this, mc);

            if (!p1Type.equals(BOOLEAN) || !p2Type.equals(BOOLEAN)) {
                System.out.println("And Expression type mismatch!");
                throw new RuntimeException("And Expression type mismatch!");
            }
            return BOOLEAN;
        }

        // p1 > p2
        if (choice instanceof CompareExpression) {
            CompareExpression e = (CompareExpression) choice;
            PrimaryExpression p1 = e.f0;
            PrimaryExpression p2 = e.f2;
            
            String p1Type = p1.accept(this, mc);
            String p2Type = p2.accept(this, mc);

            if (!p1Type.equals(INT) || !p2Type.equals(INT)) {
                System.out.println("Compare Expression type mismatch!");
                throw new RuntimeException("Compare Expression type mismatch!");
            }
            return BOOLEAN;
        }

        
        // p1 + p2
        if (choice instanceof PlusExpression) {
            PlusExpression e = (PlusExpression) choice;
            PrimaryExpression p1 = e.f0;
            PrimaryExpression p2 = e.f2;
            
            String p1Type = p1.accept(this, mc);
            String p2Type = p2.accept(this, mc);

            if (!p1Type.equals(INT) || !p2Type.equals(INT)) {
                System.out.println("Plus Expression type mismatch!");
                throw new RuntimeException("Plus Expression type mismatch!");
            }
            return INT;
        }


        // p1 - p2
        if (choice instanceof MinusExpression) {
            MinusExpression e = (MinusExpression) choice;
            PrimaryExpression p1 = e.f0;
            PrimaryExpression p2 = e.f2;
            
            String p1Type = p1.accept(this, mc);
            String p2Type = p2.accept(this, mc);

            if (!p1Type.equals(INT) || !p2Type.equals(INT)) {
                System.out.println("Minus Expression type mismatch!");
                throw new RuntimeException("Minus Expression type mismatch!");
            }
            return INT;
        }


        // p1 * p2
        if (choice instanceof TimesExpression) {
            TimesExpression e = (TimesExpression) choice;
            PrimaryExpression p1 = e.f0;
            PrimaryExpression p2 = e.f2;
            
            String p1Type = p1.accept(this, mc);
            String p2Type = p2.accept(this, mc);

            if (!p1Type.equals(INT) || !p2Type.equals(INT)) {
                System.out.println("Times Expression type mismatch!");
                throw new RuntimeException("Times Expression type mismatch!");
            }
            return INT;
        }


        // arr[i]
        if (choice instanceof ArrayLookup) {
            ArrayLookup e = (ArrayLookup) choice;
            PrimaryExpression p1 = e.f0;
            PrimaryExpression p2 = e.f2;
            
            String p1Type = p1.accept(this, mc);
            String p2Type = p2.accept(this, mc);

            if (!p1Type.equals(INTARR) || !p2Type.equals(INT)) {
                System.out.println("Array Lookup type mismatch!");
                throw new RuntimeException("Array Lookup type mismatch!");
            }
            return INT;
        }


        // arr.length
        if (choice instanceof ArrayLength) {
            ArrayLength e = (ArrayLength) choice;
            PrimaryExpression p1 = e.f0;
            
            String p1Type = p1.accept(this, mc);

            if (!p1Type.equals(INTARR)) {
                System.out.println("Array Length type mismatch!");
                throw new RuntimeException("Array Length type mismatch!");
            }
            return INT;
        }


        // message send
        // objectReference.methodName(argumentList)
        if (choice instanceof MessageSend) {
            MessageSend e = (MessageSend) choice;
            String className = e.f0.accept(this, mc); // e.f0 is PrimaryExp Token denoting class name

            // check if class object is valid
            ClassInfo c;
            if (symTable.hasClass(className)) {
                c = symTable.getClass(className);
            }
            else {
                System.out.println("MessageSend call failed! Class not found!");
                throw new RuntimeException("MessageSend call failed! Class not found!");
            }

            //check if class has method
            String methodName = e.f2.f0.tokenImage;
            MethodInfo m;
            m = null;
            while (c != null) {
                if (c.hasMethod(methodName)) {
                    m = c.getMethod(methodName);
                    break;
                }
                else {
                    c = symTable.getClass(c.getParent());
                    if (c == null) {
                        System.out.println("MessageSend call failed! Method not found!");
                        throw new RuntimeException("MessageSend call failed! Method not found!");
                    }
                }
            }

            List<String> argTypes = new ArrayList<>();
            if (e.f4.node != null) {
                ExpressionList expl = (ExpressionList) e.f4.node;
                Expression exp = expl.f0;
                String expType = exp.accept(this, mc);
                argTypes.add(expType);

                if (expl.f1.present()) {
                    for (Node expR : expl.f1.nodes) {
                        ExpressionRest expr = (ExpressionRest) expR;
                        Expression exprE = expr.f1;
                        String exprEReturn = exprE.accept(this, mc);
                        argTypes.add(exprEReturn);
                    }
                }
            }

            List<String> params = m.getParamTypes();
            for (int i = 0; i < params.size(); i++) {
                if (i >= argTypes.size()) {
                    System.out.println("Arguments missing!");
                    throw new RuntimeException("Arguments missing!");
                }
                String arg = argTypes.get(i);
                String para = params.get(i);
                if (!arg.equals(para)) {
                    System.out.println("Incomplete argument type!");
                    throw new RuntimeException("Incomplete argument type!");
                }
            }

            return m.getReturnType();
        }


        // primary expression 
        if (choice instanceof PrimaryExpression) {

            PrimaryExpression e = (PrimaryExpression) choice;
            return e.accept(this, mc);
        }


        return null;
    }


    // PrimaryExpression
    public String visit(PrimaryExpression n, MethodClass mc) {
        Node choice = n.f0.choice;
        if (choice instanceof IntegerLiteral) {
            return INT;
        }
        if (choice instanceof TrueLiteral || choice instanceof FalseLiteral) {
            return BOOLEAN;
        }
        // identifier refers to variable name
        if (choice instanceof Identifier) {
            Identifier i = (Identifier) choice;
            return findVarType(i.f0.tokenImage, mc);
        }
        if (choice instanceof ThisExpression) {
            return mc.getClassInfo().getName();
        }
        if (choice instanceof ArrayAllocationExpression) {
            ArrayAllocationExpression aae = (ArrayAllocationExpression) choice;
            String num = aae.f3.accept(this, mc);

            if (!num.equals(INT)) {
                System.out.println("Array length must be integer for array allocation!");
                throw new RuntimeException("Array length must be integer for array allocation!");
            }

            return INTARR;
        }
        if (choice instanceof AllocationExpression) {
            AllocationExpression ae = (AllocationExpression) choice;
            String objName = ae.f1.f0.tokenImage;
            if (!symTable.hasClass(objName)) {
                System.out.println("Class not found for allocation!");
                throw new RuntimeException("Class not found for allocation!");
            }
            return ae.f1.f0.tokenImage;
        }
        if (choice instanceof NotExpression) {
            NotExpression ne = (NotExpression) choice;
            String retType = ne.f1.accept(this, mc);
            if (!retType.equals(BOOLEAN)) {
                System.out.println("Incorrect type with Not!");
                throw new RuntimeException("Incorrect type with Not!");
            }
            return BOOLEAN;
        }

        if (choice instanceof BracketExpression) {
            BracketExpression be = (BracketExpression) choice;
            return be.f1.accept(this, mc);
        }
        


        System.out.println("Hit the null in primary expression!");
        Class clazz = choice.getClass();
        System.out.println(clazz);

        return null;

    }



}
