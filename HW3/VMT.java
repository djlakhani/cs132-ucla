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

    public void printVMT() {

        // TODO: REMOVE
        System.out.println("GO TO FILE!!");
        System.err.println("GO TO TERMINAL!!");

        for (Map.Entry<String, VClass> classMethod : ClassToMethodTable.entrySet()) {
            String className = classMethod.getKey();
            VClass vc = classMethod.getValue();
            System.out.println("Class: " + className);
            vc.printVC();
        }
    }
}
