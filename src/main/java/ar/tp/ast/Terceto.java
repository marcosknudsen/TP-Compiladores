package ar.tp.ast;

import java.util.ArrayList;
import ar.tp.parser.*;

public class Terceto {
    public String operand;
    public ParserVal a, b;
    public String tipo;
    public ArrayList<String> errors;

    public Terceto(String operand, ParserVal a, ParserVal b) {
        this(operand, a, b, null, new ArrayList<>());
    }

    public Terceto(String operand, ParserVal a, ParserVal b, ArrayList<String> errors) {
        this(operand, a, b, null, errors == null ? new ArrayList<>() : errors);
    }

    public Terceto(String operand, ParserVal a, ParserVal b, String tipo, ArrayList<String> errors) {
        this.operand = operand;
        this.a = a;
        this.b = b;
        this.tipo = tipo;
        this.errors = errors == null ? new ArrayList<>() : errors;
    }

    public String showable(ParserVal a) {
        if (a.sval == null) return String.valueOf(a.ival);
        return a.sval;
    }

    @Override
    public String toString() {
        return operand + " " + showable(a) + " " + showable(b)
                + " " + (errors.size() > 0) + (tipo != null ? (" : " + tipo) : "");
    }
}
