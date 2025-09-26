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

public class TranslatorVisitor extends GJDepthFirst<String, Void> {
    private SymbolTable symTable;
    private VMT vmt;
    private int regCount;
    private int spaceCount;

    // TODO: Update this parameter during classDeclaration and classExtends declarations
    private String currentClass;

    public TranslatorVisitor(SymbolTable symTable, VMT vmt) {
        this.symTable = symTable;
        this.vmt = vmt;
        regCount = 0;
        spaceCount = 0;
        // there is no actual "Main" class, however, it does not
        // make sense to call this.methodName when in main method.
        currentClass = "Main";
    }

    /**
     * Goal
     * f0 -> MainClass()
     * f1 -> ( TypeDeclaration() )*
     * f2 -> <EOF>
     * @param n root node of AST.
     * @param arg
     * @return
     */
    public String visit(Goal n, Void arg) {
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
     * MainClass
     * f0 -> "class"
     * f1 -> Identifier()
     * f2 -> "{"
     * f3 -> "public"
     * f4 -> "static"
     * f5 -> "void"
     * f6 -> "main"
     * f7 -> "("
     * f8 -> "String"
     * f9 -> "["
     * f10 -> "]"
     * f11 -> Identifier()
     * f12 -> ")"
     * f13 -> "{"
     * f14 -> ( VarDeclaration() )*
     * f15 -> ( Statement() )*
     * f16 -> "}"
     * f17 -> "}"
     * @param n
     * @param arg
     * @return
     */
    public String visit(MainClass n, Void arg) {
        System.out.println("func Main()");
        spaceCount = spaceCount + 1;
        if (n.f15.present()) {
            for (Node s : n.f15.nodes) {
                Statement sta = (Statement) s;
                sta.accept(this, null);
            }
        }

        return null;
    }



    /**
     * Statement
     * @param n
     * @param arg
     * @return
     */
    public String visit(Statement n, Void arg) {
        Node choice = n.f0.choice;
        if (choice instanceof PrintStatement) {
            PrintStatement ps = (PrintStatment) choice;
            ps.accept(this, null);
            return null;
        }
        // TODO: Complete the rest of the statements

        return null;
    }


    /**
     * PrintStatement
     * @param n
     * @param arg
     * @return
     */
    public String visit(PrintStatement n, Void arg) {
        String retExpr = n.f2.accept(this, null);
        System.out.println("PrintIntS(" + retExpr + ")");
        return null;
    }


    /**
     * Expression
     * @param n
     * @param arg
     * @return
     */
    public String visit(Expression n, Void arg) {
        Node choice = n.f0.choice;

        // MessageSend
        if (choice instanceof MessageSend) {
            MessageSend ms = (MessageSend) choice;
            return ms.accept(this, null);
        }

        // TODO: Complete analsis for the rest of the expressions

    }


    public String visit(MessageSend n, Void arg) {
        // TODO: handle return statement here.
        // MessageSend	::=	PrimaryExpression "." Identifier "(" ( ExpressionList )? ")"


        String peRet = n.f0.accept(this, null);
        String nextReg = "t." + regCount;
        regCount = regCount + 1;
        String methodName = n.f2.f0.tokenImage; 


        return null;
    }


    public String visit(PrimaryExpression n, Void arg) {
        Node choice = n.f0.choice;
        if (choice instanceof AllocationExpression) {
            AllocationExpression ae = (AllocationExpression) choice;

        }
    }



    /**
     * TODO: Update allowable call statuses
     * Only called by MessageSend currently.
     * @param n
     * @return
     */
    public String getObjectType(PrimaryExpression n) {
        Node choice = n.f0.choice;
        if (choice instanceof AllocationExpression) {
            AllocationExpression ae = (AllocationExpression) choice;
            return ae.f1.f0.tokenImage;
        }
        if (choice instanceof ThisExpression) {
            return currentClass;
        }
        if (choice instanceof Identifier) {
            Identifier i = (Identifier) choice;
            return i.f0.tokenImage;
        }
        if (choice instanceof BracketExpression) {
            BracketExpression be = (BracketExpression) choice;
            return be.f1.accept(this, null);
        }
        throw new RuntimeException("Typechecking fail, invalid object type demanded in getObjectType");
    }
}
