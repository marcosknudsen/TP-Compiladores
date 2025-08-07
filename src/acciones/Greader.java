package acciones;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;

import classes.*;
import lex.Lex;

public class Greader extends SemanticAction {

    @Override
    public Pointer ejecutar(BufferedReader sourceCode, Lex lex, int currentChar,
            HashMap<String, Symbol> symbols, HashMap<String, Integer> reservedWords) throws IOException {
        sourceCode.reset();
        return new Pointer(62);
    }
}
