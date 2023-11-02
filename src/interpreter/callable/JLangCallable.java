package interpreter.callable;
import java.util.List;
import interpreter.Interpreter;

public interface JLangCallable {
    int arity();
    Object call(Interpreter interpreter, List<Object> arguments);
}

