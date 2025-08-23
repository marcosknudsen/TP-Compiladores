package ar.tp.acciones;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;

import ar.tp.lexer.Lex;
import ar.tp.ast.*;

public class Asterisk extends SemanticAction{

    @Override
    public Pointer ejecutar(BufferedReader sourceCode, Lex lex, int currectChar,
            HashMap<String, Symbol> symbols, HashMap<String,Integer> reservedWords) throws IOException {
                sourceCode.reset();
        return new Pointer(42);
    }

}