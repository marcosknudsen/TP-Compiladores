package ar.tp.app;

import ar.tp.generacion.GeneradorAsm;
import ar.tp.lexer.Lex;
import ar.tp.parser.*;

import java.io.File;
import java.util.regex.Pattern;

import static ar.tp.parser.Parser.mostrarPila;
import static ar.tp.parser.Parser.mostrarReglas;

public class Run {
    public static void main(String[] args) throws Exception {
        if (args.length == 0) {
            System.err.println("Uso: run <archivo> [debugTokens]");
            System.exit(1);
        }

        Lex lexer = new Lex(args[0]);
        Parser.setLexer(lexer);

        Parser p = new Parser(false);
        int rc = p.runParser();

        mostrarReglas(Parser.reglas);
        System.out.println();
        System.out.println("PILA:");
        mostrarPila(Parser.pila);
        System.out.println(rc == 0 ? "Parse OK" : "Parse con errores");

        String filename = new File(args[0]).getName().split("\\.")[0];

        if (rc == 0) {
            GeneradorAsm gen = new GeneradorAsm(Parser.reglas, lexer.symbols);
            gen.generar("out/" + filename + ".asm");
            System.out.println("Assembler generado en out/" + filename + ".asm");
        }
    }
}
