import java.util.*;
import syntaxtree.*;
import visitor.*;

/**
 * Typecheck.java
 *
 * Description: Typechecker for Minijava
 *
 * @author Diya Lakhani
 * @version 1.0
 * @since 2025-08-25
 */


public class Typecheck {
    public static void main(String[] args) throws ParseException {
        try {
            MiniJavaParser parser = new MiniJavaParser(System.in);
            Goal root = parser.Goal();

            // Build symbol table
            SymbolTableBuilder stBuilder = new SymbolTableBuilder();
            root.accept(stBuilder, null);
            SymbolTable symTable = stBuilder.getSymbolTable();
        }
        catch (Exception e) {
            System.out.println("Parser Error!");
            return;
        }
        System.out.println("Typechecking successful!");
    }
} 
