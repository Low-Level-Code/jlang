package enivirement;

import java.util.HashMap;
import java.util.Map;

import interpreter.errors.RuntimeError;
import interpreter.errors.UndefinedVariableException;
import tokenizer.Token;

public class Environment {

    public final Environment enclosing;
    public final Map<String, Object> values = new HashMap<>();
    public final Map<String, Object> constants = new HashMap<>();
    

    public Environment() {
        enclosing = null;
    }
    public Environment(Environment enclosing) {
        this.enclosing = enclosing;
    }

    public Boolean isConstant(String name){
        return constants.containsKey(name);
    }

    public Boolean isVariable(String name){
        return values.containsKey(name);
    }
    
    Environment ancestor(int distance) {
        Environment environment = this;
        for (int i = 0; i < distance; i++) {
            environment = environment.enclosing;
        }
        return environment;
    }

    public void assignAt(int distance, Token name, Object value) {
        ancestor(distance).values.put(name.lexeme, value);
    }
         
    public Object getAt(int distance, String name) {
        return ancestor(distance).values.get(name);
    }
        
    public Object get(Token name) {
        if (isConstant(name.lexeme)) {
            return constants.get(name.lexeme);
        }
        if (isVariable(name.lexeme)) {
            return values.get(name.lexeme);
        }
        if (enclosing != null) return enclosing.get(name);

        throw new UndefinedVariableException(name,
                "Undefined variable '" + name.lexeme + "'.");
    }

    public void define(String name, Object value) {// this will only handle the variable definition
        if (isConstant(name)) constants.remove(name); // this will change the constant 
        values.put(name, value);
    }

    public void assign(Token name, Object value) {
        if (constants.containsKey(name.lexeme)){
            throw new RuntimeError(name,
                "'" + name.lexeme + "' is a Constant and can't be reassigned.");
        }
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
    public void defineConst(String name, Object value) {
        constants.put(name, value);
    }
        
}