package interpreter.callable;

import java.util.List;

import ast.Stmt;
import ast.Stmt.Return;
import enivirement.Environment;
import interpreter.Interpreter;
import interpreter.exceptions.ReturnException;
import interpreter.klass.JLangClass;
import interpreter.klass.JLangInstance;
import interpreter.klass.JLangObject;

public class JLangFunction implements JLangCallable{
    
    private final Stmt.Function declaration;
    private final Environment closure;
    private final boolean isInitializer;


    public JLangFunction(Stmt.Function declaration, Environment closure,
                Boolean isInitializer) {
        this.declaration = declaration;
        this.closure = closure;
        this.isInitializer = isInitializer;
    }
    public JLangFunction bind(JLangObject instance) {
        Environment environment = new Environment(closure);
        environment.define(JLangClass.CLASS_INNER_INSTANCE_NAME, instance); 
        return new JLangFunction(declaration, environment, isInitializer);
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

        Environment environment = new Environment(closure);
        for (int i = 0; i < declaration.params.size(); i++) {
            environment.define(declaration.params.get(i).lexeme,
            arguments.get(i));
        }
        try {
            interpreter.executeBlock(declaration.body, environment);
        } catch (ReturnException returnValue) { // if a return statement exists we quite the function right away
            if (isInitializer) return closure.getAt(0, JLangClass.CLASS_INNER_INSTANCE_NAME);
            return returnValue.value;
        }
        if (isInitializer) return closure.getAt(0, JLangClass.CLASS_INNER_INSTANCE_NAME);
        return null;
    }
}
