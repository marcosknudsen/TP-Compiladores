package ar.tp.acciones;

import ar.tp.ast.Pointer;
import ar.tp.ast.Symbol;
import ar.tp.lexer.Lex;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;

public class WarningCommentNotClosed extends  SemanticAction {
    @Override
    public Pointer ejecutar(BufferedReader sourceCode, Lex lex, int currentChar, HashMap<String, Symbol> symbols, HashMap<String, Integer> reservedWords) throws IOException {
        System.out.println("Warning: Comment not closed: linea " + lex.line);
        return new Pointer(-1);
    }
}
