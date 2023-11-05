package interpreter.string;

import interpreter.indexible.JLangIndexible;

public class JLangString implements JLangIndexible {
    private final String content;

    public JLangString(String content) {
        this.content = content;
    }

    @Override
    public Object getItem(int index) {
        return this.content.charAt(index);
    }

    @Override
    public int length() {
        return this.content.length();
    }

    // ... potentially other string methods ...
}