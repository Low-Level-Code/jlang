package interpreter.builtins.methods;

import java.util.List;

import interpreter.Interpreter;
import interpreter.callable.JLangCallable;
import interpreter.errors.InvalidArgumentsException;
import tokenizer.Token;

public class MinFunc implements JLangCallable {

    @Override
    public int arity() {
        return 2;
    }

    @Override
    public Object call(Interpreter interpreter, List<Object> arguments) {
        Object arg1 = arguments.get(0);
        Object arg2 = arguments.get(1);
        // Handle String type
        if (arg1 instanceof String && arg2 instanceof String) {
            String str1 = (String) arg1;
            String str2 = (String) arg2;
            return str1.compareTo(str2) < 0 ? str1 : str2;
        }
        // Handle Double type
        if (arg1 instanceof Double && arg2 instanceof Double) {
            Double int1 = (Double) arg1;
            Double int2 = (Double) arg2;
            return int1 < int2 ? int1 : int2;
        }
        // If types are not what we expect, throw an error or return null
        throw new InvalidArgumentsException("Invalid argument types for function min.");
    }
    
}
