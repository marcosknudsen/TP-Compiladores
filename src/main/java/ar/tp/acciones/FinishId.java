package ar.tp.acciones;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;

import ar.tp.ast.Pointer;
import ar.tp.ast.Symbol;
import ar.tp.lexer.Lex;

public class FinishId extends SemanticAction {

    @Override
    public Pointer ejecutar(BufferedReader sourceCode,
                            Lex lex,
                            int currentChar,
                            HashMap<String, Symbol> symbols,
                            HashMap<String, Integer> reservedWords) throws IOException {
        String cad = lex.getString();
        if (cad.length() > 25) {
            System.out.println(String.format(
                    "The string %s is shortened to 25 characters: %s",
                    cad, cad.substring(0, 25)));
            cad = cad.substring(0, 25);
        }

        // ¿Es palabra reservada?
        Integer tok = reservedWords.get(cad.toLowerCase());
        lex.yylval = cad;

        if (tok != null) {
            sourceCode.reset();
            return new Pointer(tok, cad);
        }

        // **NO declarar** el identificador aquí.
        // Opción 1 (recomendada): no tocar la TS
        // Opción 2: si querés que exista para tooling, usar placeholder que NO cuenta como declarado
        Symbol s = symbols.get(cad);
        if (s == null) {
            symbols.put(cad, new Symbol(null, "")); // tipo=null, uso=""
        }

        // Devolver token ID (en tu gramática ID=259)
        sourceCode.reset();
        return new Pointer(259, cad);
    }
}
