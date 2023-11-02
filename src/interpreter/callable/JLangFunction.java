package interpreter.callable;

import java.util.List;

import ast.Stmt;
import ast.Stmt.Return;
import enivirement.Environment;
import interpreter.Interpreter;
import interpreter.exceptions.ReturnException;

public class JLangFunction implements JLangCallable{
    
    private final Stmt.Function declaration;

    public JLangFunction(Stmt.Function declaration) {
        this.declaration = declaration;
    }
    @Override
    public int arity() {
        return declaration.params.size();
    }
    @Override
    public String toString() {
        return "<fn " + declaration.name.lexeme + ">";
    }

    @Override
    public Object call(Interpreter interpreter,
        List<Object> arguments) {
        Environment environment = new Environment(interpreter.globals);
        for (int i = 0; i < declaration.params.size(); i++) {
            environment.define(declaration.params.get(i).lexeme,
            arguments.get(i));
        }
        try {
            interpreter.executeBlock(declaration.body, environment);
        } catch (ReturnException returnValue) { // if a return statement exists we quite the function right away
            return returnValue.value;
        }
        interpreter.executeBlock(declaration.body, environment);
        return null;
    }
}
