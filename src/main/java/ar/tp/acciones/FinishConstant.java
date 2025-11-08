package ar.tp.acciones;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import ar.tp.ast.*;
import ar.tp.lexer.Lex;

public class FinishConstant extends SemanticAction {
    private static final long UINT_MAX = 65535L;           // 16-bit sin signo
    private static final long LONG_MIN = -2147483648L;     // 32-bit con signo
    private static final long LONG_MAX =  2147483647L;

    @Override
    public Pointer ejecutar(BufferedReader sourceCode, Lex lex, int currentChar,
                            HashMap<String, Symbol> symbols,
                            HashMap<String, Integer> reservedWords) throws IOException {
        sourceCode.reset();                 // devolvé el lookahead

        final String lexeme = lex.getString();
        lex.yylval = lexeme;

        // Parse robusto como long (sin signo en el literal, el '-' lo maneja el parser)
        long value;
        try {
            value = Long.parseLong(lexeme);
        } catch (NumberFormatException e) {
            System.out.println("ERROR on line " + lex.line + ": entero inválido.");
            return new Pointer(-1);
        }

        // Chequeo de rango del TP (podés ajustar si tu consigna usa otro)
        if (value < 0 && value < LONG_MIN || value > LONG_MAX) {
            System.out.println("ERROR on line " + lex.line + ": literal fuera de rango de longint (32 bits).");
            return new Pointer(-1);
        }

        // Tipar en la TS: uinteger si 0..65535; si no, longint
        final String tipo = (0 <= value && value <= UINT_MAX) ? "uinteger" : "longint";
        symbols.putIfAbsent(lexeme, new Symbol(tipo, "cte"));   // <<< uso = "cte"

        // SIEMPRE devolvemos CTE
        return new Pointer(ar.tp.parser.Parser.CTE, lexeme);
    }
}
