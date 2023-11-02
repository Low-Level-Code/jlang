package interpreter.builtins.methods;

import java.util.List;
import interpreter.Interpreter;
import interpreter.callable.JLangCallable;
import parser.Parser;

public class PrintFunc implements JLangCallable {
    @Override
    public int arity() {
        return -1; // variable number of arguments
    }

    @Override
    public Object call(Interpreter interpreter, List<Object> arguments) {
        arguments.forEach(arg -> System.out.print(arg + " "));
        System.out.println();
        return null;
    }
}