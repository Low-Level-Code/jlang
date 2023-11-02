package interpreter.builtins.methods;

import java.util.List;
import interpreter.Interpreter;
import interpreter.callable.JLangCallable;

public class TypeOfFunc implements JLangCallable {
    @Override
    public int arity() {
        return 1;
    }

    @Override
    public Object call(Interpreter interpreter, List<Object> arguments) {
        Object arg = arguments.get(0);
        return arg.getClass().getSimpleName();
    }
}