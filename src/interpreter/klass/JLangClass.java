package interpreter.klass;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import interpreter.Interpreter;
import interpreter.callable.JLangCallable;
import interpreter.callable.JLangFunction;
public class JLangClass implements JLangCallable {
    final String name;
    public final Map<String, JLangFunction> methods;
    private final List<JLangClass> superclasses;
    public static final String CLASS_INITIALIZATION_FUNCTION_NAME = "init";
    public static final String CLASS_INNER_INSTANCE_NAME = "this";
    public static final String CLASS_SUPER_INSTANCE_NAME = "super";
    

    public JLangClass(String name) {
        this.name = name;
        this.methods = new HashMap<String, JLangFunction>();
        this.superclasses = new ArrayList<JLangClass>();
        defineBuiltInMethods();
    }
    public JLangClass(String name, List<JLangClass> superclasses, Map<String, JLangFunction> methods) {
        this.name = name;
        this.methods = methods;
        this.superclasses = superclasses; // Now stores a list of superclasses
        defineBuiltInMethods();
    }

    public JLangFunction findMethod(String name) {
        JLangFunction method = methods.get(name);
        if (method != null) {
            return method;
        }
        // Check each superclass for the method
        if (superclasses != null) {
            for (JLangClass superclass : superclasses) {
                method = superclass.findMethod(name);
                if (method != null) {
                    return method;
                }
            }
        }
        return null;
    }
    @Override
    public String toString() {
        return name;
    }
    @Override
    public Object call(Interpreter interpreter,
        List<Object> arguments) {
        JLangInstance instance = new JLangInstance(this);
        JLangFunction initializer = findMethod(CLASS_INITIALIZATION_FUNCTION_NAME);
        if (initializer != null) {
            initializer.bind(instance).call(interpreter, arguments);
        }
        return instance;
    }
    @Override
    public int arity() {
        JLangFunction initializer = findMethod(CLASS_INITIALIZATION_FUNCTION_NAME);
        if (initializer == null) return 0;
        return initializer.arity();
    }
    
    public Map<String, JLangFunction> getMethods() { return methods; }
    
    protected void defineBuiltInMethods() {
        // Define common methods for all JLangClasses here, if any
    }

}

