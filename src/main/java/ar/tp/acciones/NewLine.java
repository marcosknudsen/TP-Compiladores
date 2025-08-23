package ar.tp.acciones;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;

import ar.tp.ast.*;
import ar.tp.lexer.Lex;

public class NewLine extends SemanticAction {

    @Override
    public Pointer ejecutar(BufferedReader sourceCode, Lex lex, int currentChar,
            HashMap<String, Symbol> symbols, HashMap<String, Integer> reservedWords) throws IOException {
            if (currentChar=='\n')
                lex.line+=1;
            return new Pointer(-1);
    }
    
}
