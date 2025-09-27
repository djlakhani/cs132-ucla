import java.util.*;
import syntaxtree.*;
import visitor.*;

/**
 * ClassInfo.java
 *
 * Description: Wrapper class to store information about mini-java class.
 *
 * @author Diya Lakhani
 * @version 1.0
 * @since 2025-08-29
 */

public class ClassInfo {
    private final String name;
    private final String parent;  // if this extends another class
    private final Map<String, String> fields = new LinkedHashMap<>();
    private final Map<String, MethodInfo> methods = new LinkedHashMap<>();
    private int numFields = 0;

    public ClassInfo(String name, String parent) {
        this.name = name;
        this.parent = parent;
    }

    public String getName() {
        return name;
    }

    public String getParent() {
        return parent;
    }

    public int getNumFields() {
        return numFields;
    }

    public Map<String, MethodInfo> getMethods() {
        return methods;
    }

    public void addField(String fieldName, String type) {
        if (fields.containsKey(fieldName)) {
            System.out.println("Field " + fieldName + " already defined in " + this.name + "!");
            throw new RuntimeException("Field " + fieldName + " already defined in " + this.name + "!");
        }
        fields.put(fieldName, type);
        numFields = numFields + 1;
    }

    public void addMethod(String methodName, MethodInfo method) {
        if (methods.containsKey(methodName)) {
            System.out.println("Method " + methodName + " already defined in " + this.name + "!");
            throw new RuntimeException("Method " + methodName + " already defined in " + this.name + "!");
        }
        methods.put(methodName, method);
    }

    public String getFieldType(String fieldName) {
        return fields.get(fieldName);
    }

    public MethodInfo getMethod(String methodName) {
        return methods.get(methodName);
    }

    public boolean hasField(String fieldName) {
        return fields.containsKey(fieldName);
    }

    public boolean hasMethod(String methodName) {
        return methods.containsKey(methodName);
    }

}
