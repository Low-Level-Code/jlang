package interpreter.builtins.methods;

import java.util.List;

import interpreter.Interpreter;
import interpreter.array.JLangArray;
import interpreter.callable.JLangCallable;
import interpreter.errors.InvalidArgumentsException;
import interpreter.klass.JLangClass;

public class MethodsFunc implements JLangCallable{
    @Override
    public int arity() {
        return 1;
    }
    @Override
    public Object call(Interpreter interpreter, List<Object> arguments) {
        Object arg = arguments.get(0);
        if (arg instanceof JLangClass){
            return ((JLangClass)arg).getMethods();
        }
        throw new InvalidArgumentsException("methods function should take a class");
    }
}
