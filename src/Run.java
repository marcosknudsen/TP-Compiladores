import java.io.IOException;
import java.util.HashMap;
import lex.Lex;

public class Run {

    public static void main(String[] args) throws IOException {
        Lex lex = new Lex(args[0]);
        int Token = 0;
        HashMap<Integer, String> tokens = new HashMap<>();
        tokens.put(260, ":=");
        tokens.put(276, "<>");
        tokens.put(61, "=");
        tokens.put(62, ">");
        tokens.put(274, ">=");
        tokens.put(60, "<");
        tokens.put(275, "<=");
        tokens.put(45, "-");
        tokens.put(47, "/");
        tokens.put(42, "*");
        tokens.put(43, "+");
        tokens.put(59, ";");
        tokens.put(40, "(");
        tokens.put(41, ")");
        tokens.put(44, ",");
        tokens.put(259, "IDENTIFICADOR");
        tokens.put(271, "CADENA");
        tokens.put(257, "if");
        tokens.put(258, "then");
        tokens.put(261, "else");
        tokens.put(262, "begin");
        tokens.put(263, "end");
        tokens.put(264, "end_if");
        tokens.put(265, "print");
        tokens.put(266, "while");
        tokens.put(267, "do");
        tokens.put(268, "fun");
        tokens.put(269, "return");
        tokens.put(272, "uinteger");
        tokens.put(273, "longint");
        Token = lex.getToken();
        while (Token != -1) {
            System.out.println(Token + ": " + tokens.get(Token));
            Token = lex.getToken();
        }
    }
}
