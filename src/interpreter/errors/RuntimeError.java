package interpreter.errors;

import tokenizer.Token;

public class RuntimeError extends RuntimeException {
    public final Token token;
    public RuntimeError(Token token, String message) {
        super(message);
        this.token = token;
    }
}

