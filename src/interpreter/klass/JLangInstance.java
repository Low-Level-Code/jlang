package interpreter.klass;

import java.util.List;

import interpreter.Interpreter;
import interpreter.callable.JLangFunction;
import interpreter.errors.RuntimeError;
import tokenizer.Token;

import java.util.HashMap;
import java.util.Map;

public class JLangInstance {

    private JLangClass klass;
    private final Map<String, Object> fields = new HashMap<>();

    JLangInstance(JLangClass klass) {
        this.klass = klass;
    }
    @Override
    public String toString() {
        return klass.name + " instance";
    }

    public Object get(Token name) {
        if (fields.containsKey(name.lexeme)) {
            return fields.get(name.lexeme);
        }

        JLangFunction method = klass.findMethod(name.lexeme);
        if (method != null) return method.bind(this);

        throw new RuntimeError(name, "Undefined property '" + name.lexeme + "'.");
    }
    
    public void set(Token name, Object value) {
        fields.put(name.lexeme, value);
    }

}
