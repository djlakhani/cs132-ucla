import java.util.*;

//import org.w3c.dom.Node;

import syntaxtree.*;
import visitor.*;

// import org.w3c.dom.Node;

public class SymbolTableBuilder extends GJDepthFirst<Void, Void> {
    private SymbolTable symTable;
    private MethodInfo methodInfo;

    public SymbolTableBuilder() {
        this.symTable = new SymbolTable();
    }

    @Override
    public Void visit(Goal n, Void arg) {
        n.f0.accept(this, null);
        if (n.f1.present()) {
            for (Node td : n.f1.nodes) {
                TypeDeclaration typeDec = (TypeDeclaration) td;
                typeDec.accept(this, null);
            }
        }
        // return symTable;
        return null;
    }

    public SymbolTable getSymbolTable() {
        return symTable;
    }

    @Override
    public Void visit(MainClass n, Void arg) {
        String mainClassName = n.f1.f0.tokenImage;
        ClassInfo mainClass = new ClassInfo(mainClassName, null);
        String mainArgName = n.f11.f0.tokenImage;
        MethodInfo mainMethod = new MethodInfo("main", "void");
        mainMethod.addParameter(mainArgName, "String[]");
        mainClass.addMethod("main", mainMethod);
        this.symTable.addClass(mainClassName, mainClass);
        return null;
    }


    @Override
    public Void visit(TypeDeclaration n, Void arg) {
        n.f0.choice.accept(this, null);
        return null;
    }

    /*
     * ClassDeclaration Visitor
     */
    @Override
    public Void visit(ClassDeclaration n, Void arg) {
        String className = n.f1.f0.tokenImage;
        ClassInfo newClass = new ClassInfo(className, null);
        String varName;
        String varType;

        this.symTable.addClass(className, newClass);

        if (n.f3.present()) {
            for (Node vd : n.f3.nodes) {
                VarDeclaration varDec = (VarDeclaration) vd;
                varName = varDec.f1.f0.tokenImage;
                varType = extractType(varDec.f0);
                newClass.addField(varName, varType);
            }
        }
        if (n.f4.present()) {
            for (Node md : n.f4.nodes) {
                // MethodInfo methodInfo = methodDec.accept(this, null);
                MethodDeclaration methodDec = (MethodDeclaration) md;
                methodDec.accept(this, null);
                newClass.addMethod(this.methodInfo.getName(), this.methodInfo);
            }
        }
        return null;
    }


    /*
     * ClassExtendsDeclaration Visitor
     */
    @Override
    public Void visit(ClassExtendsDeclaration n, Void arg) {
        String className = n.f1.f0.tokenImage;
        String parentName = n.f3.f0.tokenImage;
        ClassInfo newClass = new ClassInfo(className, parentName);
        String varName;
        String varType;

        this.symTable.addClass(className, newClass);

        if (n.f5.present()) {
            for (Node vd : n.f5.nodes) {
                VarDeclaration varDec = (VarDeclaration) vd;
                varName = varDec.f1.f0.tokenImage;
                varType = extractType(varDec.f0);
                newClass.addField(varName, varType);
            }
        }
        if (n.f6.present()) {
            for (Node md : n.f6.nodes) {
                // MethodInfo methodInfo = methodDec.accept(this, null);
                MethodDeclaration methodDec = (MethodDeclaration) md;
                methodDec.accept(this, null);
                newClass.addMethod(this.methodInfo.getName(), this.methodInfo);
            }
        }
        return null;
    }


    @Override
    public Void visit(MethodDeclaration n, Void arg) {
        String methodName = n.f2.f0.tokenImage;
        String returnType = extractType(n.f1);
        MethodInfo newMethod = new MethodInfo(methodName, returnType);
        String paramName;
        String paramType;
        String localName;
        String localType;

        if (n.f4.node != null) {
            FormalParameterList nodeOpN = (FormalParameterList) n.f4.node;
            FormalParameter fp = (FormalParameter) nodeOpN.f0;
            paramName = fp.f1.f0.tokenImage;
            paramType = extractType(fp.f0);
            newMethod.addParameter(paramName, paramType);

            NodeListOptional nlo = nodeOpN.f1;

            if (nlo.present()) {
                for (Node node : nlo.nodes) {
                    FormalParameterRest fpr = (FormalParameterRest) node;
                    fp = fpr.f1;
                    paramName = fp.f1.f0.tokenImage;
                    paramType = extractType(fp.f0);
                    newMethod.addParameter(paramName, paramType);
                }
            }
        }

        if (n.f7.present()) {
            for (Node vd : n.f7.nodes) {
                VarDeclaration varDec = (VarDeclaration) vd;
                localName = varDec.f1.f0.tokenImage;
                localType = extractType(varDec.f0);
                newMethod.addLocalVar(localName, localType);
            }
        }

        this.methodInfo = newMethod;
        return null;
    }


    /*
     * Helper function to extract type of variable.
     * @param t The Type node.
     * @return String type.
     * @throws Exception if Type t is unrecognized.
     */
    private String extractType(Type t) {
        Node choice = t.f0.choice;

        if (choice instanceof BooleanType) {
            return "boolean";
        } else if (choice instanceof IntegerType) {
            return "int";
        } else if (choice instanceof ArrayType) {
            return "int[]";
        } else if (choice instanceof Identifier) {
            return ((Identifier) choice).f0.tokenImage;
        } else {
            throw new RuntimeException("Unrecognized type node!");
        }
    }


}
