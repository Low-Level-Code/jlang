package interpreter.errors;

import tokenizer.Token;

public class DivisionByZeroException extends RuntimeError {
    public DivisionByZeroException(Token token, String message) {
        super(token, message);
    }
}