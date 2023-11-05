package interpreter.builtins.methods;

import java.util.List;
import interpreter.Interpreter;
import interpreter.callable.JLangCallable;
import interpreter.errors.InvalidArgumentsException;
import interpreter.string.JLangString;

public class LenFunc implements JLangCallable {
    @Override
    public int arity() {
        return 1;
    }

    @Override
    public Object call(Interpreter interpreter, List<Object> arguments) {
        Object arg = arguments.get(0);
        if (arg instanceof JLangString) {
            return ((JLangString) arg).getContent().length();
        } else if (arg instanceof List<?>) {
            return ((List<?>) arg).size();
        }
        throw new InvalidArgumentsException("Invalid argument type for function len.");
    }
}