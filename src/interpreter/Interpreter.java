package interpreter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ast.Expr;
import ast.Expr.Block;
import ast.Expr.Comma;
import ast.Stmt;
import ast.Stmt.Catch;
import ast.Stmt.Print;
import ast.Stmt.Return;
import enivirement.Environment;
import interpreter.builtins.methods.*;
import interpreter.builtins.methods.math.*;
import interpreter.callable.JLangAnonymousFunction;
import interpreter.callable.JLangCallable;
import interpreter.callable.JLangFunction;
import interpreter.errors.DivisionByZeroException;
import interpreter.errors.InvalidArgumentsException;
import interpreter.errors.RuntimeError;
import interpreter.exceptions.BreakException;
import interpreter.exceptions.ContinueException;
import interpreter.exceptions.ReturnException;
import interpreter.klass.JLangClass;
import interpreter.klass.JLangInstance;
import main.JLang;
import tokenizer.Token;
import tokenizer.TokenType;

public class Interpreter implements Expr.Visitor<Object>,
                                    Stmt.Visitor<Void> {

    public final Environment globals = new Environment();
    private Environment environment = globals;
    private final Map<Expr, Integer> locals = new HashMap<>();
    public Interpreter() {
        globals.define("clock", new ClockFun());
        globals.define("min", new MinFunc());
        globals.define("max", new MaxFunc());
        globals.define("sum", new SumFunc());
        globals.define("range", new RangeFunc());
        globals.define("len", new LenFunc());
        globals.define("shw", new PrintFunc());
        globals.define("type", new TypeOfFunc());
        globals.define("rand", new RandFunc());
        globals.define("input", new InputFunc());
        globals.define("sprint", new SprintFunc());
        // maths
        globals.define("abs", new AbsFunc());
        globals.define("sqrt", new SqrtFunc());
        globals.define("round", new RoundFunc());
        globals.define("cos", new CosFunc());
        globals.define("sin", new SinFunc());
        globals.define("tan", new TanFunc());
        globals.define("log", new LogFunc());
        globals.define("exp", new ExpFunc());
        globals.define("ceil", new CeilFunc());
        globals.define("floor", new FloorFunc());
    }


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
        
    private Object lookUpVariable(Token name, Expr expr) {
        Integer distance = locals.get(expr);
        if (distance != null) {
            return environment.getAt(distance, name.lexeme);
        } else {
            return globals.get(name);
        }
    }
        
    private void updateVariable(Expr operand, double newValue) {
        if (operand instanceof Expr.Variable) {
            Expr.Variable var = (Expr.Variable)operand;
            // Assuming you have an environment that keeps track of variable values
            // The environment might be a simple map or a more complex structure
            // that handles scoping
            environment.assign(var.name, newValue);
        } else {
            throw new RuntimeException("The operand is not a variable and cannot be assigned a new value.");
        }
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
                    throw new DivisionByZeroException(expr.operator, "Division by zero.");
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
            case INCREMENT:
                checkNumberOperand(expr.operator, right);
                double incrementedValue = (double)right + 1.0;
                updateVariable(expr.right, incrementedValue);
                return right; 
            case DECREMENT:
                checkNumberOperand(expr.operator, right);
                double decrementedValue = (double)right - 1.0;
                updateVariable(expr.right, decrementedValue);
                return right; 
        }
        // Unreachable.
        return null;
    }
    @Override
    public Object visitPostfixExpr(Expr.Postfix expr) {
        Object operand = evaluate(expr.left);
    
        switch (expr.operator.type) {
            case INCREMENT:
                checkNumberOperand(expr.operator, operand);
                double incrementedValue = (double)operand + 1.0;
                updateVariable(expr.left, incrementedValue);
                return operand;
                
            case DECREMENT:
                checkNumberOperand(expr.operator, operand);
                double decrementedValue = (double)operand - 1.0;
                updateVariable(expr.left, decrementedValue);
                return operand;
    
            default:
                throw new RuntimeException("Unexpected postfix operator: " + expr.operator.type);
        }
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
    public Void visitConstStmt(Stmt.Const stmt) {
        Object value = null;
        if (stmt.initializer != null) {
            value = evaluate(stmt.initializer);
        }
        environment.defineConst(stmt.name.lexeme, value);
        return null;
    }
    @Override
    public Object visitVariableExpr(Expr.Variable expr) {
        return lookUpVariable(expr.name, expr);
    }

    @Override
    public Object visitAssignExpr(Expr.Assign expr) {
        Object value = evaluate(expr.value);
        Integer distance = locals.get(expr);
        if (distance != null) {
            environment.assignAt(distance, expr.name, value);
        } else {
            globals.assign(expr.name, value);
        }
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
        while (isTruthy(evaluate(stmt.condition))) {
            try {
                execute(stmt.body);
            } catch (ContinueException e) {
                // ContinueException is used to skip the rest of the current iteration and proceed with loop
                continue; // Explicitly continue to the next iteration
            } catch (BreakException e) {
                // BreakException is used to exit the loop early
                break; // Break out of the while loop
            }
        }
        return null;
    }
    @Override
    public Void visitBreakStmt(Stmt.Break stmt) {
        throw new BreakException();
    }
    @Override
    public Void visitContinueStmt(Stmt.Continue stmt) {
        throw new ContinueException();
    }
    @Override
    public Void visitTryCatchStmt(Stmt.TryCatch stmt) {
        boolean caughtException = false;
        try {
            execute(stmt.tryBlock);
        } catch (RuntimeException ex) {
            caughtException = true;
            for (Stmt.Catch catchBlock : stmt.catchBlocks) {
                if (ex.getClass().getSimpleName().equals(catchBlock.exceptionType.lexeme)) {
                    // Define the exception in the local environment
                    // and execute the catch block
                    environment.define(catchBlock.variable.lexeme, ex);
                    execute(catchBlock.block);
                    caughtException = false; // Exception handled
                    break; // Exit after the first matching catch block
                }
            }
            if (caughtException) {
                throw ex; // Rethrow the exception if no matching catch block is found
            }
        } finally {
            // The finally block should be executed whether an exception was thrown or not.
            if (stmt.finallyBlock != null) {
                execute(stmt.finallyBlock);
            }
            // If there was an exception that was not caught, it was re-thrown above.
            // The Java runtime will continue to propagate it after executing the finally block.
        }
        return null;
    }
    @Override
    public Object visitCallExpr(Expr.Call expr) {
        Object callee = evaluate(expr.callee);
        List<Object> arguments = new ArrayList<>();
        for (Expr argument : expr.arguments) {
            arguments.add(evaluate(argument));
        }
        if (!(callee instanceof JLangCallable)) { // if something that's not a function calls something
            throw new RuntimeError(expr.paren,
            "Can only call functions and classes.");
        }
        
        JLangCallable function = (JLangCallable)callee;

        if (arguments.size() != function.arity() && function.arity() != -1) { // if the number of arguments is bigger than it should be, the -1 is for dynamic arguments
            throw new RuntimeError(expr.paren, "Expected " +
            function.arity() + " arguments but got " +
            arguments.size() + ".");
        } 
            
        try {
            return function.call(this, arguments);
        } catch (InvalidArgumentsException e) { // catching errors for built-in functions
            throw new RuntimeError(new Token(TokenType.FUN, "", null, 1), e.getMessage());
        }
    }
    @Override
    public Void visitFunctionStmt(Stmt.Function stmt) {
        JLangFunction function = new JLangFunction(stmt, environment, false);
        environment.define(stmt.name.lexeme, function);
        return null;
    }

    @Override
    public Void visitReturnStmt(Stmt.Return stmt) {
        Object value = null;
        if (stmt.value != null) value = evaluate(stmt.value);
        throw new ReturnException(value);
    }
    @Override
    public Object visitLambdaFunctionExpr(Expr.LambdaFunction expr) {
        // Create a new function object, capturing the current environment
        JLangAnonymousFunction function = new JLangAnonymousFunction(expr , environment);
        return function;
    }

    @Override
    public Void visitClassStmt(Stmt.Class stmt) {
        Object superclass = null;
        if (stmt.superclass != null) {
            superclass = evaluate(stmt.superclass);
            if (!(superclass instanceof JLangClass)) {
                throw new RuntimeError(stmt.superclass.name,
            "Superclass must be a class.");
            }
        }
        environment.define(stmt.name.lexeme, null);
        if (stmt.superclass != null) {
            environment = new Environment(environment);
            environment.define(JLangClass.CLASS_SUPER_INSTANCE_NAME, superclass);
        }
            
        Map<String, JLangFunction> methods = new HashMap<>();
        for (Stmt.Function method : stmt.methods) {
            JLangFunction function = new JLangFunction(method, environment,method.name.lexeme.equals(JLangClass.CLASS_INITIALIZATION_FUNCTION_NAME));
            methods.put(method.name.lexeme, function);
        }
        JLangClass klass = new JLangClass(stmt.name.lexeme,
            (JLangClass)superclass, methods);

        if (superclass != null) {
            environment = environment.enclosing;
        }
                
        environment.assign(stmt.name, klass);
        return null;
    }

    @Override
    public Object visitGetExpr(Expr.Get expr) {
        Object object = evaluate(expr.object);
        if (object instanceof JLangInstance) {
            return ((JLangInstance) object).get(expr.name);
        }
        throw new RuntimeError(expr.name, "Only instances have properties.");
    }

    @Override
    public Object visitSetExpr(Expr.Set expr) {
        Object object = evaluate(expr.object);
        if (!(object instanceof JLangInstance)) {
            throw new RuntimeError(expr.name, "Only instances have fields.");
        }
        Object value = evaluate(expr.value);
        ((JLangInstance)object).set(expr.name, value);
        return value;
    }
    @Override
    public Object visitSuperExpr(Expr.Super expr) {
        int distance = locals.get(expr);
        JLangClass superclass = (JLangClass)environment.getAt(
        distance, JLangClass.CLASS_SUPER_INSTANCE_NAME);
        JLangInstance object = (JLangInstance)environment.getAt(
        distance - 1, JLangClass.CLASS_INNER_INSTANCE_NAME);
        
        JLangFunction method = superclass.findMethod(expr.method.lexeme);
        if (method == null) {
            throw new RuntimeError(expr.method,
                "Undefined property '" + expr.method.lexeme + "'.");
        }
        return method.bind(object);

    }
    @Override
    public Object visitThisExpr(Expr.This expr) {
        return lookUpVariable(expr.keyword, expr);
    }


    public void executeBlock(List<Stmt> statements,
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
        // stmt.accept(this);
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
    
    public void resolve(Expr expr, int depth) {
        locals.put(expr, depth);
    }

}
 
