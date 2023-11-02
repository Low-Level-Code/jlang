package interpreter.builtins.methods;

import java.util.List;
import interpreter.Interpreter;
import interpreter.callable.JLangCallable;
import interpreter.errors.InvalidArgumentsException;

public class AbsFunc implements JLangCallable {
    @Override
    public int arity() {
        return 1;
    }

    @Override
    public Object call(Interpreter interpreter, List<Object> arguments) {
        Object arg = arguments.get(0);
        if (arg instanceof Double) {
            return Math.abs((Double) arg);
        } else if (arg instanceof Integer) {
            return Math.abs((Integer) arg);
        }
        throw new InvalidArgumentsException("Invalid argument type for function abs.");
    }
}