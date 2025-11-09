package ar.tp.app;

import ar.tp.lexer.Lex;
import ar.tp.parser.*;

import static ar.tp.parser.Parser.mostrarPila;
import static ar.tp.parser.Parser.mostrarReglas;

public class Run {
    public static void main(String[] args) throws Exception {
        if (args.length == 0) {
            System.err.println("Uso: run <archivo> [debugTokens]");
            System.exit(1);
        }

        Parser.setLexer(new Lex(args[0]));

        Parser p = new Parser(false);
        int rc = p.runParser();
        mostrarReglas(p.reglas);
        System.out.println();
        System.out.println("PILA:");
        mostrarPila(p.pila);
        System.out.println(rc == 0 ? "Parse OK" : "Parse con errores");
    }
}
