package interpreter.builtins.methods;

import java.util.List;
import java.util.function.Predicate;

import interpreter.Interpreter;
import interpreter.array.JLangArray;
import interpreter.callable.JLangCallable;
import interpreter.errors.InvalidArgumentsException;

public class AnyFunc implements JLangCallable {
    @Override
    public int arity() {
        return 2;
    }

    @Override
    public Object call(Interpreter interpreter, List<Object> arguments) {
        if (arguments.size() != arity()) {
            throw new InvalidArgumentsException("AnyFunc expects exactly two arguments.");
        }

        Object arg = arguments.get(0);
        Object maybePredicate = arguments.get(1);

        // Check if the second argument is a type that can act as a predicate.
        if (!(maybePredicate instanceof JLangCallable)) {
            throw new InvalidArgumentsException("Second argument must be a callable (function).");
        }

        // Cast to your language's callable type.
        JLangCallable predicate = (JLangCallable) maybePredicate;

        // Assuming a list is passed as the first argument
        if (arg instanceof JLangArray) {
            List<?> listArg = ((JLangArray)arg).getElements();
            
            // Use Java's stream API to match any element that fulfills the predicate
            // The predicate test is executed by calling the JLangCallable's call method
            return listArg.stream().anyMatch(item -> {
                List<Object> callArgs = List.of(item);
                return (Boolean) predicate.call(interpreter, callArgs);
            });
        } else {
            throw new InvalidArgumentsException("First argument must be a list.");
        }
    }
}