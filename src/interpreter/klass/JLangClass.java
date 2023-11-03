package interpreter.klass;

import java.util.List;
import java.util.Map;

import interpreter.Interpreter;
import interpreter.callable.JLangCallable;
import interpreter.callable.JLangFunction;
public class JLangClass implements JLangCallable {
    final String name;
    private final Map<String, JLangFunction> methods;
    final JLangClass superclass;
    public static final String CLASS_INITIALIZATION_FUNCTION_NAME = "init";
    public static final String CLASS_INNER_INSTANCE_NAME = "this";
    public static final String CLASS_SUPER_INSTANCE_NAME = "super";
    
    public JLangClass(String name, JLangClass superclass, Map<String, JLangFunction> methods) {
        this.name = name;
        this.methods = methods;
        this.superclass = superclass;
    }
    public JLangFunction findMethod(String name) {
        if (superclass != null) {
            return superclass.findMethod(name);
        }
        if (methods.containsKey(name)) {
            return methods.get(name);
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

}

