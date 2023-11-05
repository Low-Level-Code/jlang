package interpreter.array;

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

public class JLangArray extends JLangClass  implements JLangObject, JLangIndexible{
    private final List<Object> elements;

    public JLangArray(List<Object> elements) {
        super("JLangArray", new ArrayList<JLangClass>(), new HashMap<String, JLangFunction>());
        this.elements = elements;
        defineBuiltInMethods();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        
        for (int i = 0; i < elements.size(); i++) {
            sb.append(elements.get(i));
            if (i < elements.size() - 1) {
                sb.append(", ");
            }
        }
        
        sb.append("]");
        return sb.toString();
    }
    // Add methods for working with the array, such as get, set, length, etc.
    public int size() {
        return elements.size();
    }
        
    public void setItem(int index, Object value) {
        // Ensure the index is within the array bounds
        if (index >= 0 && index < elements.size()) {
            elements.set(index, value);
        } else {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + elements.size());
        }
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
                return size();
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

        defineMethod("set", new JLangFunction(null, null, false) {
            @Override
            public int arity() {
                return 2; // set takes two parameters
            }

            @Override
            public Object call(Interpreter interpreter, List<Object> arguments) {
                // Again, validate argument types and handle errors
                int index = ((Double) arguments.get(0)).intValue();
                Object value = arguments.get(1);
                setItem(index, value);
                return null; // set doesn't return anything
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
    // Indexible
    @Override
    public int length() {
        return size();
    }
    @Override
    public Object getItem(int index) {
        if (index >= 0 && index < elements.size()) {
            return elements.get(index);
        } else {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + elements.size());
        }
    }
}