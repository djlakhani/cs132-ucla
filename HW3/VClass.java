import java.util.*;
import syntaxtree.*;
import visitor.*;

/**
 * VClass.java
 *
 * Description: Class for each class listing all the methods in the class and where they are defined. 
 * (Child or parent class).
 *
 * @author Diya Lakhani
 * @version 1.0
 * @since 2025-09-22
 */

public class VClass {
    private final Map<String, String> MethodToClass = new LinkedHashMap<>();
    private final Map<String, Integer> MethodToIdx = new LinkedHashMap<>();
    private String name;
    private int currIdx;

    public VClass(String name) {
        this.name = name;
        this.currIdx = 0;
    }

    /**
     * Add a method to the VClass and also specify the class where the method is defined.
     * @param methodName
     * @param className
     */
    public void updateMethod(String methodName, String className) {
        MethodToClass.put(methodName, className);
        if (!MethodtoIdx.contains(methodName)) {
            MethodtoIdx.put(methodName, currIdx);
            currIdx = currIdx + 1;
        }
    }

    /**
     * Returns the offset for method class with respect to the virtual method table of the class.
     * @param methodName
     * @return
     */
    public int getOffset(String methodName) {
        int offset = MethodToIdx.get(methodName) * 4;
        return offset;
    }

    /**
     * Pretty printing functions for debugging purposes.
     */
    public void printVC() {
        for (Map.Entry<String, String> classMethod : MethodToClass.entrySet()) {
            String methodName = classMethod.getKey();
            String classDefined = classMethod.getValue();
            System.out.println("    Method: " + methodName + " ------------ Class defined: " + classDefined);
        }
    }
}
