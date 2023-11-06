package interpreter.indexible;

import java.util.List;

public interface JLangIndexible {
    Object getItem(int index);
    int length();
    List<Object> asList();
    void sort();
    void reverse();
}