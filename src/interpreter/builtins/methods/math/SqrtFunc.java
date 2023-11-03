package interpreter.builtins.methods.math;

import java.util.List;
import interpreter.Interpreter;
import interpreter.callable.JLangCallable;
import interpreter.errors.InvalidArgumentsException;

public class SqrtFunc implements JLangCallable {
    @Override
    public int arity() {
        return 1;
    }

    @Override
    public Object call(Interpreter interpreter, List<Object> arguments) {
        if (arguments.get(0) instanceof Double) {
            return Math.sqrt((Double) arguments.get(0));
        }
        throw new InvalidArgumentsException("Argument to sqrt must be a double.");
    }
}