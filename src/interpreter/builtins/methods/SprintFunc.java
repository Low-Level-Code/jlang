package interpreter.builtins.methods;
import java.util.List;
import java.util.MissingFormatArgumentException;

import interpreter.Interpreter;
import interpreter.callable.JLangCallable;
import interpreter.errors.RuntimeError;
import main.JLang;
import scanner.Scanner;

public class SprintFunc implements JLangCallable {
    
    @Override
    public int arity() {
        // The arity is variable because printf can take multiple arguments
        return -1;
    }

    @Override
    public Object call(Interpreter interpreter, List<Object> arguments) {
        if (arguments.isEmpty()) {
            throw new RuntimeException("sprint function expects at least one argument.");
        }

        if (!(arguments.get(0) instanceof String)) {
            throw new RuntimeException("sprint function expects a string as the first argument for the format.");
        }

        // Extract the format string
        String format = (String) arguments.get(0);
        
        // Remove the first argument (format string) so that we are left with just the values to format
        arguments.remove(0);

        // Prepare an array for the values to format, which will be passed to the String.format method
        Object[] formatValues = arguments.toArray(new Object[0]);
        
        // Return the formatted string
        try {
            return String.format(format, formatValues);
        } catch (MissingFormatArgumentException e) {
            throw new RuntimeException("Formating elements don't match");
        }
    }
}