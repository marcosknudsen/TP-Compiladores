package ar.tp.acciones;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;

import ar.tp.ast.*;
import ar.tp.lexer.Lex;
import ar.tp.parser.Parser;

public class FinishString extends SemanticAction {

    @Override
    public Pointer ejecutar(BufferedReader sourceCode, Lex lex, int currentChar,
                            HashMap<String, Symbol> symbols, HashMap<String, Integer> reservedWords) throws IOException {

        String lit = lex.getString();   // debe estar sin comillas
        lex.yylval = lit;

        // opcional: registrar como constante
        symbols.putIfAbsent(lit, new Symbol("String", "cte"));

        // OJO: aquí NO resetear
        return new Pointer(ar.tp.parser.Parser.CADENA, lit);
    }


}
