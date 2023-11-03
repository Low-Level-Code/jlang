package interpreter.builtins.methods;

import java.util.List;

import interpreter.Interpreter;
import interpreter.callable.JLangCallable;
import java.util.List;
import java.util.Scanner;


public class InputFunc implements JLangCallable {
    private final Scanner scanner = new Scanner(System.in);
    
    @Override
    public int arity() {
        return 1; // 1 variable which will be the text before it gets the input
    }

    @Override
    public Object call(Interpreter interpreter, List<Object> arguments) {
        if (arguments.size() != 1) {
            throw new RuntimeException("Input function expects one argument.");
        }

        if (!(arguments.get(0) instanceof String)) {
            throw new RuntimeException("Input function expects a string as the prompt.");
        }

        // Print the prompt
        System.out.print(arguments.get(0));
        
        // Read and return the input from the user
        return scanner.nextLine();
    }
  
    // Make sure to close the scanner when you're done with it
    // Depending on your interpreter's lifecycle, you might want to add a method to clean up resources.
    public void close() {
        scanner.close();
    }
  
}