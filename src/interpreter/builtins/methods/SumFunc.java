package interpreter.builtins.methods;

import java.util.List;
import interpreter.Interpreter;
import interpreter.callable.JLangCallable;
import interpreter.errors.InvalidArgumentsException;

public class SumFunc implements JLangCallable {
    @Override
    public int arity() {
        return 1;
    }

    @Override
    public Object call(Interpreter interpreter, List<Object> arguments) {
        Object arg = arguments.get(0);
        if (arg instanceof List) {
            double sum = 0.0;
            for (Object item : (List<?>) arg) {
                if (item instanceof Number) {
                    sum += ((Number) item).doubleValue();
                } else {
                    throw new InvalidArgumentsException("Non-number element in sum function.");
                }
            }
            return sum;
        }
        throw new InvalidArgumentsException("Argument to sum must be a list of numbers.");
    }
}