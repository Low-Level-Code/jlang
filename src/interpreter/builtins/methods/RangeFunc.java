package interpreter.builtins.methods;

import java.util.ArrayList;
import java.util.List;
import interpreter.Interpreter;
import interpreter.callable.JLangCallable;
import interpreter.errors.InvalidArgumentsException;

public class RangeFunc implements JLangCallable {
    @Override
    public int arity() {
        return 2;
    }

    @Override
    public Object call(Interpreter interpreter, List<Object> arguments) {
        if (arguments.get(0) instanceof Double && arguments.get(1) instanceof Double) {
            int start = (int) ((Double) arguments.get(0)).doubleValue();
            int end = (int) ((Double) arguments.get(1)).doubleValue();
            List<Integer> range = new ArrayList<>();
            for (int i = start; i < end; i++) {
                range.add(i);
            }
            return range;
        }
        throw new InvalidArgumentsException("Invalid argument types for function range.");
    }
}