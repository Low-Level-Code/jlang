package interpreter;

import java.util.List;

import ast.Expr;
import ast.Expr.Block;
import ast.Expr.Comma;
import ast.Stmt;
import ast.Stmt.Catch;
import ast.Stmt.Print;
import enivirement.Environment;
import interpreter.errors.BreakException;
import interpreter.errors.ContinueException;
import interpreter.errors.RuntimeError;
import main.JLang;
import tokenizer.Token;
import tokenizer.TokenType;

public class Interpreter implements Expr.Visitor<Object>,
                                    Stmt.Visitor<Void> {

    public Environment environment = new Environment();
    private boolean isInLoop = false;

    private Object evaluate(Expr expr) {
        return expr.accept(this);
    }
    private boolean isTruthy(Object object) {
        if (object == null) return false;
        if (object instanceof Boolean) return (boolean)object;
        return true;
    }
    private boolean isEqual(Object a, Object b) {
        if (a == null && b == null) return true;
        if (a == null) return false;
        return a.equals(b);
    }
    private void checkNumberOperand(Token operator, Object operand) {
        if (operand instanceof Double) return;
        throw new RuntimeError(operator, "Operand must be a number.");
    }
    private void checkNumberOperands(Token operator,
        Object left, Object right) {
        if (left instanceof Double && right instanceof Double) return;
        throw new RuntimeError(operator, "Operands must be numbers.");
    }

    @Override
    public Object visitBinaryExpr(Expr.Binary expr) {
        Object left = evaluate(expr.left);
        Object right = evaluate(expr.right);
        switch (expr.operator.type) {
            case GREATER:
                checkNumberOperands(expr.operator, left, right);
                return (double)left > (double)right;
            case GREATER_EQUAL:
                checkNumberOperands(expr.operator, left, right);
                return (double)left >= (double)right;
            case LESS:
                checkNumberOperands(expr.operator, left, right);
                return (double)left < (double)right;
            case LESS_EQUAL:
                checkNumberOperands(expr.operator, left, right);
                return (double)left <= (double)right;
            case BANG_EQUAL: 
                return !isEqual(left, right);
            case EQUAL_EQUAL: 
                return isEqual(left, right);
            case MINUS:
                checkNumberOperands(expr.operator, left, right);
                return (double)left - (double)right;
            case PLUS:
                if (left instanceof Double && right instanceof Double) {
                    return (double)left + (double)right;
                }
                if (left instanceof String && right instanceof String) { // yess brother you can add strings
                    return (String)left + (String)right;
                }
                // This should add the adition for a string and a number like  "100" + 5 = "1005"
                if (left instanceof String) {
                    return (String)left + stringify(right);
                }
                if (right instanceof String) {
                    return stringify(left) + (String)right;
                }
                throw new RuntimeError(expr.operator, 
                "Operands must be two numbers or at least one string.");
            case SLASH:
                checkNumberOperands(expr.operator, left, right);
                if ((double) right == 0.0) {
                    throw new RuntimeError(expr.operator, "Division by zero.");
                }
                return (double)left / (double)right;
            case STAR:
                checkNumberOperands(expr.operator, left, right);
               return (double)left * (double)right;
        }
        // Unreachable.
        return null;
    }
    
    @Override
    public Object visitUnaryExpr(Expr.Unary expr) {
        Object right = evaluate(expr.right);
        switch (expr.operator.type) {
            case BANG:
                return !isTruthy(right);
            case MINUS:
                checkNumberOperand(expr.operator, right);
                return -(double)right;
        }
        // Unreachable.
        return null;
    }

    @Override
    public Object visitLiteralExpr(Expr.Literal expr) {
        return expr.value;
    }

    @Override
    public Object visitGroupingExpr(Expr.Grouping expr) {
        return evaluate(expr.expression);
    }

    @Override
    public Object visitTernaryExpr(Expr.Ternary expr) {
        Object condition = evaluate(expr.condition);
        if (isTruthy(condition)) {
            return evaluate(expr.thenExpr);
        } else {
            return evaluate(expr.elseExpr);
        }
    }
    @Override
    public Void visitExpressionStmt(Stmt.Expression stmt) {
        evaluate(stmt.expression);
        return null;
    }
    @Override
    public Void visitPrintStmt(Stmt.Print stmt) {
        Object value = evaluate(stmt.expression);
        System.out.println(stringify(value));
        return null;
    }
    
    @Override
    public Object visitBlockExpr(Expr.Block expr) {
        Object result = null;
        for (Expr expression : expr.statements) {
            result = evaluate(expression);
        }
        System.out.println(stringify(result));
        return result;
    }
    @Override
    public Object visitCommaExpr(Expr.Comma expr) {
        evaluate(expr.left);
        Object result = evaluate(expr.right);
        System.out.println(stringify(result)); 
        return result;
    }

    @Override
    public Void visitVarStmt(Stmt.Var stmt) {
        Object value = null;
        if (stmt.initializer != null) {
            value = evaluate(stmt.initializer);
        }
        environment.define(stmt.name.lexeme, value);
        return null;
    }
    @Override
    public Object visitVariableExpr(Expr.Variable expr) {
        return environment.get(expr.name);
    }

    @Override
    public Object visitAssignExpr(Expr.Assign expr) {
        Object value = evaluate(expr.value);
        environment.assign(expr.name, value);
        return value;
    }

    @Override
    public Void visitBlockStmt(Stmt.Block stmt) {
        executeBlock(stmt.statements, new Environment(environment));
        return null;
    }
    @Override
    public Void visitIfStmt(Stmt.If stmt) {
        if (isTruthy(evaluate(stmt.condition))) {
            execute(stmt.thenBranch);
        } else if (stmt.elseBranch != null) {
            execute(stmt.elseBranch);
        }
        return null;
    }
    @Override
    public Object visitLogicalExpr(Expr.Logical expr) {
        Object left = evaluate(expr.left);
        if (expr.operator.type == TokenType.OR) {
            if (isTruthy(left)) return left;
        } else {
            if (!isTruthy(left)) return left;
        }
        return evaluate(expr.right);
    }

    @Override
    public Void visitWhileStmt(Stmt.While stmt) {
        try {
            isInLoop = true;
            while (isTruthy(evaluate(stmt.condition))) {
                try {
                    execute(stmt.body);
                } catch (ContinueException e){
                    // ignore and continue the loop
                }
            }
        } catch (BreakException ex) {
            // Break encountered, we yeet here out of the loop.
            isInLoop = false;
        } finally {
            isInLoop = false;
        }
        return null;
    }
    @Override
    public Void visitBreakStmt(Stmt.Break stmt) {
        if (!isInLoop) {
            throw new RuntimeError(stmt.keyword, "Break statement must be inside a loop.");
        }
        throw new BreakException();
    }
    @Override
    public Void visitContinueStmt(Stmt.Continue stmt) {
        if (!isInLoop) {
            throw new RuntimeError(stmt.keyword, "Continue statement must be inside a loop.");
        }
        throw new ContinueException();
    }
    @Override
    public Void visitTryCatchStmt(Stmt.TryCatch stmt) {
        try {
            execute(stmt.tryBlock);
        } catch (RuntimeException ex) {
            for (Catch catchBlock : stmt.catchBlocks) {
                if (ex.getClass().getSimpleName().equals(catchBlock.exceptionType.lexeme)) {
                    // Define the exception in the local environment
                    // and execute the catch block
                    environment.define(catchBlock.variable.lexeme, ex);
                    execute(catchBlock.block);
                    return null; // Exit after the first matching catch block
                }
            }
            throw ex; // Rethrow the exception if no matching catch block is found
        }
        return null;
    }
    void executeBlock(List<Stmt> statements,
        Environment environment) {
        Environment previous = this.environment;
        try {
            this.environment = environment;
            for (Stmt statement : statements) {
                execute(statement);
            }
        } finally {
            this.environment = previous;
        }
    }
    
    private String stringify(Object object) {
        if (object == null) return "nil";
        if (object instanceof Double) {
            String text = object.toString();
            if (text.endsWith(".0")) {
                text = text.substring(0, text.length() - 2);
            }
            return text;
        }
        return object.toString();
    }
    private void execute(Stmt stmt) {
        stmt.accept(this);
    }
    public void interpret(List<Stmt> statements) {
        try {
            for (Stmt statement : statements) {
                execute(statement);
            }
        } catch (RuntimeError error) {
            JLang.runtimeError(error);
        }
    }
    
        

}
 
