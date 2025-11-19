%{
    import java.io.FileNotFoundException;
    import java.io.IOException;
    import java.util.ArrayList;
    import java.util.Stack;
    import java.util.HashMap;
    import ar.tp.lexer.Lex;
    import ar.tp.ast.*;
    import ar.tp.acciones.*;
    import ar.tp.parser.*;
%}

%token IF THEN ID ASSIGN ELSE BEGIN END END_IF PRINT WHILE DO FUN RETURN CTE CADENA UINTEGER LONGINT MAYOR_IGUAL MENOR_IGUAL DISTINTO
%start programa

%%

programa:ID bloque
;

bloque: BEGIN ss END opt_pyc
    | BEGIN END
;

bloqueejecutable: BEGIN ss END opt_pyc
;

bloquewhile: beginwhile ss END 
;

bloquefunct: beginfunct ss END opt_pyc
;

opt_pyc: ';'
 |
;

beginfunct: BEGIN {

    $$ = new ParserVal(
        crear_terceto(
            "begfunct",
            new ParserVal("-"),
            new ParserVal("-")
        )
    );

    pila.push(reglas.size());
    }
;

beginwhile: BEGIN {pila.push(reglas.size());}
;



cuerpo:
    bloqueejecutable
  | asignacion ';'
  | print ';'
  | iteracion ';'
  | retorno ';'
  ;


seleccion
  : IF condicionif THEN cuerpo
    {
      int posBF = pila.pop();
      Terceto tBF = reglas.get(posBF);
      tBF.b = new ParserVal("[" + reglas.size() + "]");
      reglas.set(posBF, tBF);
    }
    END_IF
| IF condicionif THEN cuerpo
  {
      int posBF = pila.pop();

      String sBI = crear_terceto("BI", new ParserVal("-"), new ParserVal("-"));
      int iBI = decode(sBI);

      Terceto tBF = reglas.get(posBF);
      tBF.b = new ParserVal("[" + (iBI + 1) + "]");
      reglas.set(posBF, tBF);

      pila.push(iBI);
    }
  ELSE cuerpo
  {
      int iBI = pila.pop();
      Terceto tBI = reglas.get(iBI);
      tBI.b = new ParserVal("[" + reglas.size() + "]");
      reglas.set(iBI, tBI);
  }
  END_IF
  ;

ss: ss s
    | s
;

s: declaracion
    | se
;

se
  : seleccion ';'
  | iteracion ';'
  | retorno ';'
  | asignacion ';'
  | print ';'
  | error ';' { System.out.println("ERROR on line " + lex.line + " sentencia invalida"); }
  ;

iteracion: DO bloquewhile WHILE condicionwhile {
     int posInicioDo = pila.pop();

     int posBF = reglas.size();
     crear_terceto("BF", $4, new ParserVal("-"));

     crear_terceto("BI", new ParserVal("[" + posInicioDo + "]"), new ParserVal("-"));

     Terceto tBF = reglas.get(posBF);
     tBF.b = new ParserVal("[" + reglas.size() + "]");
     reglas.set(posBF, tBF);
   }
;


condicionif: '(' condicion ')' {
    ParserVal pv = new ParserVal(
        crear_terceto("BF",
            new ParserVal("[" + (reglas.size()-1) + "]"),
            new ParserVal("-"))
    );
    pila.push(reglas.size()-1);
    $$ = pv;
}
;

condicionwhile: '(' condicion ')' { $$ = $2; }
;

condicion: expresion '>' expresion  {$$ = new ParserVal(crear_terceto(">", $1, $3));}
        | expresion '<' expresion  {$$ = new ParserVal(crear_terceto("<", $1, $3));}
        | expresion '=' expresion  {$$ = new ParserVal(crear_terceto("=", $1, $3));}
        | expresion MAYOR_IGUAL expresion {$$ = new ParserVal(crear_terceto(">=", $1, $3));}
        | expresion MENOR_IGUAL expresion {$$ = new ParserVal(crear_terceto("<=", $1, $3));}
        | expresion DISTINTO expresion {$$ = new ParserVal(crear_terceto("<>", $1, $3));}
        | expresion '>' {System.out.println("ERROR on line "+lex.line+": second expresion expected");}
        | expresion '<' {System.out.println("ERROR on line "+lex.line+": second expresion expected");}
        | expresion '=' {System.out.println("ERROR on line "+lex.line+": second expresion expected");}
        | expresion MAYOR_IGUAL {System.out.println("ERROR on line "+lex.line+": second expresion expected");}
        | expresion MENOR_IGUAL {System.out.println("ERROR on line "+lex.line+": second expresion expected");}
        | expresion DISTINTO {System.out.println("ERROR on line "+lex.line+": second expresion expected");}
