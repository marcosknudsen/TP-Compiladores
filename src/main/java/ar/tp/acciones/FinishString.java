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

        String lit = lex.getString();
        lex.yylval = lit;

        symbols.putIfAbsent(lit, new Symbol("String", "cte"));

        return new Pointer(ar.tp.parser.Parser.CADENA, lit);
    }


}
