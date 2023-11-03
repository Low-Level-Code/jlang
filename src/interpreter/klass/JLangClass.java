package interpreter.klass;

import java.util.List;
import java.util.Map;

import interpreter.Interpreter;
import interpreter.callable.JLangCallable;
public class JLangClass implements JLangCallable {
    final String name;
    public JLangClass(String name) {
        this.name = name;
    }
    @Override
    public String toString() {
        return name;
    }
    @Override
    public Object call(Interpreter interpreter,
        List<Object> arguments) {
        JLangInstance instance = new JLangInstance(this);
        return instance;
    }
    @Override
    public int arity() {
        return 0;
    }

}

