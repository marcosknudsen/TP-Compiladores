package ar.tp.generacion;

import ar.tp.ast.Terceto;
import ar.tp.ast.Symbol;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Map;

public class GeneradorAsm {
    private final ArrayList<Terceto> reglas;
    private final Map<String, Symbol> ts;

    private final StringBuilder data = new StringBuilder();
    private final StringBuilder code = new StringBuilder();

    public GeneradorAsm(ArrayList<Terceto> reglas, Map<String, Symbol> ts) {
        this.reglas = reglas;
        this.ts = ts;
    }

    public void generar(String path) throws IOException {
        generarData();
        generarCodigo();

        try (PrintWriter pw = new PrintWriter(new FileWriter(path))) {
            pw.print(data);
            pw.println();
            pw.print(code);
            pw.println();
            pw.println("END main");
        }
    }

    private boolean esVar(Symbol s) {
        return s != null && "Var".equalsIgnoreCase(s.uso);
    }

    private boolean esParametro(Symbol s) {
        return s != null && "parametro".equalsIgnoreCase(s.uso);
    }

    private boolean esConstante(Symbol s) {
        return s != null && "cte".equalsIgnoreCase(s.uso);
    }

    private boolean produceValor(Terceto t) {
        if (t == null) return false;
        return switch (t.operand) {
            case "+", "-", "*", "/", ":=", "exec", "uitol" -> true;
            default -> false;
        };
    }

    private String mangle(String tsName) {
        return tsName.replace(':', '_');
    }

    private String tempName(int idx) {
        return "_T" + idx;
    }

    private void cargarEn(String reg, ar.tp.parser.ParserVal pv) {
        if (pv == null || pv.sval == null)
            return;

        String s = pv.sval;

        if (s.startsWith("[") && s.endsWith("]")) {
            int idx = ar.tp.parser.Parser.decode(s);
            code.append("    mov ").append(reg)
                    .append(", ").append(tempName(idx)).append("\n");
            return;
        }

        Symbol sym = ts.get(s);
        if (sym != null) {
            code.append("    mov ").append(reg)
                    .append(", ").append(mangle(s)).append("\n");
            return;
        }

        code.append("    mov ").append(reg)
                .append(", ").append(s).append("\n");
    }

    private void generarData() {

        data.append(".386\n");
        data.append(".model flat, stdcall\n");
        data.append("option casemap:none\n\n");

        data.append("include K:\\masm32\\include\\windows.inc\n");
        data.append("include K:\\masm32\\include\\kernel32.inc\n");
        data.append("include K:\\masm32\\include\\user32.inc\n\n");

        data.append("includelib K:\\masm32\\lib\\kernel32.lib\n");
        data.append("includelib K:\\masm32\\lib\\user32.lib\n\n");

        data.append(".data\n");

        for (Map.Entry<String, Symbol> e : ts.entrySet()) {
            String name = e.getKey();
            Symbol s = e.getValue();

            if ((esVar(s) || esParametro(s)) && !"main".equalsIgnoreCase(name)) {
                data.append("    ")
                        .append(mangle(name))
                        .append(" dd 0\n");
            }
        }

        for (int i = 0; i < reglas.size(); i++) {
            Terceto t = reglas.get(i);
            if (produceValor(t)) {
                data.append("    ")
                        .append(tempName(i))
                        .append(" dd 0\n");
            }
        }

        data.append("    msg_div_zero db \"Error: division por cero\", 0\n");
        data.append("    msg_overflow_mul db \"Error: overflow en multiplicacion\", 0\n");
        data.append("    msg_neg_uint db \"Error: resta negativa de uinteger\", 0\n");
    }

    private void generarCodigo() {
        code.append(".code\n");
        code.append("main PROC\n");

        for (int i = 0; i < reglas.size(); i++) {
            Terceto t = reglas.get(i);
            traducirTerceto(i, t);
        }

        code.append("    ; fin de programa\n");
        code.append("    ret\n");

        code.append("main ENDP\n\n");

        generarRutinasError();
    }


