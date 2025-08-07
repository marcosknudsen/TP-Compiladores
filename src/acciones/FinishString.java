package acciones;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;

import classes.*;
import lex.Lex;

public class FinishString extends SemanticAction {

    @Override
    public Pointer ejecutar(BufferedReader sourceCode, Lex lex, int currentChar,
            HashMap<String, Symbol> symbols, HashMap<String, Integer> reservedWords) throws IOException {
        Symbol value = symbols.get(lex.getString());
        lex.yylval = lex.getString();
        if (value == null) {
            symbols.put(lex.getString(), new Symbol("String", "String"));
        }
        return new Pointer(271, lex.getString());
    }

}
