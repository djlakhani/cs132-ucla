import java.util.*;
import syntaxtree.*;
import visitor.*;

/**
 * MethodClass.java
 *
 * Description: Wrapper class to store current method and current class of statements.
 *
 * @author Diya Lakhani
 * @version 1.0
 * @since 2025-09-03
 */

public class MethodClass {
    private ClassInfo currClass;
    private MethodInfo currMethod;

    public MethodClass(ClassInfo currClass, MethodInfo currMethod) {
        this.currClass = currClass;
        this.currMethod = currMethod;
    }

    public ClassInfo getClassInfo() {
        return this.currClass;
    }

    public MethodInfo getMethodInfo() {
        return this.currMethod;
    }
}