;

parametro: tipodato ID
{
   String nombreMng = getVariableName($2.sval, 0);

   guardarVariable($2.sval, new Symbol($1.sval, "parametro"));

   crear_terceto("decl", $1, new ParserVal(nombreMng));

   $$ = new ParserVal(nombreMng);

   pilaString.push($1.sval);
}
    | ID {System.out.println("ERROR on line "+lex.line+": datatype expected");}
    | tipodato {System.out.println("ERROR on line "+lex.line+": identifier expected");}
;

retorno: RETURN '('expresion')' {
                                      ArrayList<String> errores = new ArrayList<>();

                                      String funActual = pilaFun.isEmpty() ? null : pilaFun.peek();

                                      String tRet = (funActual != null) ? tiposRetFunct.get(funActual) : null;
                                      String tExp = tipoDe($3);

                                      ParserVal expr = $3;

                                      if (isLong(tRet) && isUInt(tExp)) {
                                          expr = promoteToLong($3);
                                      } else if (isUInt(tRet) && isLong(tExp)) {
                                          if (!isConstWithinUInt($3)) {
                                              errores.add("type missmatch");
                                          }
                                      } else if (tRet != null && tExp != null && !tRet.equalsIgnoreCase(tExp)) {
                                          errores.add("type missmatch");
                                      }

                                      $$ = new ParserVal(crear_terceto("ret", expr, new ParserVal("-"), errores));
                                  }
    | RETURN '(' expresion {System.out.println("ERROR on line "+lex.line+": ')' expected");}
    | RETURN expresion ')' {System.out.println("ERROR on line "+lex.line+": '(' expected");}
;

asignacion: ID ASSIGN expresion
{
    String lhs = getVisibleVariableName($1.sval);
    String tL = tipoDe(new ParserVal(lhs));
    String tR = tipoDe($3);

    ParserVal rhs = $3;
    ArrayList<String> errores = new ArrayList<>();

    if (isLong(tL) && isUInt(tR)) {
        rhs = promoteToLong($3);
    }
    else if (isUInt(tL) && isLong(tR)) {
        errores.add("type mismatch");
    }

    $$ = new ParserVal(crear_terceto(":=", new ParserVal(lhs), rhs, errores));
}
    | ID ASSIGN { System.out.println("ERROR on line "+lex.line+": expresion expected"); }
    | ASSIGN expresion { System.out.println("ERROR on line "+lex.line+": identifier expected"); }
;

print: PRINT '(' CADENA ')'
{
String lit = "\"" + $3.sval + "\"";
$$ = new ParserVal(
      crear_terceto("print",
                    new ParserVal(lit),
                    new ParserVal("-")));
}
    | PRINT CADENA ')' {System.out.println("ERROR on line "+lex.line+": '(' expected");}
    | PRINT '(' CADENA {System.out.println("ERROR on line "+lex.line+": ')' expected");}
    | PRINT '(' ')' {System.out.println("ERROR on line "+lex.line+": String expected");}
;

declaracion: tipodato FUN identificadorfunct '(' parametro ')'
{
    tiposParFunct.put($3.sval, pilaString.pop());
    tiposRetFunct.put($3.sval, $1.sval);
}
bloquefunct
{
    ArrayList<String> errores = new ArrayList<>();
    if (buscarVariable($3.sval) != null) errores.add("declared");

    $$ = new ParserVal(crear_terceto("endfun", $3, new ParserVal("-"), errores));

    t = reglas.get(pila.peek() - 1);
    t.a = $3;
    t.b = $5;
    reglas.set(pila.pop() - 1, t);

    colaAmbito.remove(colaAmbito.size() - 1);
    if (!errores.contains("declared")) {
        guardarVariable($3.sval, new Symbol($1.sval, "Fun"));
    }
    pilaFun.pop();
}
    | tipodato FUN identificadorfunct '(' ')'
     {
         tiposParFunct.put($3.sval, "");
         tiposRetFunct.put($3.sval, $1.sval);
     }
     bloquefunct  {
                      ArrayList<String> errores = new ArrayList<>();
                      if (buscarVariable($3.sval) != null) errores.add("declared");

                      $$ = new ParserVal(crear_terceto("endfun", $3, new ParserVal("-"), errores));

                      t = reglas.get(pila.peek() - 1);
                      t.a = new ParserVal($3.sval);
                      reglas.set(pila.pop() - 1, t);

                      colaAmbito.remove(colaAmbito.size() - 1);
                      if (!errores.contains("declared")){
                          guardarVariable($3.sval,new Symbol($1.sval,"Fun"));
                      }
                      pilaFun.pop();
                  }
