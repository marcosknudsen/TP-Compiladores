package ar.tp.acciones;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;

import ar.tp.ast.*;
import ar.tp.acciones.*;
import ar.tp.lexer.Lex;
public class Assign extends SemanticAction {

    @Override
    public Pointer ejecutar(BufferedReader sourceCode, Lex lex, int currectChar,
            HashMap<String, Symbol> symbols, HashMap<String, Integer> reservedWords) throws IOException {
        return new Pointer(260);
    }
}
