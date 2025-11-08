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
        if (a.sval != null) return a.sval;
        if (a.ival != 0) return Integer.toString(a.ival);
        if (a.obj != null) return a.obj.toString();
        return "-";
    }

    @Override
    public String toString() {
        // Núcleo estable para los goldens:
        String core = String.format("( %s , %s , %s )", operand,showable(a),showable(b));

        // Si querés dejar trazas de tipo/errores para debug:
        if ((tipo != null && !tipo.isEmpty()) || (errors != null && !errors.isEmpty())) {
            StringBuilder sb = new StringBuilder(core);
            if (tipo != null && !tipo.isEmpty()) sb.append(" :").append(tipo);
            if (errors != null && !errors.isEmpty()) sb.append("  errors=").append(errors);
            return sb.toString();
        }
        return core;
    }
}
