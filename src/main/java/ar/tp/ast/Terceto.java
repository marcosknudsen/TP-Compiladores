package ar.tp.ast;

import java.util.ArrayList;
import ar.tp.parser.*;

public class Terceto {
    String operand;
    public ar.tp.parser.ParserVal a, b;
    ArrayList<String> errors;

    public Terceto(String operand, ParserVal a, ParserVal b) {
        this.operand = operand;
        this.a = a;
        this.b = b;
        this.errors = new ArrayList<String>();
    }

    public Terceto(String operand, ParserVal a, ParserVal b, ArrayList<String> errors) {
        this.operand = operand;
        this.a = a;
        this.b = b;
        this.errors = errors;
    }

    public String showable(ParserVal a) {
        if (a.sval == null) {
            return String.valueOf(a.ival);
        }
        return a.sval;
    }

    @Override
    public String toString() {
        return this.operand + " " + showable(this.a) + " " + showable(this.b) + " "
                + String.valueOf(this.errors.size() > 0);
    }
}
