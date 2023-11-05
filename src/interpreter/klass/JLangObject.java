package interpreter.klass;

import tokenizer.Token;

public interface JLangObject {
    Object get(Token name);
}
