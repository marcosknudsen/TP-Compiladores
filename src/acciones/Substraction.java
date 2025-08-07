package acciones;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;

import classes.*;
import lex.Lex;

public class Substraction extends SemanticAction {

    @Override
    public Pointer ejecutar(BufferedReader sourceCode, Lex lex, int currectChar,
            HashMap<String, Symbol> symbols, HashMap<String, Integer> reservedWords) throws IOException {
        return new Pointer(45);
    }

}
