import java.util.*;
import syntaxtree.*;
import visitor.*;

/**
 * VMTBuilder.java
 *
 * Description: Contains helper methods to construct Virtual Method Table for each class.
 *
 * @author Diya Lakhani
 * @version 1.0
 * @since 2025-09-22
 */

public class VMTBuilder {
    private VMT vmt;
    private SymbolTable symTable;

    public VMTBuilder(SymbolTable symTable) {
        vmt = new VMT();
        this.symTable = symTable;
    }

    public VMT getVMT() {
        return vmt;
    }


    /**
     * Construct Virtual Method Table for each class.
     */
    public void BuildTable() {
        String className;
        String parentName;
        ClassInfo info;
        for (Map.Entry<String, ClassInfo> classInfoName : symTable.getClasses().entrySet()) {
            className = classInfoName.getKey();
            info = classInfoName.getValue();
            parentName = info.getParent();

            VClass vc = new VClass(className);
            BuildVC(className, parentName, vc);

            vmt.addClass(className, vc);
        }
    }


    /**
     * Construct VClass, i.e. create a list of methods (inherited and defined) for the specified class
     * @param className
     * @param parentName
     * @param vc Populate this VClass
     */
    public void BuildVC(String className, String parentName, VClass vc) {
        if (parentName == null) {
            for (Map.Entry<String, MethodInfo> method : symTable.getClass(className).getMethods().entrySet()) {
                vc.updateMethod(method.getKey(), className);
            }
            return;
        }
        else {
            BuildVC(parentName, symTable.getClass(parentName).getParent(), vc);
            for (Map.Entry<String, MethodInfo> method : symTable.getClass(className).getMethods().entrySet()) {
                vc.updateMethod(method.getKey(), className);
            }
            return;
        }
    }


    
}
