package interpreter.string;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import interpreter.Interpreter;
import interpreter.callable.JLangFunction;
import interpreter.errors.RuntimeError;
import interpreter.indexible.JLangIndexible;
import interpreter.klass.JLangClass;
import interpreter.klass.JLangObject;
import tokenizer.Token;

public class JLangString extends JLangClass  implements JLangObject, JLangIndexible {
    private final String content;

    public JLangString(String content) {
        super("JLangString", new ArrayList<JLangClass>(), new HashMap<String, JLangFunction>());
        this.content = content;
         defineBuiltInMethods();

    }

    @Override
    public String toString() {
        // TODO Auto-generated method stub
        return getContent();
    }


    public String getContent() {
        return this.content;
    }
    
    private void defineMethod(String name, JLangFunction function) {
        getMethods().put(name, function);
    }
    private void defineBuiltInMethods() {
        defineMethod("size", new JLangFunction(null, null, false) {
            @Override
            public int arity() {
                return 0; // size doesn't take any parameters
            }

            @Override
            public Object call(Interpreter interpreter, List<Object> arguments) {
                return length();
            }
        });

        defineMethod("get", new JLangFunction(null, null, false) {
            @Override
            public int arity() {
                return 1; // get takes one parameter
            }

            @Override
            public Object call(Interpreter interpreter, List<Object> arguments) {
                // You would also need to check that arguments are of the correct type and handle any errors
                int index = ((Double) arguments.get(0)).intValue();
                return getItem(index);
            }
        });

        // Add more methods as needed
    }

    // Instance
    @Override
    public Object get(Token name) {
        if (methods.containsKey(name.lexeme)) {
            return methods.get(name.lexeme);
        }

        JLangFunction method = this.findMethod(name.lexeme);
        if (method != null) return method.bind(this);

        throw new RuntimeError(name, "Undefined property '" + name.lexeme + "'.");
    }
    // ... potentially other string methods ...

    // Indexible
    @Override
    public Object getItem(int index) {
        return this.content.charAt(index);
    }

    @Override
    public int length() {
        return this.content.length();
    }

}