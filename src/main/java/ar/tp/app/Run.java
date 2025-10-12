package ar.tp.app;

import ar.tp.lexer.Lex;
import ar.tp.parser.*;

public class Run {
    public static void main(String[] args) throws Exception {
        if (args.length == 0) {
            System.err.println("Uso: run <archivo> [debugTokens]");
            System.exit(1);
        }

        Parser.setLexer(new Lex(args[0]));

        Parser p = new Parser(false);    // false = sin trazas del parser
        int rc = p.runParser();            // o p.run() si tu clase lo expone
        System.out.println(rc == 0 ? "Parse OK" : "Parse con errores");
    }
}
