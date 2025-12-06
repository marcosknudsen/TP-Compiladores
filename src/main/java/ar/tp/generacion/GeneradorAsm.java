package ar.tp.generacion;

import ar.tp.ast.Terceto;
import ar.tp.ast.Symbol;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class GeneradorAsm {
    private final ArrayList<Terceto> reglas;
    private final Map<String, Symbol> ts;

    private final StringBuilder data = new StringBuilder();
    private final StringBuilder code = new StringBuilder();

    private final Map<String, Integer> funStart = new HashMap<>();
    private final Map<String, Integer> funEnd   = new HashMap<>();
    private final boolean[] esDeFuncion;
    private final boolean[] esLabel;
    private final Map<String, String> printLabels = new HashMap<>();

    public GeneradorAsm(ArrayList<Terceto> reglas, Map<String, Symbol> ts) {
        this.reglas = reglas;
        this.ts = ts;
        this.esDeFuncion = new boolean[reglas.size()];
        this.esLabel = new boolean[reglas.size() + 1];

        String funActual = null;
        int begIdx = -1;

        for (int i = 0; i < reglas.size(); i++) {
            Terceto t = reglas.get(i);
            if ("begfunct".equals(t.operand)) {
                funActual = t.a.sval;
                begIdx = i;
                funStart.put(funActual, i);
            } else if ("endfun".equals(t.operand)) {
                String f = t.a.sval;
                funEnd.put(f, i);

                for (int j = begIdx + 1; j < i; j++) {
                    esDeFuncion[j] = true;
                }

                funActual = null;
                begIdx = -1;
            }
        }

        for (int i = 0; i < reglas.size(); i++) {
            Terceto t = reglas.get(i);
            if ("BF".equals(t.operand)) {
                if (t.b != null && t.b.sval != null) {
                    int dest = ar.tp.parser.Parser.decode(t.b.sval);
                    if (dest >= 0 && dest < esLabel.length) {
                        esLabel[dest] = true;
                    }
                }
            } else if ("BI".equals(t.operand)) {
                String target = null;

                if (t.a != null && t.a.sval != null && t.a.sval.startsWith("[") && t.a.sval.endsWith("]")) {
                    target = t.a.sval;
                } else if (t.b != null && t.b.sval != null && t.b.sval.startsWith("[") && t.b.sval.endsWith("]")) {
                    target = t.b.sval;
                }

                if (target != null) {
                    int dest = ar.tp.parser.Parser.decode(target);
                    if (dest >= 0 && dest < esLabel.length) {
                        esLabel[dest] = true;
                    }
                }
            }
        }

        int printCount = 0;

        for (Terceto t : reglas) {
            if ("print".equals(t.operand) && t.a != null && t.a.sval != null) {
                String lit = t.a.sval;
                if (!printLabels.containsKey(lit)) {
                    String label = "msg_print_" + printCount++;
                    printLabels.put(lit, label);
                }
            }
        }
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

    private boolean isUIntType(String tipo) {
        return tipo != null && tipo.equalsIgnoreCase("uinteger");
    }

    private boolean produceValor(Terceto t) {
        if (t == null) return false;
        return switch (t.operand) {
            case "+", "-", "*", "/", ":=", "exec", "uitol", "ret" -> true;
            default -> false;
        };
    }

    private String mangle(String tsName) {
        return "_v_" + tsName.replace(':', '_');
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
            code.append("    mov ")
                    .append(reg)
                    .append(", ")
                    .append(tempName(idx))
                    .append("\n");
            return;
        }

        Symbol sym = ts.get(s);
        if (sym != null) {
            if (esConstante(sym)) {
                code.append("    mov ")
                        .append(reg)
                        .append(", ")
                        .append(s)
                        .append("\n");
            } else {
                code.append("    mov ")
                        .append(reg)
                        .append(", ")
                        .append(mangle(s))
                        .append("\n");
            }
            return;
        }

        code.append("    mov ")
                .append(reg)
                .append(", ")
                .append(s)
                .append("\n");
    }

    private String tipoDeOperand(ar.tp.parser.ParserVal pv) {
        if (pv == null || pv.sval == null) return null;
        String s = pv.sval;

        if (s.startsWith("[") && s.endsWith("]")) {
            int idx = ar.tp.parser.Parser.decode(s);
            if (idx >= 0 && idx < reglas.size()) {
                Terceto tt = reglas.get(idx);
                return tt.tipo;
            }
        }

        Symbol sym = ts.get(s);
        return sym != null ? sym.tipo : null;
    }

    private void generarData() {

        data.append(".386\n");
        data.append(".model flat, stdcall\n");
        data.append("option casemap:none\n\n");

        data.append("include \\masm32\\include\\windows.inc\n");
        data.append("include \\masm32\\include\\kernel32.inc\n");
        data.append("include \\masm32\\include\\user32.inc\n");

        data.append("includelib \\masm32\\lib\\kernel32.lib\n");
        data.append("includelib \\masm32\\lib\\user32.lib\n");
        data.append(".data\n");

        for (Map.Entry<String, Symbol> e : ts.entrySet()) {
            String name = e.getKey();
            Symbol s = e.getValue();

            if ((esVar(s) || esParametro(s)) && !"main".equalsIgnoreCase(name)) {
                data.append("    ")
                        .append(mangle(name))
                        .append(" ")
                        .append(isUIntType(s.tipo) ? "dw 0" : "dd 0")
                        .append("\n");
            }
        }

        for (int i = 0; i < reglas.size(); i++) {
            Terceto t = reglas.get(i);
            if (produceValor(t)) {
                data.append("    ")
                        .append(tempName(i))
                        .append(" ")
                        .append(isUIntType(t.tipo) ? "dw 0" : "dd 0")
                        .append("\n");
            }
        }

        for (Map.Entry<String, String> e : printLabels.entrySet()) {
            String lit = e.getKey();
            String label = e.getValue();

            data.append("    ")
                    .append(label)
                    .append(" db ")
                    .append(lit)
                    .append(", 0\n");
        }

        data.append("    msg_div_zero db \"Error: division por cero\", 0\n");
        data.append("    msg_overflow_mul db \"Error: overflow en multiplicacion\", 0\n");
        data.append("    msg_neg_uint db \"Error: resta negativa de uinteger\", 0\n");
    }

    private void generarCodigo() {
        code.append(".code\n");
        code.append("start:\n");

        for (int i = 0; i < reglas.size(); i++) {
            Terceto t = reglas.get(i);
            if (esDeFuncion[i]) continue;

            if (esLabel[i]) {
                code.append(labelName(i)).append(":\n");
            }

            traducirTerceto(i, t);
        }

        if (esLabel[reglas.size()]) {
            code.append(labelName(reglas.size())).append(":\n");
        }

        code.append("    ; fin de programa\n");
        code.append("    invoke ExitProcess, 0\n\n");

        for (Map.Entry<String, Integer> e : funStart.entrySet()) {
            String fun = e.getKey();
            Integer ini = e.getValue();
            Integer fin = funEnd.get(fun);
            if (ini == null || fin == null) continue;

            code.append(funLabel(fun)).append(":\n");

            for (int i = ini + 1; i < fin; i++) {
                if (perteneceAOtraFuncion(fun, i)) {
                    continue;
                }

                Terceto t = reglas.get(i);

                if ("decl".equals(t.operand)) continue;

                if (esLabel[i]) {
                    code.append(labelName(i)).append(":\n");
                }

                traducirTerceto(i, t);
            }

            code.append("\n");
        }

        generarRutinasError();

        code.append("end start\n");
    }


    private void traducirTerceto(int idx, Terceto t) {
        switch (t.operand) {
            case ":=" -> genAsignacion(idx, t);
            case "+"  -> genSuma(idx, t);
            case "-"  -> genResta(idx, t);
            case "*"  -> genProducto(idx, t);
            case "/"  -> genDivision(idx, t);
            case "uitol" -> genUitol(idx, t);
            case "exec" -> genExec(idx, t);
            case "print" -> genPrint(idx, t);
            case "ret"   -> genRet(idx, t);
            case "<", ">", "<=", ">=", "=", "<>", "==", "!=" -> genComparacion(idx, t);
            case "BF" -> genBF(idx, t);
            case "BI" -> genBI(idx, t);

            default -> {
                System.out.println("IGNORADO EN ASM ["+idx+"]: " + t.operand);
            }
        }
    }

    private void genRet(int idx, Terceto t) {
        code.append("    ; [").append(idx).append("] ret\n");

        String funName = null;
        for (Map.Entry<String, Integer> e : funStart.entrySet()) {
            String f = e.getKey();
            Integer ini = e.getValue();
            Integer fin = funEnd.get(f);
            if (ini == null || fin == null) continue;

            if (idx > ini && idx < fin) {
                funName = f;
                break;
            }
        }

        boolean isUInt = false;
        if (funName != null) {
            Symbol funSym = ts.get(funName);
            if (funSym != null && isUIntType(funSym.tipo)) {
                isUInt = true;
            }
        }

        String reg = isUInt ? "ax" : "eax";

        cargarEn(reg, t.a);

        code.append("    ret\n");
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
        Symbol s = ts.get(lhs);
        boolean isUInt = s != null && isUIntType(s.tipo);
        String reg = isUInt ? "ax" : "eax";

        code.append("    ; [").append(idx).append("] :=\n");
        cargarEn(reg, t.b);

        code.append("    mov ").append(mangle(lhs)).append(", ").append(reg).append("\n");
    }

    private void genSuma(int idx, Terceto t) {
        code.append("    ; [").append(idx).append("] +\n");
        boolean isUInt = isUIntType(t.tipo);
        String ra = isUInt ? "ax" : "eax";
        String rb = isUInt ? "bx" : "ebx";

        cargarEn(ra, t.a);
        cargarEn(rb, t.b);
        code.append("    add ").append(ra).append(", ").append(rb).append("\n");
        code.append("    mov ").append(tempName(idx)).append(", ").append(ra).append("\n");
    }

    private void genResta(int idx, Terceto t) {
        code.append("    ; [").append(idx).append("] -\n");
        boolean isUInt = isUIntType(t.tipo);
        String ra = isUInt ? "ax" : "eax";
        String rb = isUInt ? "bx" : "ebx";

        cargarEn(ra, t.a);
        cargarEn(rb, t.b);
        code.append("    sub ").append(ra).append(", ").append(rb).append("\n");

        if (isUInt) {
            code.append("    js _ERR_NEG_UINT\n");
        }

        code.append("    mov ").append(tempName(idx)).append(", ").append(ra).append("\n");
    }

    private void genProducto(int idx, Terceto t) {
        code.append("    ; [").append(idx).append("] *\n");
        boolean isUInt = isUIntType(t.tipo);

        if (isUInt) {
            cargarEn("ax", t.a);
            cargarEn("bx", t.b);
            code.append("    mul bx\n");
            code.append("    cmp dx, 0\n");
            code.append("    jne _ERR_OVERFLOW_MUL\n");
            code.append("    mov ").append(tempName(idx)).append(", ax\n");
        } else {
            cargarEn("eax", t.a);
            cargarEn("ebx", t.b);
            code.append("    imul ebx\n");
            code.append("    jo _ERR_OVERFLOW_MUL\n");
            code.append("    mov ").append(tempName(idx)).append(", eax\n");
        }
    }

    private void genDivision(int idx, Terceto t) {
        code.append("    ; [").append(idx).append("] /\n");
        boolean isUInt = isUIntType(t.tipo);

        if (isUInt) {
            cargarEn("ax", t.b);
            code.append("    cmp ax, 0\n");
            code.append("    je _ERR_DIV_ZERO\n");

            cargarEn("ax", t.a);
            code.append("    xor dx, dx\n");
            cargarEn("bx", t.b);
            code.append("    div bx\n");
            code.append("    mov ").append(tempName(idx)).append(", ax\n");
        } else {
            cargarEn("eax", t.b);
            code.append("    cmp eax, 0\n");
            code.append("    je _ERR_DIV_ZERO\n");

            cargarEn("eax", t.a);
            code.append("    cdq\n");
            cargarEn("ebx", t.b);
            code.append("    idiv ebx\n");

            code.append("    mov ").append(tempName(idx)).append(", eax\n");
        }
    }

    private void genUitol(int idx, Terceto t) {
        code.append("    ; [").append(idx).append("] uitol\n");
        cargarEn("ax", t.a);
        code.append("    movzx eax, ax\n");
        code.append("    mov ").append(tempName(idx)).append(", eax\n");
    }

    private void genExec(int idx, Terceto t) {
        code.append("    ; [").append(idx).append("] exec\n");

        String fun = t.a.sval;

        String paramKey = null;
        for (String key : ts.keySet()) {
            if (key.startsWith(fun + ":")) {
                paramKey = key;
                break;
            }
        }

        Symbol paramSym = (paramKey != null) ? ts.get(paramKey) : null;
        boolean paramIsUInt = paramSym != null && "uinteger".equalsIgnoreCase(paramSym.tipo);

        if (paramKey != null && t.b != null && t.b.sval != null && !"-".equals(t.b.sval)) {
            String argReg = paramIsUInt ? "ax" : "eax";
            cargarEn(argReg, t.b);

            code.append("    mov ")
                    .append(mangle(paramKey))
                    .append(", ")
                    .append(argReg)
                    .append("\n");
        }

        code.append("    call ")
                .append(funLabel(fun))
                .append("\n");

        Symbol funSym = ts.get(fun);
        boolean retIsUInt = funSym != null && "uinteger".equalsIgnoreCase(funSym.tipo);
        String retReg = retIsUInt ? "ax" : "eax";

        code.append("    mov ")
                .append(tempName(idx))
                .append(", ")
                .append(retReg)
                .append("\n");
    }

    private String labelName(int idx) {
        return "L" + idx;
    }

    private void genComparacion(int idx, Terceto t) {
        code.append("    ; [").append(idx).append("] ").append(t.operand).append("\n");

        String ta = tipoDeOperand(t.a);
        String tb = tipoDeOperand(t.b);

        boolean useLong =
                "longint".equalsIgnoreCase(ta) ||
                        "longint".equalsIgnoreCase(tb);

        if (useLong) {
            if (isUIntType(ta)) {
                cargarEn("ax", t.a);
                code.append("    movzx eax, ax\n");
            } else {
                cargarEn("eax", t.a);
            }

            cargarEn("ebx", t.b);
            code.append("    cmp eax, ebx\n");
        } else {
            cargarEn("ax", t.a);
            cargarEn("bx", t.b);
            code.append("    cmp ax, bx\n");
        }
    }

    private void genBF(int idx, Terceto t) {
        code.append("    ; [").append(idx).append("] BF\n");

        int condIdx = ar.tp.parser.Parser.decode(t.a.sval);
        Terceto cond = reglas.get(condIdx);

        int dest = ar.tp.parser.Parser.decode(t.b.sval);
        String op = cond.operand;

        String tLeft  = tipoDeOperand(cond.a);
        String tRight = tipoDeOperand(cond.b);
        boolean leftIsUInt  = isUIntType(tLeft);
        boolean rightIsUInt = isUIntType(tRight);
        boolean isUInt = leftIsUInt && rightIsUInt;

        String jmpFalse;

        if (isUInt) {
            switch (op) {
                case "<"  -> jmpFalse = "jae";
                case ">"  -> jmpFalse = "jbe";
                case "<=" -> jmpFalse = "ja";
                case ">=" -> jmpFalse = "jb";
                case "=", "=="  -> jmpFalse = "jne";
                case "<>", "!=" -> jmpFalse = "je";
                default         -> jmpFalse = "je";
            }
        } else {
            switch (op) {
                case "<"  -> jmpFalse = "jge";
                case ">"  -> jmpFalse = "jle";
                case "<=" -> jmpFalse = "jg";
                case ">=" -> jmpFalse = "jl";
                case "=", "=="  -> jmpFalse = "jne";
                case "<>", "!=" -> jmpFalse = "je";
                default         -> jmpFalse = "je";
            }
        }

        code.append("    ")
                .append(jmpFalse)
                .append(" ")
                .append(labelName(dest))
                .append("\n");
    }

    private void genBI(int idx, Terceto t) {
        code.append("    ; [").append(idx).append("] BI\n");

        String target = null;

        if (t.a != null && t.a.sval != null && t.a.sval.startsWith("[") && t.a.sval.endsWith("]")) {
            target = t.a.sval;
        } else if (t.b != null && t.b.sval != null && t.b.sval.startsWith("[") && t.b.sval.endsWith("]")) {
            target = t.b.sval;
        }

        if (target == null) {
            return;
        }

        int dest = ar.tp.parser.Parser.decode(target);

        code.append("    jmp ")
                .append(labelName(dest))
                .append("\n");
    }

    private void genPrint(int idx, Terceto t) {
        code.append("    ; [").append(idx).append("] print\n");

        if (t.a == null || t.a.sval == null) {
            return;
        }

        String lit = t.a.sval;
        String label = printLabels.get(lit);
        if (label == null) {
            return;
        }

        code.append("    push 0\n");
        code.append("    push OFFSET ").append(label).append("\n");
        code.append("    push OFFSET ").append(label).append("\n");
        code.append("    push 0\n");
        code.append("    call MessageBoxA\n");
    }

    private String funLabel(String fun) {
        return "fun_" + fun;
    }

    private boolean perteneceAOtraFuncion(String funActual, int idx) {
        Integer iniActual = funStart.get(funActual);
        Integer finActual = funEnd.get(funActual);
        if (iniActual == null || finActual == null) {
            return false;
        }

        for (Map.Entry<String, Integer> e : funStart.entrySet()) {
            String f = e.getKey();
            if (f.equals(funActual)) {
                continue;
            }

            Integer ini = e.getValue();
            Integer fin = funEnd.get(f);
            if (ini == null || fin == null) {
                continue;
            }

            boolean funcionInternaDeActual = ini > iniActual && fin < finActual;
            if (!funcionInternaDeActual) {
                continue;
            }

            if (idx > ini && idx < fin) {
                return true;
            }
        }
        return false;
    }
}
