package acciones;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;

import classes.*;
import lex.*;

public class FinishIdNewLine extends SemanticAction {

    @Override
    public Pointer ejecutar(BufferedReader sourceCode, Lex lex, int currentChar,
            HashMap<String, Symbol> symbols, HashMap<String, Integer> reserverdWords) throws IOException {
        String cad = lex.getString();
        if (cad.length() > 25) {// recorto cad a 25 c,
            cad = cad.substring(0, 25);
            System.out.println("The string is shortened to 25 characters");
        }
        Symbol value = symbols.get(cad);
        int Token = reserverdWords.getOrDefault(cad, -1);
        lex.yylval = lex.getString();
        if (Token == -1) {// Si no es PR reescribe el token
            if (value == null) {
                value = new Symbol("String", "Var");
                symbols.put(cad, value);
            }
            Token = 259;
        }
        if (currentChar == '\n')
            lex.line += 1;
        return new Pointer(Token, cad);
    }

}
