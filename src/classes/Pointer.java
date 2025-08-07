package classes;

public class Pointer {
    public int token;
    public String lexema;

    public Pointer(int token, String lexema) {
        this.token = token;
        this.lexema = lexema;
    }

    public Pointer(int token) {
        this.token = token;
        this.lexema = null;
    }
}
