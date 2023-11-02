package enivirement;

import java.util.HashMap;
import java.util.Map;

import interpreter.errors.RuntimeError;
import interpreter.errors.UndefinedVariableException;
import tokenizer.Token;

public class Environment {

    final Environment enclosing;
    public final Map<String, Object> values = new HashMap<>();
    
    public Environment() {
        enclosing = null;
    }
    public Environment(Environment enclosing) {
        this.enclosing = enclosing;
    }

    public Object get(Token name) {
        if (values.containsKey(name.lexeme)) {
            return values.get(name.lexeme);
        }
        if (enclosing != null) return enclosing.get(name);

        throw new UndefinedVariableException(name,
                "Undefined variable '" + name.lexeme + "'.");
    }

    public void define(String name, Object value) {
        values.put(name, value);
    }

    public void assign(Token name, Object value) {
        if (values.containsKey(name.lexeme)) {
            values.put(name.lexeme, value);
            return;
        }
        if (enclosing != null) {
            enclosing.assign(name, value);
            return;
        }
        
        throw new UndefinedVariableException(name,
        "Undefined variable '" + name.lexeme + "'.");
    }
        
}