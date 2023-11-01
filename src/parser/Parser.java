package parser;

import java.util.ArrayList;
import java.util.List;

import ast.Expr;
import main.JLang;

import static tokenizer.TokenType.*;
import tokenizer.*;

public class Parser {
    private static class ParseError extends RuntimeException {}
    private final List < Token > tokens;
    private int current = 0;
    public Parser(List < Token > tokens) {
        this.tokens = tokens;
    }
    
    private boolean isAtEnd() {
        return peek().type == EOF;
    }
    
    private Token peek() {
        return tokens.get(current);
    }
    
    private Token previous() {
        return tokens.get(current - 1);
    }
        
    private Token advance() {
        if (!isAtEnd()) current++;
        return previous();
    }
        
    private boolean check(TokenType type) {
        if (isAtEnd()) return false;
        return peek().type == type;
    }
        
    private boolean match(TokenType... types) {
        for (TokenType type : types) {
            if (check(type)) {
                advance();
                return true;
            }
        }
        return false;
    }
    

        
    private ParseError error(Token token, String message) {
        JLang.error(token, message);
        return new ParseError();
    }
        

    private Token consume(TokenType type, String message) {
        if (check(type)) return advance();
        throw error(peek(), message);
    }

    private Expr primary() {
        if (match(LEFT_BRACE)) return block(); // Block implementation
        if (match(FALSE)) return new Expr.Literal(false);
        if (match(TRUE)) return new Expr.Literal(true);
        if (match(NIL)) return new Expr.Literal(null);
        if (match(NUMBER, STRING)) {
           return new Expr.Literal(previous().literal);
        }
        if (match(LEFT_PAREN)) {
            Expr expr = expression();
            consume(RIGHT_PAREN, "Expect ')' after expression.");
            return new Expr.Grouping(expr);
        }
        throw error(peek(), "Expect expression.");
    }
 
    private Expr block() {
        List<Expr> statements = new ArrayList<>();
        while (!check(RIGHT_BRACE) && !isAtEnd()) {
            statements.add(expression());
            // Consume semicolons or other delimiters as needed
        }
        consume(RIGHT_BRACE, "Expect '}' after block.");
        return new Expr.Block(statements);
    }

    private Expr unary() {
        if (match(BANG, MINUS)) {
            Token operator = previous();
            Expr right = unary();
            return new Expr.Unary(operator, right);
        }
        return primary();
    }

    private Expr factor() {
        Expr expr = unary();
        while (match(SLASH, STAR)) {
            Token operator = previous();
            Expr right = unary();
            expr = new Expr.Binary(expr, operator, right);
        }
        return expr;
    }

    private Expr term() {
        Expr expr = factor();
        while (match(MINUS, PLUS)) {
            Token operator = previous();
            Expr right = factor();
            expr = new Expr.Binary(expr, operator, right);
        }
        return expr;
    }
        
    private Expr comparison() {
        Expr expr = term();
        while (match(GREATER, GREATER_EQUAL, LESS, LESS_EQUAL)) {
            Token operator = previous();
            Expr right = term();
            expr = new Expr.Binary(expr, operator, right);
        }
        return expr;
    }
        
    private Expr equality() {
        Expr expr = comparison();
        while (match(BANG_EQUAL, EQUAL_EQUAL)) {
        Token operator = previous();
        Expr right = comparison();
        expr = new Expr.Binary(expr, operator, right);
        }
        return expr;
    }
        
    private Expr expression() {
        // support for comma seperated statements
        return comma();
    }

    private Expr comma() {
        Expr expr = ternary();
        while (match(COMMA)) {
            Token operator = previous();
            Expr right = ternary();
            expr = new Expr.Comma(expr, right);
        }
        return expr;
    }

    private Expr ternary() {
        Expr expr = equality();
        if (match(QUESTION_MARK)) {
            Expr thenExpr = expression(); // or equality(), depending on what you want to allow
            consume(COLON, "Expect ':' after expression in ternary operation.");
            Expr elseExpr = ternary();
            expr = new Expr.Ternary(expr, thenExpr, elseExpr);
        }
        return expr;
    }

    private void synchronize() {
        advance();
        while (!isAtEnd()) {
            if (previous().type == SEMICOLON) return;
            switch (peek().type) {
                case CLASS:
                case FUN:
                case VAR:
                case FOR:
                case IF:
                case WHILE:
                case PRINT:
                case RETURN:
                return;
            }
            advance();
        }
    }
    public Expr parse() {
        try {
            return expression();
        } catch (ParseError error) {
            return null;
        }
    }
        
        
        
}