    private void traducirTerceto(int idx, Terceto t) {
        // System.out.println("ASM terceto ["+idx+"]: " + t.operand + " " + t.a.sval + " , " + t.b.sval);

        switch (t.operand) {
            case ":=" -> genAsignacion(idx, t);
            case "+"  -> genSuma(idx, t);
            case "-"  -> genResta(idx, t);
            case "*"  -> genProducto(idx, t);
            case "/"  -> genDivision(idx, t);
            case "uitol" -> genUitol(idx, t);

            default -> {
                // System.out.println("IGNORADO EN ASM ["+idx+"]: " + t.operand);
            }
        }
    }

    private void generarRutinasError() {
        code.append("_ERR_DIV_ZERO:\n");
        code.append("push 0\n");
        code.append("push OFFSET msg_div_zero\n");
        code.append("push OFFSET msg_div_zero\n");
        code.append("push 0\n");
        code.append("call MessageBoxA\n\n");
        code.append("push 0\n");
        code.append("call ExitProcess\n\n");

        code.append("_ERR_OVERFLOW_MUL:\n");
        code.append("push 0\n");
        code.append("push OFFSET msg_overflow_mul\n");
        code.append("push OFFSET msg_overflow_mul\n");
        code.append("push 0\n");
        code.append("call MessageBoxA\n\n");
        code.append("push 0\n");
        code.append("call ExitProcess\n\n");

        code.append("_ERR_NEG_UINT:\n");
        code.append("push 0\n");
        code.append("push OFFSET msg_neg_uint\n");
        code.append("push OFFSET msg_neg_uint\n");
        code.append("push 0\n");
        code.append("call MessageBoxA\n\n");
        code.append("push 0\n");
        code.append("call ExitProcess\n\n");
    }

    private void genAsignacion(int idx, Terceto t) {
        String lhs = t.a.sval;
        code.append("    ; [").append(idx).append("] :=\n");
        cargarEn("eax", t.b);

        code.append("    mov ").append(mangle(lhs)).append(", eax\n");
    }

    private void genSuma(int idx, Terceto t) {
        code.append("    ; [").append(idx).append("] +\n");
        cargarEn("eax", t.a);
        cargarEn("ebx", t.b);
        code.append("    add eax, ebx\n");
        code.append("    mov ").append(tempName(idx)).append(", eax\n");
    }

    private void genResta(int idx, Terceto t) {
        code.append("    ; [").append(idx).append("] -\n");
        cargarEn("eax", t.a);
        cargarEn("ebx", t.b);
        code.append("    sub eax, ebx\n");

        if ("uinteger".equalsIgnoreCase(t.tipo)) {
            code.append("    js _ERR_NEG_UINT\n");
        }

        code.append("    mov ").append(tempName(idx)).append(", eax\n");
    }

    private void genProducto(int idx, Terceto t) {
        code.append("    ; [").append(idx).append("] *\n");
        cargarEn("eax", t.a);
        cargarEn("ebx", t.b);
        code.append("    imul ebx\n");
        code.append("    jo _ERR_OVERFLOW_MUL\n");
        code.append("    mov ").append(tempName(idx)).append(", eax\n");
    }

    private void genDivision(int idx, Terceto t) {
        code.append("    ; [").append(idx).append("] /\n");

        cargarEn("eax", t.b);
        code.append("    cmp eax, 0\n");
        code.append("    je _ERR_DIV_ZERO\n");

        cargarEn("eax", t.a);
        code.append("    cdq\n");
        cargarEn("ebx", t.b);
        code.append("    idiv ebx\n");

        code.append("    mov ").append(tempName(idx)).append(", eax\n");
    }

    private void genUitol(int idx, Terceto t) {
        code.append("    ; [").append(idx).append("] uitol\n");
        cargarEn("eax", t.a);
        code.append("    mov ").append(tempName(idx)).append(", eax\n");
    }
}
