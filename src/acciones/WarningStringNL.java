package acciones;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import classes.*;
import lex.Lex;

public class WarningStringNL extends SemanticAction {

    @Override
    public Pointer ejecutar(BufferedReader sourceCode, Lex lex, int currentChar,
            HashMap<String, Symbol> symbols, HashMap<String, Integer> reservedWords) throws IOException {
        Symbol value = symbols.get(lex.getString());
        if (value == null) {
            symbols.put(lex.getString(), new Symbol("String", "String"));
        }
        if (currentChar == '\n')
            lex.line += 1;
        lex.yylval = lex.getString();
        System.out.println("Warning: La cadena no fue cerrada correctamente: linea " + lex.line);
        return new Pointer(271, lex.getString());
    }

}
