package interpreter.array;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import interpreter.Interpreter;
import interpreter.callable.JLangCallable;
import interpreter.callable.JLangFunction;
import interpreter.errors.RuntimeError;
import interpreter.indexible.JLangIndexible;
import interpreter.klass.JLangClass;
import interpreter.klass.JLangObject;
import tokenizer.Token;
import tokenizer.TokenType;

public class JLangArray extends JLangClass  implements JLangObject, JLangIndexible{
    private final List<Object> elements;

    public JLangArray(List<Object> elements) {
        super("JLangArray");
        this.elements = elements;
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

    public List<Object> getElements(){
        return elements;
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

    @Override
    public void sort() {
        if(elements != null && !elements.isEmpty() && elements.get(0) instanceof Comparable) {
            Collections.sort((List)elements);
        } else {
            throw new IllegalStateException("Array elements are not comparable and cannot be sorted.");
        }
    }
    @Override
    public void reverse() {
        if(elements != null) {
            Collections.reverse(elements);
        } else {
            throw new IllegalStateException("Array is null and cannot be reversed.");
        }
    }

    
    @Override
    protected void defineBuiltInMethods() {
        System.out.println("Defined array built-ins");
        methods.put("size", new JLangFunction(null, null, false) {
            @Override
            public int arity() {
                return 0; // size doesn't take any parameters
            }

            @Override
            public Object call(Interpreter interpreter, List<Object> arguments) {
                return size();
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

        methods.put("set", new JLangFunction(null, null, false) {
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

        methods.put("add", new JLangFunction(null, null, false) {
            @Override
            public int arity() { return 1; }
    
            @Override
            public Object call(Interpreter interpreter, List<Object> arguments) {
                elements.add(arguments.get(0));
                return null; // This method does not return anything.
            }
        });
    
        methods.put("insert", new JLangFunction(null, null, false) {
            @Override
            public int arity() { return 2; }
    
            @Override
            public Object call(Interpreter interpreter, List<Object> arguments) {
                int index = ((Double) arguments.get(0)).intValue();
                Object element = arguments.get(1);
                elements.add(index, element);
                return null; // This method does not return anything.
            }
        });
        
        methods.put("removeAt", new JLangFunction(null, null, false) {
            @Override
            public int arity() { return 1; }

            @Override
            public Object call(Interpreter interpreter, List<Object> arguments) {
                int index = ((Double) arguments.get(0)).intValue();
                if (index >= 0 && index < elements.size()) {
                    return elements.remove(index);
                } else {
                    throw new RuntimeError("Index out of bounds for removal.");
                }
            }
        });

        methods.put("contains", new JLangFunction(null, null, false) {
            @Override
            public int arity() { return 1; }
    
            @Override
            public Object call(Interpreter interpreter, List<Object> arguments) {
                return elements.contains(arguments.get(0));
            }
        });

        methods.put("indexOf", new JLangFunction(null, null, false) {
            @Override
            public int arity() { return 1; }
    
            @Override
            public Object call(Interpreter interpreter, List<Object> arguments) {
                return (double) elements.indexOf(arguments.get(0));
            }
        });

        methods.put("clear", new JLangFunction(null, null, false) {
            @Override
            public int arity() { return 0; }
    
            @Override
            public Object call(Interpreter interpreter, List<Object> arguments) {
                elements.clear();
                return null;
            }
        });

        methods.put("isEmpty", new JLangFunction(null, null, false) {
            @Override
            public int arity() { return 0; }
    
            @Override
            public Object call(Interpreter interpreter, List<Object> arguments) {
                return elements.isEmpty();
            }
        });

        methods.put("toArray", new JLangFunction(null, null, false) {
            @Override
            public int arity() { return 0; }
    
            @Override
            public Object call(Interpreter interpreter, List<Object> arguments) {
                return elements.toArray();
            }
        });

        methods.put("forEach", new JLangFunction(null, null, false) {
            @Override
            public int arity() { return 1; }
        
            @Override
            public Object call(Interpreter interpreter, List<Object> arguments) {
                JLangCallable action = (JLangCallable) arguments.get(0);
                
                for (Object element : elements) {
                    action.call(interpreter, List.of(element));
                }
                return null;
            }
        });

        methods.put("filter", new JLangFunction(null, null, false) {
            @Override
            public int arity() { return 1; } // filter takes a single function as an argument
        
            @Override
            public Object call(Interpreter interpreter, List<Object> arguments) {
                JLangCallable predicate = (JLangCallable) arguments.get(0);
                List<Object> filteredElements = new ArrayList<>();
                
                for (int i = 0; i < elements.size(); i++) {
                    Object element = elements.get(i);
                    boolean result;
                    // Check the number of parameters the function expects
                    if (predicate.arity() == 1) {
                        // If the function expects one parameter, pass only the element
                        result = (boolean) predicate.call(interpreter, List.of(element));
                    } else if (predicate.arity() == 2) {
                        // If the function expects two parameters, pass the index and the element
                        result = (boolean) predicate.call(interpreter, List.of((double)i, element));
                    } else {
                        throw new IllegalArgumentException("Function passed to 'map' must accept either 1 or 2 parameters.");
                    }
                    if (result) {
                        filteredElements.add(element);
                    }
                }
                return new JLangArray(filteredElements);
            }
        });

        methods.put("map", new JLangFunction(null, null, false) {
            @Override
            public int arity() { return 1; } // map takes a single function as an argument
        
            @Override
            public Object call(Interpreter interpreter, List<Object> arguments) {
                JLangCallable transform = (JLangCallable) arguments.get(0);
                List<Object> mappedElements = new ArrayList<>();
                
                for (int i = 0; i < elements.size(); i++) {
                    Object element = elements.get(i);
                    Object result;
                    // Check the number of parameters the function expects
                    if (transform.arity() == 1) {
                        // If the function expects one parameter, pass only the element
                        result = transform.call(interpreter, List.of(element));
                    } else if (transform.arity() == 2) {
                        // If the function expects two parameters, pass the index and the element
                        result = transform.call(interpreter, List.of((double)i, element));
                    } else {
                        throw new IllegalArgumentException("Function passed to 'map' must accept either 1 or 2 parameters.");
                    }
                    mappedElements.add(result);
                }
                return new JLangArray(mappedElements);
            }
        });

        methods.put("sort", new JLangFunction(null, null, false) {
            @Override
            public int arity() {
                return 0; // 'sort' can be called without a comparator for default behavior
            }
        
            @Override
            public Object call(Interpreter interpreter, List<Object> arguments) {
                // Make a copy of the elements list to sort
                List<Object> sortedElements = new ArrayList<>(elements);
                sortedElements.sort((a, b) -> {
                    // Casting or type checking should be added based on your language's capabilities
                    Comparable comparableA = (Comparable) a;
                    Comparable comparableB = (Comparable) b;
                    return comparableA.compareTo(comparableB);
                });
                return new JLangArray(sortedElements);
            }
        });
        
        methods.put("sortWithComparator", new JLangFunction(null, null, false) {
            @Override
            public int arity() {
                return 1; // 'sortWithComparator' takes one parameter
            }
        
            @Override
            public Object call(Interpreter interpreter, List<Object> arguments) {
                JLangCallable comparator = (JLangCallable) arguments.get(0);
                List<Object> sortedElements = new ArrayList<>(elements);
        
                sortedElements.sort((a, b) -> {
                    Object result = comparator.call(interpreter, List.of(a, b));
                    // Handle the case where the result is a Double
                    if (result instanceof Double) {
                        // Convert the Double to an int for comparison
                        // assuming the double value is actually within int range and intended for comparison
                        return (int)((Double) result).doubleValue();
                    } else if (result instanceof Integer) {
                        return (Integer) result;
                    } else {
                        // Throw an error or handle the case where the result is not a number
                        throw new ClassCastException("Comparator must return an Integer or a Double.");
                    }
                });
        
                return new JLangArray(sortedElements);
            }
        });
        
        methods.put("reverse", new JLangFunction(null, null, false) {
            @Override
            public int arity() {
                return 0; // 'reverse' does not take any parameters
            }
        
            @Override
            public Object call(Interpreter interpreter, List<Object> arguments) {
                List<Object> reversedElements = new ArrayList<>(elements);
                java.util.Collections.reverse(reversedElements);
                return new JLangArray(reversedElements);
            }
        });
        methods.put("reduce", new JLangFunction(null, null, false) {
            @Override
            public int arity() {
                return 2; // reduce takes two parameters: the reducer function and the initial value
            }
        
            @Override
            public Object call(Interpreter interpreter, List<Object> arguments) {
                JLangCallable reducer = (JLangCallable) arguments.get(0);
                Object accumulator = arguments.get(1);
                
                for (Object element : elements) {
                    accumulator = reducer.call(interpreter, List.of(accumulator, element));
                }
                
                return accumulator;
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
        if (index < 0) index = elements.size() + index;
        if (index >= 0 && index < elements.size()) {
            return elements.get(index);
        } else {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + elements.size());
        }
    }
    @Override
    public List<Object> asList() {
        return new ArrayList<>(elements); // Return a new list to avoid external modifications
    }
}