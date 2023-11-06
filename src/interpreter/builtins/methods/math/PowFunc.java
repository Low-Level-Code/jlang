package interpreter.builtins.methods.math;


import java.util.List;

import interpreter.Interpreter;
import interpreter.callable.JLangCallable;
import interpreter.errors.InvalidArgumentsException;

public class PowFunc implements JLangCallable {
    @Override
    public int arity() {
        return 2;
    }

    @Override
    public Object call(Interpreter interpreter, List<Object> arguments) {
        if (arguments.get(0) instanceof Double && arguments.get(1) instanceof Double) {
            return Math.pow((Double) arguments.get(0), (Double) arguments.get(1));
        }
        throw new InvalidArgumentsException("pow function expects two numeric arguments.");
    }
}

