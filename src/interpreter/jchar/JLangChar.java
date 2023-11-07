package interpreter.jchar;

import interpreter.Interpreter;
import interpreter.callable.JLangFunction;
import interpreter.errors.RuntimeError;
import interpreter.klass.JLangClass;
import interpreter.klass.JLangObject;
import tokenizer.Token;

import java.util.HashMap;
import java.util.List;

public class JLangChar extends JLangClass implements JLangObject {

    private final char value;

    public JLangChar(char value) {
        super("JLangChar");
        this.value = value;
        defineBuiltInMethods();
    }
    
    @Override
    protected void defineBuiltInMethods() {
        // Define JLangChar-specific methods

        methods.put("isDigit", new JLangFunction(null, null, false) {
            @Override
            public int arity() {
                return 0;
            }
        
            @Override
            public String toString() {
                return "<native fn isDigit>";
            }
            @Override
            public Object call(Interpreter interpreter, List<Object> arguments) {
                return Character.isDigit(JLangChar.this.value);
            }
        });

        methods.put("toUpperCase", new JLangFunction(null, null, false) {
            @Override
            public int arity() {
                return 0;
            }
            @Override
            public String toString() {
                return "<native fn toUpperCase>";
            }
            @Override
            public Object call(Interpreter interpreter, List<Object> arguments) {
                return Character.toUpperCase(JLangChar.this.value);
            }
        });

        // Add other char-specific methods similarly
    }

    @Override
    public Object get(Token name) {
        JLangFunction method = this.findMethod(name.lexeme);
        if (method != null) {
            return method.bind(this);
        }
        throw new RuntimeError(name, "Undefined property '" + name.lexeme + "'.");
    }

    // Add other JLangChar specific methods here.

    public char getValue() {
        return value;
    }

    @Override
    public String toString() {
        return '\'' + Character.toString(value) + '\'';
    }
    
    // ... potentially other JLangChar methods ...
}
