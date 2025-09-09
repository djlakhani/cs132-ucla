import java.util.*;
import syntaxtree.*;
import visitor.*;

/**
 * Typecheck.java
 *
 * Description: Typechecker for Minijava entrance class.
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

            // Begin typechecking
            TypecheckVisitor typeCheck = new TypecheckVisitor(symTable);
            root.accept(typeCheck, null);
        }
        catch (NullPointerException ne) {
            ne.printStackTrace();  // prints full stack trace with line numbers
        }
        catch (RuntimeException re) {
            System.out.println(re);
            System.out.println("SymbolTableBuilder or TypeCheckVisitor Error!");
            return;
        }
        catch (Exception e) {
            System.out.println("Parser Error!");
            return;
        }
        System.out.println("Typechecking successful!");
    }
} 
