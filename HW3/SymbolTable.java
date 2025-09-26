import java.util.*;
import syntaxtree.*;
import visitor.*;

/**
 * SymbolTable.java
 *
 * Description: Symbol Table for type-checking mini-java programs.
 *
 * @author Diya Lakhani
 * @version 1.0
 * @since 2025-08-29
 */

public class SymbolTable {
    private final Map<String, ClassInfo> classes = new LinkedHashMap<>();
    
    public void addClass(String className, ClassInfo classInfo) {
        if (classes.containsKey(className)) {
            System.out.println("Class " + className + " already defined!");
            throw new RuntimeException("Class " + className + " already defined!");
        }
        classes.put(className, classInfo);
    }

    public ClassInfo getClass(String className) {
        return classes.get(className);
    }

    public boolean hasClass(String className) {
        return classes.containsKey(className);
    }

    public Map<String, ClassInfo> getClasses() {
        return classes;
    }

    @Override
    public String toString() {
        String printString = "";

        for (Map.Entry<String, ClassInfo> classInfoName : classes.entrySet()) {
            ClassInfo classInfo = classInfoName.getValue();
            printString = printString + "Class: " + classInfoName.getKey() + " Parent: " + classInfo.getParent() + "\n";

            for (Map.Entry<String, MethodInfo> methodInfoName : classInfo.getMethods().entrySet()) {
                printString = printString + "   " + "Method: " + methodInfoName.getKey() + "\n";
            }
        }
        return printString;
    }
     
}
