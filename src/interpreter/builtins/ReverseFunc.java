package interpreter.builtins;

import java.util.List;

import interpreter.Interpreter;
import interpreter.callable.JLangCallable;
import interpreter.errors.InvalidArgumentsException;
import interpreter.indexible.JLangIndexible;

public class ReverseFunc implements JLangCallable {
    @Override
    public int arity() {
        return 1;
    }

    @Override
    public Object call(Interpreter interpreter, List<Object> arguments) {
        Object arg = arguments.get(0);
        if (arg instanceof JLangIndexible) {
            ((JLangIndexible) arg).reverse();
        } else {
            throw new InvalidArgumentsException("Argument must be a JLangArray or JLangString.");
        }
        return null; // since we're modifying in place, we don't need to return anything
    }
}