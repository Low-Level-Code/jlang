package resolver;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import interpreter.Interpreter;
import interpreter.klass.JLangClass;
import main.JLang;
import tokenizer.Token;
import ast.Expr;
import ast.Stmt;

public class Resolver implements Expr.Visitor<Void>, Stmt.Visitor<Void> {
    // TODO: Fix the variable instanciation in the for loop 
    // TODO: Fix anonymous functions not declared when in the local scope
    private final Interpreter interpreter;
    private final Stack<Map<String, Boolean>> scopes = new Stack<>();
    private FunctionType currentFunction = FunctionType.NONE;
    private int loopDepth = 0;
    private ClassType currentClass = ClassType.NONE;

    public Resolver(Interpreter interpreter) {
        this.interpreter = interpreter;
    }
    
    private void resolve(Stmt stmt) {
        stmt.accept(this);
    }

    private void resolve(Expr expr) {
        expr.accept(this);
    }

    public void resolve(List<Stmt> statements) {
        for (Stmt statement : statements) {
           resolve(statement);
        }
    }
    
    private void beginScope() {
        scopes.push(new HashMap<String, Boolean>());
    }

    private void endScope() {
        scopes.pop();
    }

    private void declare(Token name) {
        if (scopes.isEmpty()) return;
        Map<String, Boolean> scope = scopes.peek();
        if (scope.containsKey(name.lexeme)) {
            JLang.error(name,
            "Already a variable with this name in this scope.");
        }
            
        scope.put(name.lexeme, false);
    }
    private void resolveFunction(
    Stmt.Function function, FunctionType type) {
        FunctionType enclosingFunction = currentFunction;
        currentFunction = type;
        beginScope();
        for (Token param : function.params) {
            declare(param);
            define(param);
        }
        resolve(function.body);
        endScope();
        currentFunction = enclosingFunction;
    }

    @Override
    public Void visitVarStmt(Stmt.Var stmt) {
        declare(stmt.name);
        if (stmt.initializer != null) {
            resolve(stmt.initializer);
        }
        define(stmt.name);
        return null;
    }

    @Override
    public Void visitConstStmt(Stmt.Const stmt) {
        declare(stmt.name);
        if (stmt.initializer != null) {
            resolve(stmt.initializer);
        }
        define(stmt.name);
        return null;
    }

    private void define(Token name) {
        if (scopes.isEmpty()) return;
        scopes.peek().put(name.lexeme, true);
    }

    private void resolveLocal(Expr expr, Token name) {
        for (int i = scopes.size() - 1; i >= 0; i--) {
            if (scopes.get(i).containsKey(name.lexeme)) {
                interpreter.resolve(expr, scopes.size() - 1 - i);
                return;
            }
        }
    }

    private void resolveFunction(Expr.LambdaFunction function, FunctionType type) {
        FunctionType enclosingFunction = currentFunction;
        currentFunction = type;
        beginScope();
        for (Token param : function.params) {
            declare(param);
            define(param);
        }
        resolve(function.body);
        endScope();
        currentFunction = enclosingFunction;
    }

    @Override
    public Void visitBlockStmt(Stmt.Block stmt) {
        beginScope();
        resolve(stmt.statements);
        endScope();
        return null;
    }
    @Override
    public Void visitVariableExpr(Expr.Variable expr) {
        if (!scopes.isEmpty() && scopes.peek().get(expr.name.lexeme) == Boolean.FALSE) {
            JLang.error(expr.name,
                "Can't read local variable in its own initializer.");
        }
        resolveLocal(expr, expr.name);
        return null;
    }
    
    @Override
    public Void visitAssignExpr(Expr.Assign expr) {
        resolve(expr.value);
        resolveLocal(expr, expr.name);
        return null;
    }
 
