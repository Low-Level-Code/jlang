package tokenizer;
public enum TokenType {
    // Single-character tokens.
    LEFT_PAREN, RIGHT_PAREN, LEFT_BRACE, RIGHT_BRACE,
    COMMA, DOT, MINUS, PLUS, SEMICOLON, COLON, SLASH, STAR, INCREMENT, DECREMENT,
    // One or two character tokens.
    BANG, BANG_EQUAL,
    QUESTION_MARK,
    EQUAL, EQUAL_EQUAL,
    GREATER, GREATER_EQUAL,
    LESS, LESS_EQUAL,
    // Literals.
    IDENTIFIER, STRING, NUMBER,
    // Errors
    TRY, CATCH,FINALLY,
    // Keywords.
    AND, CLASS, ELSE, FALSE, FUN, FOR, IF, NIL, OR,
    PRINT, RETURN, SUPER, THIS, TRUE, CONST, VAR, WHILE, BREAK, CONTINUE,
    EOF
}