| tipodato listavariables ';' {
  for (String nombre : variables) {
      ArrayList<String> errores = new ArrayList<>();

      String key = getVariableName(nombre, 0);
      Symbol s = lex.symbols.get(key);

      if (s != null && (s.uso == null || s.uso.isEmpty())) {
          lex.symbols.remove(key);
          s = null;
      }

      boolean existeEnEsteAmbito = (s != null) &&
                                   (s.uso != null && !s.uso.isEmpty());

      if (existeEnEsteAmbito) {
          errores.add("declared");
      }

      String nombreMng = getVariableName(nombre, 0);
      crear_terceto("decl", $1, new ParserVal(nombreMng), errores);

      if (!existeEnEsteAmbito) {
          guardarVariable(nombre, new Symbol($1.sval, "Var"));
      }
  }
  variables.clear();
}
;

identificadorfunct: ID {
                           colaAmbito.add($1.sval + ":");
                           pilaFun.push($1.sval);
                       }
;


tipodato: UINTEGER  {$$ = $1;}
    | LONGINT  {$$ = $1;}
;

expresion : termino
    | expresion '+' termino
    {
      ParserVal a = $1, b = $3;
      String ta = tipoDe(a), tb = tipoDe(b);
      if (isLong(ta) && isUInt(tb)) b = promoteToLong(b);
      if (isLong(tb) && isUInt(ta)) a = promoteToLong(a);

      ArrayList<String> errores = new ArrayList<>();
      if (ta != null && tb != null && !ta.equals(tb)) {
        String ta2 = tipoDe(a), tb2 = tipoDe(b);
        if (ta2 != null && tb2 != null && !ta2.equals(tb2)) errores.add("datatype missmatch");
      }

      String s = crear_terceto("+", a, b, errores);
      int i = decode(s);

      String ra = tipoDe(a), rb = tipoDe(b);
      reglas.get(i).tipo = (isLong(ra) || isLong(rb)) ? "longint" : "uinteger";
      $$ = new ParserVal(s);
    }
    | expresion '-' termino
    {
      ParserVal a = $1, b = $3;
      String ta = tipoDe(a), tb = tipoDe(b);
      if (isLong(ta) && isUInt(tb)) b = promoteToLong(b);
      if (isLong(tb) && isUInt(ta)) a = promoteToLong(a);

      ArrayList<String> errores = new ArrayList<>();
      if (ta != null && tb != null && !ta.equals(tb)) {
        String ta2 = tipoDe(a), tb2 = tipoDe(b);
        if (ta2 != null && tb2 != null && !ta2.equals(tb2)) errores.add("datatype missmatch");
      }

      String s = crear_terceto("-", a, b, errores);
      int i = decode(s);
      String ra = tipoDe(a), rb = tipoDe(b);
      reglas.get(i).tipo = (isLong(ra) || isLong(rb)) ? "longint" : "uinteger";
      $$ = new ParserVal(s);
    }
;

