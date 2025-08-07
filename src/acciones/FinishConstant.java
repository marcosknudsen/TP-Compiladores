package acciones;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;

import classes.*;
import lex.Lex;

public class FinishConstant extends SemanticAction {

        @Override
        public Pointer ejecutar(BufferedReader sourceCode, Lex lex, int currentChar,
                        HashMap<String, Symbol> symbols, HashMap<String, Integer> reservedWords) throws IOException {
                try {
                        Symbol symbol = symbols.get(lex.getString());
                        if (symbol == null)
                                symbols.put(lex.getString(),
                                                new Symbol(Integer.parseInt(lex.getString()) > 65535 ? "longint"
                                                                : "uinteger", "Constante"));
                        lex.yylval = lex.getString();
                        sourceCode.reset();
                        if (Integer.parseInt(lex.getString()) > 65535)
                                return new Pointer(273);
                        else
                                return new Pointer(272);

                } catch (NumberFormatException e) {
                        System.out.println("ERROR on line " + lex.line
                                        + ": El valor ingresado no se encuentra dentro del rango aceptado");
                        return new Pointer(-1);
                }
        }
}
