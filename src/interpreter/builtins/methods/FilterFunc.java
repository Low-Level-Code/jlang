package interpreter.builtins.methods;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import interpreter.Interpreter;
import interpreter.array.JLangArray;
import interpreter.callable.JLangCallable;
import interpreter.errors.InvalidArgumentsException;

// Additional utility function: filter() which filters elements based on a predicate
public class FilterFunc implements JLangCallable {
    @Override   
    public int arity() {
        return 2;
    }
    
@Override
    public Object call(Interpreter interpreter, List<Object> arguments) {
        Object arg = arguments.get(0);
        Object rawPredicate = arguments.get(1);

        if (arg instanceof JLangArray && rawPredicate instanceof JLangCallable) {
            JLangCallable callable = (JLangCallable) rawPredicate;
            List<Object> array = ((JLangArray) arg).getElements();
            
            // Use a lambda to adapt the JLangCallable to the Predicate interface
            Predicate<Object> predicate = item -> {
                List<Object> callableArgs = new ArrayList<>();
                callableArgs.add(item);
                // Assume that JLangCallable#call returns a boolean value
                return (Boolean) callable.call(interpreter, callableArgs);
            };
            
            return array.stream().filter(predicate).collect(Collectors.toList());
        }
        
        throw new InvalidArgumentsException("Invalid argument type for function filter.");
    }
}