termino  : factor
    | termino '*' factor
    {
        ParserVal a = $1, b = $3;
        String ta = tipoDe(a), tb = tipoDe(b);
        if (isLong(ta) && isUInt(tb)) b = promoteToLong(b);
        if (isLong(tb) && isUInt(ta)) a = promoteToLong(a);

        ArrayList<String> errores = new ArrayList<>();
        if (ta != null && tb != null && !ta.equals(tb)) {
         String ta2 = tipoDe(a), tb2 = tipoDe(b);
         if (ta2 != null && tb2 != null && !ta2.equals(tb2)) errores.add("datatype missmatch");
        }

        String s = crear_terceto("*", a, b, errores);
        int i = decode(s);
        String ra = tipoDe(a), rb = tipoDe(b);
        reglas.get(i).tipo = (isLong(ra) || isLong(rb)) ? "longint" : "uinteger";
        $$ = new ParserVal(s);
    }
    | termino '/' factor
    {
        ParserVal a = $1, b = $3;
        String ta = tipoDe(a), tb = tipoDe(b);
        if (isLong(ta) && isUInt(tb)) b = promoteToLong(b);
        if (isLong(tb) && isUInt(ta)) a = promoteToLong(a);

        ArrayList<String> errores = new ArrayList<>();
        if (ta != null && tb != null && !ta.equals(tb)) {
         String ta2 = tipoDe(a), tb2 = tipoDe(b);
         if (ta2 != null && tb2 != null && !ta2.equals(tb2)) errores.add("datatype missmatch");
        }

        String s = crear_terceto("/", a, b, errores);
        int i = decode(s);
        String ra = tipoDe(a), rb = tipoDe(b);
        reglas.get(i).tipo = (isLong(ra) || isLong(rb)) ? "longint" : "uinteger";
        $$ = new ParserVal(s);
    }
;

factor
  : ID { $$ = new ParserVal(getVisibleVariableName($1.sval)); }
  | CTE
  | '-' CTE
  | invocacion
  ;

listavariables: ID ',' listavariables {variables.add($1.sval);}
    | ID {variables.add($1.sval);}
;

invocacion
 : ID '(' ')'  {
                     String s = crear_terceto("exec", $1, new ParserVal("-"));
                     int i = decode(s);
                     String tret = tiposRetFunct.get($1.sval);
                     reglas.get(i).tipo = (tret != null) ? tret.toLowerCase() : null;
                     $$ = new ParserVal(s);
                 }
| ID '(' expresion ')'
  {
         ArrayList<String> errores = new ArrayList<>();
         String tArg    = tipoDe($3);
         String tFormal = tiposParFunct.get($1.sval);

         ParserVal arg = $3;

         if (tFormal == null) {
             errores.add("func-undeclared");
         } else if (isLong(tFormal) && isUInt(tArg)) {
             arg = promoteToLong($3);
         } else if (isUInt(tFormal) && isLong(tArg)) {
             if (!isConstWithinUInt($3)) {
                 errores.add("type missmatch");
             }
         } else if (tArg != null && !tArg.equalsIgnoreCase(tFormal)) {
             errores.add("type missmatch");
         }

         String s = crear_terceto("exec", $1, arg, errores);
         int i = decode(s);
         String tret = tiposRetFunct.get($1.sval);
         reglas.get(i).tipo = (tret != null) ? tret.toLowerCase() : null;

         $$ = new ParserVal(s);
     }
;


%%

static Lex lex = null;
static Parser par = null;
int index = 0;
public static ArrayList<Terceto> reglas = new ArrayList<Terceto>();
static ArrayList<String> variables = new ArrayList<String>();
public static Stack<Integer> pila = new Stack<>();
static Stack<String> pilaString = new Stack<>();
static ArrayList<String> colaAmbito = new ArrayList<String>();
static HashMap<String,String> tiposParFunct = new HashMap<>();
static HashMap<String,String> tiposRetFunct = new HashMap<>();
static Stack<String> pilaFun = new Stack<>();

ar.tp.parser.ParserVal PV;


int pointer;
Terceto t;

public int runParser() {
    reglas.clear();
    pila.clear();
    pilaString.clear();
    colaAmbito.clear();
    tiposParFunct.clear();
    tiposRetFunct.clear();
    lex.symbols.clear();

    int rc = yyparse();

    boolean hayErrores = false;

    for (int i = 0; i < reglas.size(); i++) {
        Terceto t = reglas.get(i);
        if (t.errors != null && !t.errors.isEmpty()) {
            hayErrores = true;

            System.out.println(
                "SEMANTIC ERROR in [" + i + "] " +
                t.toString() + " -> " + t.errors
            );
        }
    }

    if (hayErrores) {
        System.out.println("COMPILATION FAILED: semantic errors detected.");
        return 1;
    }

    return rc;
}

public static void setLexer(Lex l) {
     lex = l; 
}

