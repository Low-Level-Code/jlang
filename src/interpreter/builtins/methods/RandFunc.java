package interpreter.builtins.methods;

import java.util.List;
import java.util.Random;
import interpreter.Interpreter;
import interpreter.callable.JLangCallable;

public class RandFunc implements JLangCallable {
    private final Random random = new Random();

    @Override
    public int arity() {
        return 0; // No arguments needed for basic random number generation
    }

    @Override
    public Object call(Interpreter interpreter, List<Object> arguments) {
        return random.nextDouble(); // Returns a random double between 0.0 and 1.0
    }
}
