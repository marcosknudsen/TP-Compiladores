package ar.tp.acciones;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;

import ar.tp.ast.*;
import ar.tp.lexer.Lex;

public class Start extends SemanticAction {

    @Override
    public Pointer ejecutar(BufferedReader sourceCode, Lex lex, int currentChar,
            HashMap<String, Symbol> symbols, HashMap<String, Integer> reservedWords) throws IOException {
        lex.setString(String.valueOf((char) currentChar));
        return new Pointer(-1);
    }

}
