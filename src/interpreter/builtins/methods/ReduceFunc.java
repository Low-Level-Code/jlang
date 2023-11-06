package interpreter.builtins.methods;

import java.util.List;

import interpreter.Interpreter;
import interpreter.array.JLangArray;
import interpreter.callable.JLangCallable;
import interpreter.errors.InvalidArgumentsException;

// Additional utility function: reduce() which reduces a list to a single value based on a binary function
public class ReduceFunc implements JLangCallable {
    @Override
    public int arity() {
        return 3;
    }

    @Override
    public Object call(Interpreter interpreter, List<Object> arguments) {
        Object arg = arguments.get(0);
        JLangCallable binaryFunction = (JLangCallable) arguments.get(1);
        Object initialValue = arguments.get(2);
        if (arg instanceof JLangArray) {
            Object result = initialValue;
            for (Object item : ((JLangArray)arg).getElements()) {
                result = binaryFunction.call(interpreter, List.of(result, item));
            }
            return result;
        }
        throw new InvalidArgumentsException("Invalid argument type for function reduce.");
    }
}
