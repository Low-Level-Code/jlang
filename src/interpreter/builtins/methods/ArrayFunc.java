package interpreter.builtins.methods;

import java.util.ArrayList;
import java.util.List;

import interpreter.Interpreter;
import interpreter.array.JLangArray;
import interpreter.callable.JLangCallable;
import interpreter.errors.InvalidArgumentsException;
import interpreter.indexible.JLangIndexible;

public class ArrayFunc implements JLangCallable {
        @Override
        public int arity() {
            return 1;
        }

        @Override
        public Object call(Interpreter interpreter, List<Object> arguments) {
            Object arg = arguments.get(0);
            if (arg instanceof JLangIndexible) {
                return new JLangArray(((JLangIndexible) arg).asList());
            }
            throw new InvalidArgumentsException("Argument must be of type JLangIndexable for function array.");
        }
    }