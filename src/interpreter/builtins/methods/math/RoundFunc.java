package interpreter.builtins.methods.math;

import java.util.List;
import interpreter.Interpreter;
import interpreter.callable.JLangCallable;
import interpreter.errors.InvalidArgumentsException;

public class RoundFunc implements JLangCallable {
    @Override
    public int arity() {
        return 1;
    }

    @Override
    public Object call(Interpreter interpreter, List<Object> arguments) {
        if (arguments.get(0) instanceof Double) {
            return Math.round((Double) arguments.get(0));
        }
        throw new InvalidArgumentsException("Argument to round must be a double.");
    }
}