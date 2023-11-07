package interpreter.string;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import interpreter.Interpreter;
import interpreter.callable.JLangFunction;
import interpreter.errors.RuntimeError;
import interpreter.indexible.JLangIndexible;
import interpreter.jchar.JLangChar;
import interpreter.klass.JLangClass;
import interpreter.klass.JLangObject;
import tokenizer.Token;

public class JLangString extends JLangClass  implements JLangObject, JLangIndexible {
    private String content;
    
    public JLangString(String content) {
        super("JLangString");
        this.content = content; 
    }

    @Override
    public String toString() {
        // TODO Auto-generated method stub
        return '"'+ getContent() + '"';
    }


    public String getContent() {
        return this.content;
    }
    
    // Method to sort the characters of the string
    @Override
    public void sort() {
        char[] contentArray = this.content.toCharArray();
        Arrays.sort(contentArray);
        this.content = new String(contentArray);
    }

    // Method to reverse the characters of the string
    @Override
    public void reverse() {
        this.content = new StringBuilder(this.content).reverse().toString();
    }

    @Override
    protected void defineBuiltInMethods() {
        methods.put("size", new JLangFunction(null, null, false) {
            @Override
            public int arity() {
                return 0; // size doesn't take any parameters
            }

            @Override
            public Object call(Interpreter interpreter, List<Object> arguments) {
                return length();
            }
        });

        methods.put("get", new JLangFunction(null, null, false) {
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

         // Check if the string contains a certain substring
        methods.put("contains", new JLangFunction(null, null, false) {
            @Override
            public int arity() { return 1; }

            @Override
            public Object call(Interpreter interpreter, List<Object> arguments) {
                String substring = arguments.get(0).toString();
                return content.contains(substring);
            }
        });

        // Convert the string to uppercase
        methods.put("toUpperCase", new JLangFunction(null, null, false) {
            @Override
            public int arity() { return 0; }

            @Override
            public Object call(Interpreter interpreter, List<Object> arguments) {
                return content.toUpperCase();
            }
        });

        // Convert the string to lowercase
        methods.put("toLowerCase", new JLangFunction(null, null, false) {
            @Override
            public int arity() { return 0; }

            @Override
            public Object call(Interpreter interpreter, List<Object> arguments) {
                return content.toLowerCase();
            }
        });

        // Check if the string starts with a certain substring
        methods.put("startsWith", new JLangFunction(null, null, false) {
            @Override
            public int arity() { return 1; }

            @Override
            public Object call(Interpreter interpreter, List<Object> arguments) {
                String prefix = arguments.get(0).toString();
                return content.startsWith(prefix);
            }
        });

        // Check if the string ends with a certain substring
        methods.put("endsWith", new JLangFunction(null, null, false) {
            @Override
            public int arity() { return 1; }

            @Override
            public Object call(Interpreter interpreter, List<Object> arguments) {
                String suffix = arguments.get(0).toString();
                return content.endsWith(suffix);
            }
        });

        // Replace a part of the string with another string
        methods.put("replace", new JLangFunction(null, null, false) {
            @Override
            public int arity() { return 2; }

            @Override
            public Object call(Interpreter interpreter, List<Object> arguments) {
                String search = arguments.get(0).toString();
                String replacement = arguments.get(1).toString();
                return content.replace(search, replacement);
            }
        });

        // Trim whitespace from the beginning and end of the string
        methods.put("trim", new JLangFunction(null, null, false) {
            @Override
            public int arity() { return 0; }

            @Override
            public Object call(Interpreter interpreter, List<Object> arguments) {
                return content.trim();
            }
        });

        // Substring between two indices
        methods.put("substring", new JLangFunction(null, null, false) {
            @Override
            public int arity() { return 2; }

            @Override
            public Object call(Interpreter interpreter, List<Object> arguments) {
                int start = ((Double) arguments.get(0)).intValue();
                int end = ((Double) arguments.get(1)).intValue();
                return content.substring(start, end);
            }
        });

        // Return the index of the first occurrence of a specified substring
        methods.put("indexOf", new JLangFunction(null, null, false) {
            @Override
            public int arity() { return 1; }

            @Override
            public Object call(Interpreter interpreter, List<Object> arguments) {
                String substring = arguments.get(0).toString();
                return (double) content.indexOf(substring);
            }
        });

        // Return a string repeated a certain number of times
        methods.put("repeat", new JLangFunction(null, null, false) {
            @Override
            public int arity() { return 1; }

            @Override
            public Object call(Interpreter interpreter, List<Object> arguments) {
                int count = ((Double) arguments.get(0)).intValue();
                return content.repeat(count);
            }
        });
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
        if (index < 0) index = content.length() + index;
        if (index >= 0 && index < content.length()) {
            return new JLangChar(content.charAt(index));
        } else {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + content.length());
        }
    }

    @Override
    public int length() {
        return this.content.length();
    }

        @Override
    public List<Object> asList() {
        // Return a list of Characters or JChar objects
        return content.chars().mapToObj(c -> new JLangChar((char)c)) // This creates a stream of Character
                     .collect(Collectors.toList()); // Collects into a List of Character
    }
}