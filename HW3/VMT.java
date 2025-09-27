import java.util.*;
import syntaxtree.*;
import visitor.*;

/**
 * VMT.java
 *
 * Description: Virtual Method Table for each class. Wrapper class name for mapping class to VClass.
 *
 * @author Diya Lakhani
 * @version 1.0
 * @since 2025-09-22
 */


public class VMT {
    private final Map<String, VClass> ClassToMethodTable = new LinkedHashMap<>();

    public void addClass(String className, VClass methodList) {
        ClassToMethodTable.put(className, methodList);
    }

    public VClass getVClass(String className) {
        return ClassToMethodTable.get(className);
    }

    /**
     * Pretty printing function for debugging purposes.
     */
    public void printVMT() {
        for (Map.Entry<String, VClass> classMethod : ClassToMethodTable.entrySet()) {
            String className = classMethod.getKey();
            VClass vc = classMethod.getValue();
            System.err.println("Class: " + className);
            vc.printVC();
        }
    }
}
