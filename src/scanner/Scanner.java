package scanner;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import main.JLang;
import tokenizer.Token;
import tokenizer.TokenType;

import static tokenizer.TokenType.*;

public class Scanner {
    private final String source;
    private final List < Token > tokens = new ArrayList < > ();
    private int start = 0;
    private int current = 0;
    private static int line = 1;
    private static final Map<String, TokenType> keywords;
    static {
        keywords = new HashMap<>();
        keywords.put("and", AND);
        keywords.put("class", CLASS);
        keywords.put("else", ELSE);
        keywords.put("false", FALSE);
        keywords.put("for", FOR);
        keywords.put("fun", FUN);
        keywords.put("if", IF);
        keywords.put("nil", NIL);
        keywords.put("or", OR);
        keywords.put("print", PRINT);
        keywords.put("return", RETURN);
        keywords.put("super", SUPER);
        keywords.put("this", THIS);
        keywords.put("try", TRY);
        keywords.put("catch", CATCH);
        keywords.put("finally", FINALLY);
        keywords.put("true", TRUE);
        keywords.put("const", CONST);
        keywords.put("var", VAR);
        keywords.put("while", WHILE);
        keywords.put("break", BREAK);
        keywords.put("continue", CONTINUE);
    }


    public Scanner(String source) {
        this.source = source;
    }
    public static int getLine() { return line; }
    private boolean isAtEnd() {
        return current >= source.length();
    }
    private char advance() {
        return source.charAt(current++);
    }
    private void addToken(TokenType type) {
        addToken(type, null);
    }
    private void addToken(TokenType type, Object literal) {
        String text = source.substring(start, current);
        tokens.add(new Token(type, text, literal, line));
    }
    private boolean match(char expected) {
        if (isAtEnd()) return false;
        if (source.charAt(current) != expected) return false;
        current++;
        return true;
    }
    private char peek() {
        if (isAtEnd()) return '\0';
        return source.charAt(current);
    }
    private void string() {
        while (peek() != '"' && !isAtEnd()) {
            if (peek() == '\n') line++;
            advance();
        }
        if (isAtEnd()) {
            JLang.error(line, "Unterminated string.");
            return;
        }
        // The closing ".
        advance();
        // Trim the surrounding quotes.
        String value = source.substring(start + 1, current - 1);
        addToken(STRING, value);
    }
    private boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }
    private char peekNext() {
        if (current + 1 >= source.length()) return '\0';
        return source.charAt(current + 1);
    }
        
    private void number() {
        while (isDigit(peek())) advance();
        // Look for a fractional part.
        if (peek() == '.' && isDigit(peekNext())) {
            // Consume the "."
            advance();
            while (isDigit(peek())) advance();
        }
        addToken(NUMBER,
        Double.parseDouble(source.substring(start, current)));
    }
    private boolean isAlpha(char c) {
        return (c >= 'a' && c <= 'z') ||
        (c >= 'A' && c <= 'Z') ||
        c == '_';
    }
    private boolean isAlphaNumeric(char c) {
        return isAlpha(c) || isDigit(c);
    }
    private void identifier() {
        while (isAlphaNumeric(peek())) advance();
        String text = source.substring(start, current);
        TokenType type = keywords.get(text);
        if (type == null) type = IDENTIFIER;
        addToken(type);
    }
        
        
    private void scanToken() {
        char c = advance();
        switch (c) {
            case '(':addToken(LEFT_PAREN);break;
            case ')':addToken(RIGHT_PAREN);break;
            case '{':addToken(LEFT_BRACE);break;
            case '}':addToken(RIGHT_BRACE);break;
            case ',':addToken(COMMA);break;
            case '.':addToken(DOT);break;
            case '-':addToken(match('-') ? DECREMENT : (match('=') ? MINUS_EQUAL : MINUS));break;
            case '+':addToken(match('+') ? INCREMENT : (match('=') ? PLUS_EQUAL : PLUS ));break;
            case ';':addToken(SEMICOLON);break;
            case '*':addToken(match('=') ? STAR_EQUAL:STAR);break;
            case '!':addToken(match('=') ? BANG_EQUAL : BANG);break;
            case '?':addToken(QUESTION_MARK);break; // fot the tenary operation
            case ':':addToken(COLON);break; // fot the tenary operation
            case '=':addToken(match('=') ? EQUAL_EQUAL : EQUAL);break;
            case '<':addToken(match('=') ? LESS_EQUAL : (match('<') ? (match('=') ? SHIFT_LEFT_EQUAL : SHIFT_LEFT) : LESS));break;
            case '>':addToken(match('=') ? GREATER_EQUAL : (match('>') ? (match('=') ? SHIFT_RIGHT_EQUAL: SHIFT_RIGHT) : GREATER));break;
            case '/':
                if (match('/')) {
                    // A comment goes until the end of the line.
                    while (peek() != '\n' && !isAtEnd()) advance();
                } else {
                    addToken(match('=') ? SLASH_EQUAL : SLASH);
                }
                break;
            case ' ':
            case '\r':
            case '\t':
                // Ignore whitespace.
                break;
            case '\n':
                line++;
                break;
            case '"': string(); break;
            default:
                if (isDigit(c)) {
                    number();
                } else if (isAlpha(c)) {
                    identifier();
                } else {
                    JLang.error(line, "Unexpected character.");
                }
                
                break;

        }
    }

    public List < Token > scanTokens() {
        while (!isAtEnd()) {
            // We are at the beginning of the next lexeme.
            start = current;
            scanToken();
        }
        tokens.add(new Token(EOF, "", null, line));
        return tokens;
    }
}