package interpreter.builtins.methods.math;

import java.util.List;

import interpreter.Interpreter;
import interpreter.callable.JLangCallable;

public class FloorFunc implements JLangCallable {
    @Override
    public int arity() { return 1; }

    @Override
    public Object call(Interpreter interpreter, List<Object> arguments) {
        if (arguments.size() != 1) {
            throw new RuntimeException("floor() expects exactly one argument");
        }
        return JLangMath.floor((Double)arguments.get(0));
    }
}   