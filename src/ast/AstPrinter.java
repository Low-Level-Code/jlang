package ast;

import ast.Expr.Block;
import ast.Expr.Comma;
import ast.Expr.Ternary;
import tokenizer.Token;
import tokenizer.TokenType;

public class AstPrinter implements Expr.Visitor<String> {
    private String parenthesize(String name, Expr... exprs) {
        StringBuilder builder = new StringBuilder();
        builder.append("(").append(name);
        for (Expr expr : exprs) {
            builder.append(" ");
            builder.append(expr.accept(this));
        }
        builder.append(")");
        return builder.toString();
    }
        
    public String print(Expr expr) {
        return expr.accept(this);
    }
    @Override
    public String visitBinaryExpr(Expr.Binary expr) {
        return parenthesize(expr.operator.lexeme, expr.left, expr.right);
    }
    @Override
    public String visitGroupingExpr(Expr.Grouping expr) {
        return parenthesize("group", expr.expression);
    }
    @Override
    public String visitLiteralExpr(Expr.Literal expr) {
        if (expr.value == null) return "nil";
        return expr.value.toString();
    }
    @Override
    public String visitUnaryExpr(Expr.Unary expr) {
        return parenthesize(expr.operator.lexeme, expr.right);
    }
    @Override
    public String visitBlockExpr(Block expr) {
        String rv = "";
        rv += "block {\n";
        for (Expr statement : expr.statements) {
            rv += statement.accept(this);
        }
        rv += "\n}\n";
        return rv;
    }

    @Override
    public String visitCommaExpr(Comma expr) {
        return parenthesize("comma", expr.left, expr.right);
    }

    
    @Override
    public String visitTernaryExpr(Ternary expr) {
        return "tenary( "+expr.condition.accept(this)+" ){ "+ expr.thenExpr.accept(this)+" else "+expr.elseExpr.accept(this)+" }";
    }

    public static void main(String[] args) {
        Expr expression = new Expr.Binary(
                            new Expr.Unary(
                                new Token(TokenType.MINUS, "-", null, 1),
                                new Expr.Literal(123)
                            ),
                            new Token(TokenType.STAR, "*", null, 1),
                            new Expr.Grouping(
                                new Expr.Literal(45.67)
                            )
                          );
        System.out.println(new AstPrinter().print(expression));
    }


}
