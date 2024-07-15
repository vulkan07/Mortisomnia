package me.barni.mortisomnia.paractivity;

public class ParaResult {
    public static enum Type {SUCCESS, FAIL, FINISHED};
    private final Type type;
    private final String message;

    public static ParaResult success() {
        return new ParaResult(Type.SUCCESS, null);
    }
    public static ParaResult fail(String reason) {
        return new ParaResult(Type.FAIL, reason);
    }
    public static ParaResult end() {
        return new ParaResult(Type.FINISHED, null);
    }
    public static ParaResult end(String message) {
        return new ParaResult(Type.FINISHED, message);
    }

    private ParaResult(Type type, String message) {
        this.type = type;
        this.message = message;
    }
    public Type getType() {
        return type;
    }
    public String getMessage() {
        return message;
    }
}
