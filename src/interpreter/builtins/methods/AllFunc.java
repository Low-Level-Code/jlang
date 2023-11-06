package interpreter.builtins.methods;

import java.util.List;
import java.util.function.Predicate;

import interpreter.Interpreter;
import interpreter.array.JLangArray;
import interpreter.callable.JLangCallable;
import interpreter.errors.InvalidArgumentsException;

public class AllFunc implements JLangCallable {
    @Override
    public int arity() {
        return 2;
    }

    @Override
    public Object call(Interpreter interpreter, List<Object> arguments) {
        Object arg = arguments.get(0);
        JLangCallable predicateFunction = (JLangCallable) arguments.get(1);

        if (arg instanceof JLangArray) {
            for (Object element : ((JLangArray)arg).getElements()) {
                Object result = predicateFunction.call(interpreter, List.of(element));
                if (!(result instanceof Boolean)) {
                    throw new InvalidArgumentsException("Predicate function must return a boolean value.");
                }
                if (!((Boolean) result)) {
                    return false;
                }
            }
            return true;
        }

        throw new InvalidArgumentsException("Invalid argument type for function all.");
    }
}