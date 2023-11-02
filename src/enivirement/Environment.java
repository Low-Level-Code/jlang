package enivirement;

import java.util.HashMap;
import java.util.Map;

import interpreter.errors.RuntimeError;
import tokenizer.Token;

public class Environment {
    public final Map<String, Object> values = new HashMap<>();
    
    public Object get(Token name) {
        if (values.containsKey(name.lexeme)) {
            return values.get(name.lexeme);
        }
        throw new RuntimeError(name,
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
        throw new RuntimeError(name,
        "Undefined variable '" + name.lexeme + "'.");
    }
        
}