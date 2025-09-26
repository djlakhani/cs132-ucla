import java.util.*;
import syntaxtree.*;
import visitor.*;

/**
 * ClassInfo.java
 *
 * Description: Wrapper class to store information about mini-java method.
 *
 * @author Diya Lakhani
 * @version 1.0
 * @since 2025-08-29
 */

public class MethodInfo {
    private final String name;
    private final String returnType;
    private final Map<String, String> params = new LinkedHashMap<>();
    private final Map<String, String> locals = new LinkedHashMap<>();
    private final List<String> paramTypes = new ArrayList<>();

    public MethodInfo(String methodName, String returnType) {
        this.name = methodName;
        this.returnType = returnType;
    }

    public String getName() {
        return name;
    }

    public String getReturnType() {
        return returnType;
    }

    public void addParameter(String paramName, String type) {
        if (params.containsKey(paramName)) {
            System.out.println("Parameter " + paramName + " already defined in method " + name + "!");
            throw new RuntimeException("Parameter " + paramName + " already defined in method " + name + "!");
        }
        params.put(paramName, type);
        paramTypes.add(type);
    }

    public void addLocalVar(String varName, String type) {
        if (params.containsKey(varName) || locals.containsKey(varName)) {
            System.out.println("Variable " + varName + " already defined in method " + name + "!");
            throw new RuntimeException("Variable " + varName + " already defined in method " + name + "!");
        }
        locals.put(varName, type);
    }

    public boolean hasParam(String paramName) {
        return params.containsKey(paramName);
    }

    public boolean hasLocal(String varName) {
        return locals.containsKey(varName);
    }

    public String getParamType(String paramName) {
        return params.get(paramName);
    }

    public String getLocalType(String varName) {
        return locals.get(varName);
    }

    public List<String> getParamTypes() {
        return paramTypes;
    }

}
