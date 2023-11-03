package interpreter.builtins.methods.math;

public class JLangMath {

    public static double sin(double value) {
        return Math.sin(value);
    }

    public static double cos(double value) {
        return Math.cos(value);
    }

    public static double tan(double value) {
        return Math.tan(value);
    }

    public static double log(double value) {
        if (value <= 0) {
            throw new IllegalArgumentException("Value must be greater than 0 for log");
        }
        return Math.log(value);
    }

    public static double exp(double value) {
        return Math.exp(value);
    }

    public static double sqrt(double value) {
        if (value < 0) {
            throw new IllegalArgumentException("Value must be non-negative for sqrt");
        }
        return Math.sqrt(value);
    }

    public static double abs(double value) {
        return Math.abs(value);
    }

    public static long round(double value) {
        return Math.round(value);
    }

    public static double ceil(double value) {
        return Math.ceil(value);
    }

    public static double floor(double value) {
        return Math.floor(value);
    }
}