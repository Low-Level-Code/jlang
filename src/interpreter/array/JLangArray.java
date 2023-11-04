package interpreter.array;

import java.util.List;

public class JLangArray {
    private final List<Object> elements;

    public JLangArray(List<Object> elements) {
        this.elements = elements;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        
        for (int i = 0; i < elements.size(); i++) {
            sb.append(elements.get(i));
            if (i < elements.size() - 1) {
                sb.append(", ");
            }
        }
        
        sb.append("]");
        return sb.toString();
    }
    // Add methods for working with the array, such as get, set, length, etc.
    public int size() {
        return elements.size();
    }

    public Object get(int index) {
        if (index >= 0 && index < elements.size()) {
            return elements.get(index);
        } else {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + elements.size());
        }
    }
    public void set(int index, Object value) {
        // Ensure the index is within the array bounds
        if (index >= 0 && index < elements.size()) {
            elements.set(index, value);
        } else {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + elements.size());
        }
    }
        
}