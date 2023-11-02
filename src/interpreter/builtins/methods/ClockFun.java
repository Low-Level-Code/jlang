package interpreter.builtins.methods;

import java.util.List;

import interpreter.Interpreter;
import interpreter.callable.JLangCallable;

public class ClockFun implements JLangCallable {

    @Override
    public int arity() { return 0; }

    @Override
    public Object call(Interpreter interpreter, List<Object> arguments) {
        return (double)System.currentTimeMillis() / 1000.0;
    }
    @Override
    public String toString() { return "<clock native fn>"; }
    
}
