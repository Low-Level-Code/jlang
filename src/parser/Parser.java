package parser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ast.Expr;
import ast.Stmt;
import ast.Stmt.Catch;
import main.JLang;

import static tokenizer.TokenType.*;
import tokenizer.*;

public class Parser {
    private static class ParseError extends RuntimeException {}
    private final List < Token > tokens;
    private int current = 0;
    public static int FUNC_ARG_LIMIT = 255;
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
    private Expr array() {
        List<Expr> elements = new ArrayList<>();
        if (!check(RIGHT_BRACKET)) {
            do {
                elements.add(expression());
            } while (match(COMMA));
        }
        consume(RIGHT_BRACKET, "Expect ']' after array elements.");
        return new Expr.Array(elements);
    }
    private Expr arrayAccess() {
        Expr expr = primary();
    
        while (true) {
            if (match(LEFT_BRACKET)) {
                expr = finishArrayAccess(expr);
            } else {
                break;
            }
        }
    
        return expr;
    }
    private Expr finishArrayAccess(Expr array) {
        Expr index = expression();
        consume(RIGHT_BRACKET, "Expect ']' after array index.");
        return new Expr.ArrayAccess(array, index);
    }
    private Expr primary() {
        // if (match(LEFT_BRACE)) return block(); // Block implementation
        if (match(FUN)) {
            return lambdafunction("function");
        }
        if (match(FALSE)) return new Expr.Literal(false);
        if (match(TRUE)) return new Expr.Literal(true);
        if (match(NIL)) return new Expr.Literal(null);
        if (match(NUMBER, STRING)) {
           return new Expr.Literal(previous().literal);
        }
        if (match(SUPER)) {
            Token keyword = previous();
            consume(DOT, "Expect '.' after 'super'.");
            Token method = consume(IDENTIFIER,
            "Expect superclass method name.");
            return new Expr.Super(keyword, method);
        }
            
        if (match(THIS)) return new Expr.This(previous());
        if (match(IDENTIFIER)) {
            return new Expr.Variable(previous());
        }   
        if (match(LEFT_BRACKET)) return array();
        if (match(LEFT_PAREN)) {
            Expr expr = expression();
            consume(RIGHT_PAREN, "Expect ')' after expression.");
            return new Expr.Grouping(expr);
        }
        throw error(peek(), "Expect expression.");
    }
  

    private Expr finishCall(Expr callee) {
        List<Expr> arguments = new ArrayList<>();
        if (!check(RIGHT_PAREN)) {
            do {
                if (arguments.size() >= FUNC_ARG_LIMIT) {
                    error(peek(), "Can't have more than "+FUNC_ARG_LIMIT+" arguments.");
                }   
                arguments.add(expression());
            } while (match(COMMA));
        }
        Token paren = consume(RIGHT_PAREN, "Expect ')' after arguments.");
        return new Expr.Call(callee, paren, arguments);
    }

    private Expr call() {
        Expr expr = primary();
        while (true) {
            if (match(LEFT_PAREN)) { // this should make sure that we have nested calls to the functions
                expr = finishCall(expr);
            }  else if (match(DOT)) {
                Token name = consume(IDENTIFIER, "Expect property name after '.'.");
                expr = new Expr.Get(expr, name);
            } else if (match(LEFT_BRACKET)) {
                expr = finishArrayAccess(expr);
                // Presuming finishArrayAccess() consumes the index and the RIGHT_BRACKET.
            } else {
                break;
            }
        }
        return expr;
    }