    @Override
    public Void visitFunctionStmt(Stmt.Function stmt) {
        declare(stmt.name);
        define(stmt.name);
        resolveFunction(stmt, FunctionType.FUNCTION);
        return null;
    }
    @Override
    public Void visitLambdaFunctionExpr(Expr.LambdaFunction expr) {
        declare(expr.name);
        define(expr.name);
        resolveFunction(expr, FunctionType.LAMBDA);
        return null;
    }
    @Override
    public Void visitExpressionStmt(Stmt.Expression stmt) {
        resolve(stmt.expression);
        return null;
    }
    @Override
    public Void visitIfStmt(Stmt.If stmt) {
        resolve(stmt.condition);
        resolve(stmt.thenBranch);
        if (stmt.elseBranch != null) resolve(stmt.elseBranch);
        return null;
    }
    @Override
    public Void visitPrintStmt(Stmt.Print stmt) {
        resolve(stmt.expression);
        return null;
    }
    @Override
    public Void visitReturnStmt(Stmt.Return stmt) {
        if (currentFunction == FunctionType.NONE) {
            JLang.error(stmt.keyword, "Can't return from top-level code.");
        }
            
        if (stmt.value != null) {
            if (currentFunction == FunctionType.INITIALIZER) {
                JLang.error(stmt.keyword,
                "Can't return a value from an initializer.");
            }
                
            if ( currentFunction == FunctionType.FUNCTION || currentFunction == FunctionType.LAMBDA ) {
                resolve(stmt.value);
            } else {
                // Add any additional checks or errors for other types of functions
            }
        }
        return null;
    }
    @Override
    public Void visitWhileStmt(Stmt.While stmt) {
        loopDepth++;
        resolve(stmt.condition);
        resolve(stmt.body);
        loopDepth--;
        return null;
    }
    @Override
    public Void visitBinaryExpr(Expr.Binary expr) {
        resolve(expr.left);
        resolve(expr.right);
        return null;
    }
    @Override
    public Void visitCallExpr(Expr.Call expr) {
        resolve(expr.callee);
        for (Expr argument : expr.arguments) {
            resolve(argument);
        }
        return null;
    }
    @Override
    public Void visitGroupingExpr(Expr.Grouping expr) {
        resolve(expr.expression);
        return null;
    }
    @Override
    public Void visitLiteralExpr(Expr.Literal expr) {
        return null;
    }
    @Override
    public Void visitLogicalExpr(Expr.Logical expr) {
        resolve(expr.left);
        resolve(expr.right);
        return null;
    }
    @Override
    public Void visitUnaryExpr(Expr.Unary expr) {
        resolve(expr.right);
        return null;
    }
    @Override
    public Void visitPostfixExpr(Expr.Postfix expr) {
        resolve(expr.left);
        return null;
    }
    @Override
    public Void visitClassStmt(Stmt.Class stmt) {
        ClassType enclosingClass = currentClass;
        currentClass = ClassType.CLASS;
        declare(stmt.name);
        define(stmt.name);
        if (stmt.superclass != null &&
            stmt.name.lexeme.equals(stmt.superclass.name.lexeme)) {
            JLang.error(stmt.superclass.name,
            "A class can't inherit from itself.");
        }
        if (stmt.superclass != null) {
            currentClass = ClassType.SUBCLASS;
            resolve(stmt.superclass);
        }
        if (stmt.superclass != null) {
            beginScope();
            scopes.peek().put(JLangClass.CLASS_SUPER_INSTANCE_NAME, true);
        }
        beginScope();
        scopes.peek().put(JLangClass.CLASS_INNER_INSTANCE_NAME, true);
        for (Stmt.Function method : stmt.methods) {
            FunctionType declaration = FunctionType.METHOD;
            if (method.name.lexeme.equals(JLangClass.CLASS_INITIALIZATION_FUNCTION_NAME)) {
                declaration = FunctionType.INITIALIZER;
            }
            resolveFunction(method, declaration);
        }
        endScope();
        if (stmt.superclass != null) endScope();
        currentClass = enclosingClass;
        return null;
    }

    @Override
    public Void visitGetExpr(Expr.Get expr) {
        resolve(expr.object);
        return null;
    }

    @Override
    public Void visitSetExpr(Expr.Set expr) {
        resolve(expr.value);
        resolve(expr.object);
        return null;
    }

    @Override
    public Void visitSuperExpr(Expr.Super expr) {
        if (currentClass == ClassType.NONE) {
            JLang.error(expr.keyword,
            "Can't use "+JLangClass.CLASS_SUPER_INSTANCE_NAME+" outside of a class.");
        } else if (currentClass != ClassType.SUBCLASS) {
            JLang.error(expr.keyword,
            "Can't use "+JLangClass.CLASS_SUPER_INSTANCE_NAME+" in a class with no superclass.");
        }
        resolveLocal(expr, expr.keyword);
        return null;
    }

    @Override
    public Void visitTernaryExpr(Expr.Ternary expr) {
        resolve(expr.condition); // Resolve the condition part
        resolve(expr.thenExpr);  // Resolve the 'then' part
        resolve(expr.elseExpr);  // Resolve the 'else' part
        return null;
    }

    @Override
    public Void visitCommaExpr(Expr.Comma expr) {
        resolve(expr.left);  // Resolve the left expression
        resolve(expr.right); // Resolve the right expression, which is the result of the comma expression
        return null;
    }

    @Override
    public Void visitBreakStmt(Stmt.Break stmt) {
        if (loopDepth <= 0) {
            JLang.error(stmt.keyword, "Break statement must be inside a loop.");
        }
        // Do not throw an exception here; the resolver's job is to report errors, not to handle control flow.
        return null;
    }

    @Override
    public Void visitContinueStmt(Stmt.Continue stmt) {
        if (loopDepth <= 0) {
            JLang.error(stmt.keyword, "Continue statement must be inside a loop.");
        }
        // Do not throw an exception here; same reason as above.
        return null;
    }

    @Override
    public Void visitTryCatchStmt(Stmt.TryCatch stmt) {
        // Resolve the try block
        resolve(stmt.tryBlock);

        // Resolve each catch block
        for (Stmt.Catch catchBlock : stmt.catchBlocks) {
            beginScope();
            declare(catchBlock.variable);
            define(catchBlock.variable);
            resolve(catchBlock.block);
            endScope();
        }

        // If there's a finally block, resolve it too
        if (stmt.finallyBlock != null) {
            resolve(stmt.finallyBlock);
        }

        return null;
    }
    @Override
    public Void visitThisExpr(Expr.This expr) {
        if (currentClass == ClassType.NONE) {
            JLang.error(expr.keyword,
            "Can't use 'this' outside of a class.");
            return null;
        }
        resolveLocal(expr, expr.keyword);
        return null;
    }

}