int yylex() {
  int token;
  try {
    token = lex.getToken();
    yylval = new ar.tp.parser.ParserVal(lex.yylval);
    //System.out.println("TOK " + token + "  lexeme='" + lex.yylval + "'  line=" + lex.line);
  } catch (IOException e) {
    token = -1;
  }
  return token;
}

void yyerror(String s){
    System.out.println(s + " on line " + lex.line);
}

String crear_terceto(String operando, ParserVal a, ParserVal b){
    Terceto t = new Terceto(operando, a, b);
    reglas.add(t);
    return "["+Integer.toString(reglas.indexOf(t))+"]";
}

String crear_terceto(String operando, ParserVal a, ParserVal b, ArrayList<String> errores){
    Terceto t = new Terceto(operando, a, b,errores);
    reglas.add(t);
    return "["+Integer.toString(reglas.indexOf(t))+"]";
}

public static int mostrarPila(Stack<Integer> pila){
    if (pila.empty()){
        System.out.println("Pila Vacía");
    }
    else{
        for (int i = 0 ; i < pila.size() ; i++){
            System.out.println(pila.get(i));
        }
    }

    return 0;
}

public static int mostrarReglas(ArrayList<Terceto> reglas){
    if (reglas.size() == 0){
        System.out.println("No hay reglas");
    }
    else{
        for (int i = 0 ; i < reglas.size() ; i++){
            System.out.println("["+i+"] "+reglas.get(i).toString());
        }
    }
    return 0;
}

static void mostrarTS(){
    for (String name: lex.symbols.keySet()) {
        String key = name.toString();
        String value = lex.symbols.get(name).toString();
        System.out.println(key + " " + value);
    }
}

Symbol buscarVariable(String nombre){
    Symbol variable;
    int N=0;
    do {
        variable=lex.symbols.get(getVariableName(nombre,N++));
    } while (variable == null && N < colaAmbito.size());
    return variable;
}

static String getVisibleVariableName(String nombre){
    for (int n = 0; n <= colaAmbito.size(); n++) {
        String key = getVariableName(nombre, n);
        if (lex.symbols.containsKey(key)){
            return key;
        }
    }
    return nombre;
}

String tipoDe(ar.tp.parser.ParserVal v) {
    if (v == null || v.sval == null){
        return null;
    }
    String s = v.sval;

    if (s.length() >= 3 && s.charAt(0) == '[' && s.charAt(s.length() - 1) == ']') {
        try {
            int idx = decode(s);
            Terceto tt = reglas.get(idx);
            return tt.tipo;
        } catch (Exception e){
            return null;
        }
    }

    Symbol sv = buscarVariable(s);
    if (sv != null){
        return sv.tipo;
    }

    Symbol sc = lex.symbols.get(s);
    if (sc != null){
        return sc.tipo;
    }

    return null;
}


static String getVariableName(String nombre, int n){
    String sufijo = "";
    for (int i = 0; i < colaAmbito.size() - n; i++){
        sufijo = sufijo + colaAmbito.get(i);
    }
    return sufijo + nombre;
}

void guardarVariable(String nombre, Symbol s){
    lex.symbols.put(getVariableName(nombre, 0), s);
}

public static int decode(String str){
    str = str.substring(1, str.length() - 1);
    return Integer.valueOf(str);
}

boolean declaradoEnEsteAmbito(String nombre) {
    String key = getVariableName(nombre, 0);
    Symbol s = lex.symbols.get(key);
    if (s == null){
        return false;
    }

    return s.uso != null && !s.uso.isEmpty();
}

static boolean isUInt(String t){
    return t != null && t.equalsIgnoreCase("uinteger");

}

static boolean isLong(String t){
    return t != null && t.equalsIgnoreCase("longint");
}

ParserVal promoteToLong(ParserVal v) {
    String tv = tipoDe(v);
    if (isUInt(tv)) {
        String s = crear_terceto("uitol", v, new ParserVal("-"));
        int i = decode(s);
        reglas.get(i).tipo = "longint";
        return new ParserVal(s);
    }
    return v;
}


static boolean isConstWithinUInt(ParserVal v) {
    if (v == null || v.sval == null){
        return false;
    }

    Symbol s = lex.symbols.get(v.sval);
    if (s == null || !"cte".equalsIgnoreCase(s.uso)){
        return false;
    }

    try {
        long val = Long.parseLong(v.sval);
        return 0 <= val && val <= 65535;
    } catch (NumberFormatException e) {
        return false;
    }
}
