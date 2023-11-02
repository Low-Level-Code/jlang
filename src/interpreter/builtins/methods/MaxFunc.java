package interpreter.builtins.methods;

import java.util.List;

import interpreter.Interpreter;
import interpreter.callable.JLangCallable;
import interpreter.errors.InvalidArgumentsException;
import tokenizer.Token;

public class MaxFunc implements JLangCallable {

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
            return str1.compareTo(str2) > 0 ? str1 : str2;
        }
        // Handle Integer type
        if (arg1 instanceof Integer && arg2 instanceof Integer) {
            Integer int1 = (Integer) arg1;
            Integer int2 = (Integer) arg2;
            return int1 > int2 ? int1 : int2;
        }
        // If types are not what we expect, throw an error or return null
        throw new InvalidArgumentsException("Invalid argument types for function min.");
    }
    
}
