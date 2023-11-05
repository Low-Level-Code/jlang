package interpreter.klass;

import java.util.HashMap;
import java.util.Map;

import interpreter.errors.RuntimeError;
import tokenizer.Token;

public class JLangBaseObject extends JLangInstance {
    public final Map<String, Object> fields;

    public JLangBaseObject(JLangClass klass) {
        super(klass); // Pass the class this instance belongs to.
        this.fields = new HashMap<>();
    }
    public Map<String, Object> getFields(){
        return this.fields;
    }
    // Method to define a property on the object.
    public void defineProperty(String name, Object value) {
        fields.put(name, value);
    }

    // Method to access a property of the object. This could return null if the property doesn't exist.
    public Object getProperty(String name) {
        return fields.get(name);
    }

    public Object get(Token propertyName) {
        if (this.fields.containsKey(propertyName.lexeme)) {
            return this.fields.get(propertyName.lexeme);
        }
        throw new RuntimeError(propertyName, "Undefined property '" + propertyName.lexeme + "'.");
    }

    // Method to set a property on the object. This would be called when a property is assigned a new value.
    public void set(Token propertyName, Object value) {
        fields.put(propertyName.lexeme, value);
    }

}
