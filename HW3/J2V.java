import java.util.*;
import syntaxtree.*;
import visitor.*;

/**
 * J2V.java
 *
 * Description: Entrance point for program that converts MiniJava to Vapor.
 *
 * @author Diya Lakhani
 * @version 1.0
 * @since 2025-09-10
 */

public class J2V {
    public static void main(String[] args) {
        try {
            MiniJavaParser parser = new MiniJavaParser(System.in);
            Goal root = parser.Goal();
            
            // Build symbol table
            SymbolTableBuilder stBuilder = new SymbolTableBuilder();
            root.accept(stBuilder, null);
            SymbolTable symTable = stBuilder.getSymbolTable();

            System.out.println("Printing Symbol Table...");
            System.out.println(symTable);

            // Building Virtual Method Tables
            VMTBuilder vmtBuilder = new VMTBuilder(symTable);
            vmtBuilder.BuildTable();

            // Print VMT for each class for debugging
            VMT vmt = vmtBuilder.getVMT();
            vmt.printVMT();

            System.out.println("Successfully Converted!");


            //TODO: REMOVE testing / correction point
            System.out.println("NEED TO CLEAN ABOVE");

            
        }
        catch (Exception e) {
            System.out.println(e);
            return;
        }
        
    }
}
