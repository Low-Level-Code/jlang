package interpreter.builtins.methods;

import java.util.List;
import java.util.stream.Collectors;

import interpreter.Interpreter;
import interpreter.array.JLangArray;
import interpreter.callable.JLangCallable;
import interpreter.errors.InvalidArgumentsException;

public class MapFunc implements JLangCallable {
        @Override
        public int arity() {
            return 2;
        }

        @Override
        public Object call(Interpreter interpreter, List<Object> arguments) {
            Object arg = arguments.get(0);
            JLangCallable function = (JLangCallable) arguments.get(1);
            if (arg instanceof JLangArray) {
                List<?> listArg = ((JLangArray) arg).getElements();
                return listArg.stream().map(element -> function.call(interpreter, List.of(element)))
                        .collect(Collectors.toList());
            }
            throw new InvalidArgumentsException("Invalid argument type for function map.");
        }
    }