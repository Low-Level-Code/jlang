package interpreter.errors;

import tokenizer.Token;

public class UndefinedVariableException extends RuntimeError {
    public UndefinedVariableException(Token token, String message) {
        super(token, message);
    }
}