    private Expr postfix() {
        Expr expr = call();
        while (match(INCREMENT, DECREMENT)) {
            Token operator = previous();
            expr = new Expr.Postfix(expr, operator);
        }
        return expr;
    }
    private Expr unary() {
        if (match(INCREMENT, DECREMENT)) {
            Token operator = previous();
            Expr right = unary();
            return new Expr.Unary(operator, right); // 'true' if your Expr.Unary supports postfix
        }
        if (match(BANG, MINUS)) {
            Token operator = previous();
            Expr right = unary();
            return new Expr.Unary(operator, right);
        }
        return postfix();
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
        while (match(MINUS, PLUS, SHIFT_LEFT, SHIFT_RIGHT)) {
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
        // return comma();
        return assignment();

    }

    private Expr and() {
        // Expr expr = equality(); 
        Expr expr = ternary(); // to allow tenary operator 
        while (match(AND)) {
            Token operator = previous();
            Expr right = equality();
            expr = new Expr.Logical(expr, operator, right);
        }
        return expr;
    }

    private Expr or() {
        Expr expr = and();
        while (match(OR)) {
            Token operator = previous();
            Expr right = and();
           expr = new Expr.Logical(expr, operator, right);
        }
        return expr;
    }
        
    private Expr assignment() {
        Expr expr = or();
        if (match(EQUAL, PLUS_EQUAL, MINUS_EQUAL, STAR_EQUAL, SLASH_EQUAL, SHIFT_LEFT_EQUAL, SHIFT_RIGHT_EQUAL)) {
        Token operator = previous();
        Expr value = assignment();

        if (expr instanceof Expr.Variable) {
            Token name = ((Expr.Variable)expr).name;
            if (operator.type != TokenType.EQUAL) {
                // For compound operators, we read the current value of the variable and apply the operator.
                expr = new Expr.Binary(new Expr.Variable(name), operator, value);
            }
            return new Expr.Assign(name, value, operator);
        } else if (expr instanceof Expr.Get) {
            Expr.Get get = (Expr.Get)expr;
            if (operator.type != TokenType.EQUAL) {
                value = new Expr.Binary(new Expr.Get(get.object, get.name), operator, value);
            }
            return new Expr.Set(get.object, get.name, value);
        }
        error(operator, "Invalid assignment target.");
    }
    return expr;
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
                case CONST:
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
    private Stmt expressionStatement() {
        Expr expr = expression();
        consume(SEMICOLON, "Expect ';' after expression.");
        return new Stmt.Expression(expr);
    }
        
    private Stmt printStatement() {
        Expr value = expression();
        consume(SEMICOLON, "Expect ';' after value.");
        return new Stmt.Print(value);
    }
        
    private List<Stmt> block() {
        List<Stmt> statements = new ArrayList<>();
        while (!check(RIGHT_BRACE) && !isAtEnd()) {
           statements.add(declaration());
        }
        consume(RIGHT_BRACE, "Expect '}' after block.");
        return statements;
    }
    private Stmt ifStatement() {
        consume(LEFT_PAREN, "Expect '(' after 'if'.");
        Expr condition = expression();
        consume(RIGHT_PAREN, "Expect ')' after if condition.");
        Stmt thenBranch = statement();
        Stmt elseBranch = null;
        if (match(ELSE)) {
           elseBranch = statement();
        }
        return new Stmt.If(condition, thenBranch, elseBranch);
    }
        
    private Stmt whileStatement() {
        consume(LEFT_PAREN, "Expect '(' after 'while'.");
        Expr condition = expression();
        consume(RIGHT_PAREN, "Expect ')' after condition.");
        Stmt body = statement();
        return new Stmt.While(condition, body);
    }
    private Stmt forStatement() {
        consume(LEFT_PAREN, "Expect '(' after 'for'.");
        Stmt initializer;
        if (match(SEMICOLON)) {
            initializer = null;
        } else if (match(VAR)) {
            initializer = varDeclaration();
        } else {
            initializer = expressionStatement();
        }
        Expr condition = null;
        if (!check(SEMICOLON)) {
            condition = expression();
        }
        consume(SEMICOLON, "Expect ';' after loop condition.");
        Expr increment = null;
        if (!check(RIGHT_PAREN)) {
            increment = expression();
        }
        consume(RIGHT_PAREN, "Expect ')' after for clauses.");
        Stmt body = statement();
        if (increment != null) { // basicallt we'll execute the increment as the last expression in a while loop
            body = new Stmt.Block(
            Arrays.asList(
                body,
                new Stmt.Expression(increment)
                ));
        }
        if (condition == null) condition = new Expr.Literal(true);
        body = new Stmt.While(condition, body); // converting the forloop body to an actual while loop with tht condition
        if (initializer != null) { // the initializer should be added as a single expression in the top of the loop
            body = new Stmt.Block(Arrays.asList(initializer, body));
        }
            
        return body;

    }
    private Stmt breakStatement() {
        Token keyword = previous();
        consume(SEMICOLON, "Expect ';' after 'break'.");
        return new Stmt.Break(keyword);
    } 
    private Stmt continueStatement() {
        Token keyword = previous();
        consume(SEMICOLON, "Expect ';' after 'break'.");
        return new Stmt.Continue(keyword);
    } 
    private Stmt tryCatchStatement() {
        Stmt tryBlock = statement();
        Stmt finallyBlock = null;
        List<Catch> catchBlocks = new ArrayList<>();
        while (match(CATCH)) {
            consume(LEFT_PAREN, "Expect '(' after 'catch'.");
            Token exceptionType = consume(IDENTIFIER, "Expect exception type.");
            Token variable = consume(IDENTIFIER, "Expect exception variable name.");
            consume(RIGHT_PAREN, "Expect ')' after catch arguments.");
            Stmt catchBlock = statement();
            catchBlocks.add(new Stmt.Catch(exceptionType, variable, catchBlock));
        }
        if(match(FINALLY)){
            finallyBlock = statement();
        }
    
        return new Stmt.TryCatch(tryBlock, catchBlocks, finallyBlock);
    }
    private Stmt returnStatement() {
        Token keyword = previous();
        Expr value = null;
        if (!check(SEMICOLON)) {
        value = expression();
        }
        consume(SEMICOLON, "Expect ';' after return value.");
        return new Stmt.Return(keyword, value);
    }

    private Stmt statement() {
        if (match(TRY)) return tryCatchStatement(); // break statement yaaay
        if (match(BREAK)) return breakStatement(); // break statement yaaay
        if (match(CONTINUE)) return continueStatement(); // continue still has an embarrassing bug
        if (match(IF)) return ifStatement();
        if (match(PRINT)) return printStatement();
        if (match(RETURN)) return returnStatement();
        if (match(WHILE)) return whileStatement();
        if (match(FOR)) return forStatement();
        if (match(LEFT_BRACE)) return new Stmt.Block(block());
        return expressionStatement();
    }

    private Stmt varDeclaration() {
        Token name = consume(IDENTIFIER, "Expect variable name.");
        Expr initializer = null;
        if (match(EQUAL)) {
           initializer = expression();
        }
        consume(SEMICOLON, "Expect ';' after variable declaration.");
        return new Stmt.Var(name, initializer);
    }

    private Stmt constDeclaration() {
        Token name = consume(IDENTIFIER, "Expect constant name.");
        Expr initializer = null;
        if (match(EQUAL)) {
           initializer = expression();
        }
        consume(SEMICOLON, "Expect ';' after constant declaration.");
        return new Stmt.Const(name, initializer);
    }

    private Expr.LambdaFunction lambdafunction(String kind) {
        consume(LEFT_PAREN, "Expect '(' after " + kind + ".");
        List<Token> parameters = new ArrayList<>();
        if (!check(RIGHT_PAREN)) {
            do {
                if (parameters.size() >= FUNC_ARG_LIMIT) {
                    error(peek(), "Can't have more than " + FUNC_ARG_LIMIT + " parameters.");
                }
                parameters.add(consume(IDENTIFIER, "Expect parameter name."));
            } while (match(COMMA));
        }
        consume(RIGHT_PAREN, "Expect ')' after parameters.");
    
        consume(LEFT_BRACE, "Expect '{' before " + kind + " body.");
        List<Stmt> body = block();
        return new Expr.LambdaFunction(new Token(TokenType.FUN, "anonymous",null, 0), parameters, body);
    }

    private Stmt.Function function(String kind) {
        Token name = consume(IDENTIFIER, "Expect " + kind + " name.");
        consume(LEFT_PAREN, "Expect '(' after " + kind + " name.");
        List<Token> parameters = new ArrayList<>();
        if (!check(RIGHT_PAREN)) {
            do {
                if (parameters.size() >= FUNC_ARG_LIMIT) {
                    error(peek(), "Can't have more than 255 parameters.");
                }
                parameters.add(consume(IDENTIFIER, "Expect parameter name."));
            } while (match(COMMA));
        }
        consume(RIGHT_PAREN, "Expect ')' after parameters.");
        consume(LEFT_BRACE, "Expect '{' before " + kind + " body.");
        List<Stmt> body = block();
        return new Stmt.Function(name, parameters, body);
    }
    private Stmt classDeclaration() {
        Token name = consume(IDENTIFIER, "Expect class name.");
        Expr.Variable superclass = null;
        if (match(LESS)) {
            consume(IDENTIFIER, "Expect superclass name.");
            superclass = new Expr.Variable(previous());
        }
        consume(LEFT_BRACE, "Expect '{' before class body.");
        List<Stmt.Function> methods = new ArrayList<>();
        while (!check(RIGHT_BRACE) && !isAtEnd()) {
            methods.add(function("method"));
        }
        consume(RIGHT_BRACE, "Expect '}' after class body.");
        return new Stmt.Class(name, superclass, methods);
    }
        
    private Stmt declaration() {
        try {
            if (match(CLASS)) return classDeclaration();
            if (match(FUN)) return function("function");
            if (match(VAR)) return varDeclaration();
            if (match(CONST)) return constDeclaration();
            return statement();
        } catch (ParseError error) {
            synchronize();
            return null;
        }
    }
        
    public List<Stmt> parse() {
        List<Stmt> statements = new ArrayList<>();
        while (!isAtEnd()) {
            statements.add(declaration());
        }
        return statements;
    }
    
    public Object parseLine() {
        try {
            // Try to parse as a statement
            if (check(EOF)) {
                return null; // Only whitespace or comment
            }
            return declaration();
        } catch (ParseError error) {
            // If it fails, revert to the starting point and try as expression
            current = 0;
            Expr expression = expression();
            consume(EOF, "Expect end of expression");
            return expression;
        }
    }
        
}
