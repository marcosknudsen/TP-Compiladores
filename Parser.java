//### This file created by BYACC 1.8(/Java extension  1.15)
//### Java capabilities added 7 Jan 97, Bob Jamison
//### Updated : 27 Nov 97  -- Bob Jamison, Joe Nieten
//###           01 Jan 98  -- Bob Jamison -- fixed generic semantic constructor
//###           01 Jun 99  -- Bob Jamison -- added Runnable support
//###           06 Aug 00  -- Bob Jamison -- made state variables class-global
//###           03 Jan 01  -- Bob Jamison -- improved flags, tracing
//###           16 May 01  -- Bob Jamison -- added custom stack sizing
//###           04 Mar 02  -- Yuval Oren  -- improved java performance, added options
//###           14 Mar 02  -- Tomas Hurka -- -d support, static initializer workaround
//### Please send bug reports to tom@hukatronic.cz
//### static char yysccsid[] = "@(#)yaccpar	1.8 (Berkeley) 01/20/90";



package ar.tp.parser;



//#line 2 ".\src\main\resources\grammar\gramatica.y"
    import java.io.FileNotFoundException;
    import java.io.IOException;
    import java.util.ArrayList;
    import java.util.Stack;
    import java.util.HashMap;
    import ar.tp.lexer.Lex;
    import ar.tp.ast.*;
    import ar.tp.acciones.*;
    import ar.tp.parser.*;
//#line 27 "Parser.java"




public class Parser
{

boolean yydebug;        //do I want debug output?
int yynerrs;            //number of errors so far
int yyerrflag;          //was there an error?
int yychar;             //the current working character

//########## MESSAGES ##########
//###############################################################
// method: debug
//###############################################################
void debug(String msg)
{
  if (yydebug)
    System.out.println(msg);
}

//########## STATE STACK ##########
final static int YYSTACKSIZE = 500;  //maximum stack size
int statestk[] = new int[YYSTACKSIZE]; //state stack
int stateptr;
int stateptrmax;                     //highest index of stackptr
int statemax;                        //state when highest index reached
//###############################################################
// methods: state stack push,pop,drop,peek
//###############################################################
final void state_push(int state)
{
  try {
		stateptr++;
		statestk[stateptr]=state;
	 }
	 catch (ArrayIndexOutOfBoundsException e) {
     int oldsize = statestk.length;
     int newsize = oldsize * 2;
     int[] newstack = new int[newsize];
     System.arraycopy(statestk,0,newstack,0,oldsize);
     statestk = newstack;
     statestk[stateptr]=state;
  }
}
final int state_pop()
{
  return statestk[stateptr--];
}
final void state_drop(int cnt)
{
  stateptr -= cnt; 
}
final int state_peek(int relative)
{
  return statestk[stateptr-relative];
}
//###############################################################
// method: init_stacks : allocate and prepare stacks
//###############################################################
final boolean init_stacks()
{
  stateptr = -1;
  val_init();
  return true;
}
//###############################################################
// method: dump_stacks : show n levels of the stacks
//###############################################################
void dump_stacks(int count)
{
int i;
  System.out.println("=index==state====value=     s:"+stateptr+"  v:"+valptr);
  for (i=0;i<count;i++)
    System.out.println(" "+i+"    "+statestk[i]+"      "+valstk[i]);
  System.out.println("======================");
}


//########## SEMANTIC VALUES ##########
//public class ParserVal is defined in ParserVal.java


String   yytext;//user variable to return contextual strings
ParserVal yyval; //used to return semantic vals from action routines
ParserVal yylval;//the 'lval' (result) I got from yylex()
ParserVal valstk[];
int valptr;
//###############################################################
// methods: value stack push,pop,drop,peek.
//###############################################################
void val_init()
{
  valstk=new ParserVal[YYSTACKSIZE];
  yyval=new ParserVal();
  yylval=new ParserVal();
  valptr=-1;
}
void val_push(ParserVal val)
{
  if (valptr>=YYSTACKSIZE)
    return;
  valstk[++valptr]=val;
}
ParserVal val_pop()
{
  if (valptr<0)
    return new ParserVal();
  return valstk[valptr--];
}
void val_drop(int cnt)
{
int ptr;
  ptr=valptr-cnt;
  if (ptr<0)
    return;
  valptr = ptr;
}
ParserVal val_peek(int relative)
{
int ptr;
  ptr=valptr-relative;
  if (ptr<0)
    return new ParserVal();
  return valstk[ptr];
}
final ParserVal dup_yyval(ParserVal val)
{
  ParserVal dup = new ParserVal();
  dup.ival = val.ival;
  dup.dval = val.dval;
  dup.sval = val.sval;
  dup.obj = val.obj;
  return dup;
}
//#### end semantic value section ####
public final static short IF=257;
public final static short THEN=258;
public final static short ID=259;
public final static short ASSIGN=260;
public final static short ELSE=261;
public final static short BEGIN=262;
public final static short END=263;
public final static short END_IF=264;
public final static short PRINT=265;
public final static short WHILE=266;
public final static short DO=267;
public final static short FUN=268;
public final static short RETURN=269;
public final static short CTE=270;
public final static short CADENA=271;
public final static short UINTEGER=272;
public final static short LONGINT=273;
public final static short MAYOR_IGUAL=274;
public final static short MENOR_IGUAL=275;
public final static short DISTINTO=276;
public final static short YYERRCODE=256;
final static short yylhs[] = {                           -1,
    0,    1,    1,    3,    4,    6,    7,    5,    8,    2,
    2,    9,    9,   11,   11,   11,   11,   11,   11,   13,
   13,   12,   12,   12,   12,   12,   12,   18,   17,   19,
   19,   19,   19,   19,   19,   19,   19,   19,   19,   19,
   19,   21,   21,   21,   14,   14,   14,   15,   15,   15,
   16,   16,   16,   16,   10,   10,   10,   23,   22,   22,
   20,   20,   20,   25,   25,   25,   26,   26,   26,   26,
   26,   26,   24,   24,   27,   27,
};
final static short yylen[] = {                            2,
    2,    3,    2,    3,    3,    3,    1,    1,    3,    2,
    1,    1,    1,    2,    2,    2,    2,    2,    2,    4,
    3,    5,    7,    8,    8,    6,    6,    3,    3,    3,
    3,    3,    3,    3,    3,    2,    2,    2,    2,    2,
    2,    2,    1,    1,    4,    3,    3,    3,    2,    2,
    4,    3,    3,    3,    7,    6,    3,    1,    1,    1,
    1,    3,    3,    1,    3,    3,    1,    1,    2,    1,
    2,    1,    3,    1,    3,    4,
};
final static short yydefred[] = {                         0,
    0,    0,    0,    1,    0,    0,    0,    0,    8,    3,
    0,    0,    0,   59,   60,    0,    0,    0,   11,   12,
   13,    0,    0,    0,    0,    0,    0,   19,    0,   68,
   70,    0,    0,    0,    0,    0,    0,   64,   72,    0,
    0,    0,    0,    0,    0,    0,    2,   10,    0,    0,
   14,   15,   16,   17,   18,    0,    0,    0,    0,    0,
   69,   71,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,   52,    0,   54,
    0,    0,   47,    0,   21,    5,    0,   58,    0,   57,
   75,    0,    0,   28,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,   65,   66,   51,   20,
   45,    0,   73,    0,   76,    0,    0,   22,    9,    0,
    0,    0,   29,   43,    0,    0,    0,    0,    0,    0,
   26,    0,    7,   56,    0,    0,   42,    0,   23,    4,
    0,    0,   55,   24,   25,    6,
};
final static short yydgoto[] = {                          2,
    4,   16,  121,   17,   18,  134,  135,   65,   19,   20,
   21,   22,   23,   24,   25,   26,   85,   34,   35,   36,
  126,   27,   89,   58,   37,   38,   39,
};
final static short yysindex[] = {                      -242,
 -189,    0, -174,    0,   31,  -18, -202,   16,    0,    0,
  -29, -165,    1,    0,    0, -146, -154,  -54,    0,    0,
    0,   41,   71,   74,   79,   87, -181,    0,   96,    0,
    0,   16, -164, -166,  111,   -6,   29,    0,    0,   16,
   36,  116,  -25, -107,   16,   77,    0,    0,  122, -128,
    0,    0,    0,    0,    0,  123,  -89,  120,   -1,  -38,
    0,    0,  -82,  -54,  -76,  -70,   16,   16,   16,   16,
   16,   16,   16,   16,   16,   16,   36,    0,  156,    0,
  122,  132,    0,   16,    0,    0,  -61,    0,  161,    0,
    0,  150,  -82,    0, -204, -109,  -55,  -82,   36,   36,
   36,   36,   36,   36,   29,   29,    0,    0,    0,    0,
    0,  163,    0,    4,    0,  -52,  -55,    0,    0,  -54,
  -50,  -51,    0,    0,  -46,  171,  -35,  -55,  -43,  -91,
    0,  -55,    0,    0,  -54,  -46,    0,  -27,    0,    0,
  -17,  -73,    0,    0,    0,    0,
};
final static short yyrindex[] = {                         0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,  -41,    0,
    0,    0,    0,    0,    0,    0,  -36,    0,    0,  164,
  167,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,  169,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,  -33,  -28,  -26,   -8,
    6,   21,    0,    0,    0,    0,  170,    0,   74,    0,
    0,  172,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,   22,   23,
   24,   25,   26,   27,  -31,   -9,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,  195,    0,  189,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,
};
final static short yygindex[] = {                         0,
    0,   20,  -58,  239,    0,  117,    0,  -21,   19,    0,
    0,    0,    0,    0,    0,    0,  175,    0,   17,   35,
    0,  138,    0,  174,   51,   67,    0,
};
final static int YYTABLESIZE=289;
static short yytable[];
static { yytable();}
static void yytable(){
yytable = new short[]{                         67,
   67,   67,   94,   67,   61,   67,   61,   39,   61,   62,
   43,   62,   40,   62,   41,   80,    1,   67,   67,   67,
   67,   32,   61,   61,   61,   61,   33,   62,   62,   62,
   62,   63,   36,   63,   48,   63,   73,   50,   74,   91,
   45,   95,   41,   33,  125,   33,   37,   46,   60,   63,
   63,   63,   63,   71,   72,   70,  117,   40,  129,  118,
   33,   38,   33,   34,   35,   30,   31,   32,   48,  138,
   75,  116,    3,  141,   77,   76,  122,   56,   73,   82,
   74,    5,    6,   96,    7,    8,   57,    9,   10,   28,
   11,   63,   12,   92,   13,   64,    9,   14,   15,   51,
  112,   99,  100,  101,  102,  103,  104,   61,   62,    5,
    6,   49,    7,    8,   48,    9,   47,   83,   11,   73,
   12,   74,   13,  105,  106,   14,   15,    5,    6,   52,
    7,    8,   53,    9,   86,   59,   11,   54,   12,  130,
   13,  107,  108,   14,   15,   55,    5,    6,   48,    7,
    8,   66,    9,  119,  142,   11,   78,   12,   81,   13,
   48,   84,   14,   15,    5,    6,   87,    7,    8,   88,
    9,  140,  111,   11,   73,   12,   74,   13,   90,   64,
   14,   15,    5,    6,   97,    7,    8,   98,    9,  146,
  115,   11,   73,   12,   74,   13,  109,   56,   14,   15,
  114,    5,    6,  123,    7,    8,  120,    9,  128,  132,
   11,  136,   12,  131,   13,  133,   67,   14,   15,   93,
  139,   61,   49,  137,   39,   50,   62,   74,   48,   40,
   46,   41,   67,   67,   67,   44,  144,   61,   61,   61,
   29,   42,   62,   62,   62,   79,  145,   27,   63,   36,
   44,  127,  143,   30,   31,  110,    0,   29,    0,   29,
  113,    0,  124,   37,   63,   63,   63,   67,   68,   69,
   30,   31,   30,   31,   29,   14,   15,    0,   38,   33,
   34,   35,   30,   31,   32,    0,    0,   30,   31,
};
}
static short yycheck[];
static { yycheck(); }
static void yycheck() {
yycheck = new short[] {                         41,
   42,   43,   41,   45,   41,   47,   43,   41,   45,   41,
   40,   43,   41,   45,   41,   41,  259,   59,   60,   61,
   62,   40,   59,   60,   61,   62,   45,   59,   60,   61,
   62,   41,   41,   43,   16,   45,   43,   18,   45,   41,
   40,   63,    8,   45,   41,   45,   41,   13,   32,   59,
   60,   61,   62,   60,   61,   62,  261,  260,  117,  264,
   45,   41,   41,   41,   41,   41,   41,   41,   50,  128,
   42,   93,  262,  132,   40,   47,   98,  259,   43,   45,
   45,  256,  257,   64,  259,  260,  268,  262,  263,   59,
  265,  258,  267,   59,  269,  262,  262,  272,  273,   59,
   84,   67,   68,   69,   70,   71,   72,  272,  273,  256,
  257,  266,  259,  260,   96,  262,  263,   41,  265,   43,
  267,   45,  269,   73,   74,  272,  273,  256,  257,   59,
  259,  260,   59,  262,  263,   40,  265,   59,  267,  120,
  269,   75,   76,  272,  273,   59,  256,  257,  130,  259,
  260,   41,  262,  263,  135,  265,   41,  267,  266,  269,
  142,   40,  272,  273,  256,  257,   44,  259,  260,  259,
  262,  263,   41,  265,   43,  267,   45,  269,   59,  262,
  272,  273,  256,  257,  261,  259,  260,  258,  262,  263,
   41,  265,   43,  267,   45,  269,   41,  259,  272,  273,
   40,  256,  257,   41,  259,  260,  262,  262,  261,  261,
  265,   41,  267,  264,  269,  262,  258,  272,  273,  258,
  264,  258,   59,  259,  258,   59,  258,   59,   59,  258,
   59,  258,  274,  275,  276,   41,  264,  274,  275,  276,
  259,  271,  274,  275,  276,  271,  264,   59,  258,  258,
   12,  114,  136,  272,  273,   81,   -1,  259,   -1,  259,
   87,   -1,  259,  258,  274,  275,  276,  274,  275,  276,
  272,  273,  272,  273,  259,  272,  273,   -1,  258,  258,
  258,  258,  258,  258,  258,   -1,   -1,  272,  273,
};
}
final static short YYFINAL=2;
final static short YYMAXTOKEN=276;
final static String yyname[] = {
"end-of-file",null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,"'('","')'","'*'","'+'","','",
"'-'",null,"'/'",null,null,null,null,null,null,null,null,null,null,null,"';'",
"'<'","'='","'>'",null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,"IF","THEN","ID","ASSIGN","ELSE","BEGIN","END",
"END_IF","PRINT","WHILE","DO","FUN","RETURN","CTE","CADENA","UINTEGER",
"LONGINT","MAYOR_IGUAL","MENOR_IGUAL","DISTINTO",
};
final static String yyrule[] = {
"$accept : programa",
"programa : ID bloque",
"bloque : BEGIN ss END",
"bloque : BEGIN END",
"bloqueejecutable : BEGIN ss END",
"bloquewhile : beginwhile ss END",
"bloquefunct : beginfunct ss END",
"beginfunct : BEGIN",
"beginwhile : BEGIN",
"bloquethen : BEGIN ss END",
"ss : ss s",
"ss : s",
"s : declaracion",
"s : se",
"se : seleccion ';'",
"se : iteracion ';'",
"se : retorno ';'",
"se : asignacion ';'",
"se : print ';'",
"se : error ';'",
"iteracion : DO bloquewhile WHILE condicionwhile",
"iteracion : bloquewhile WHILE condicionwhile",
"seleccion : IF condicionif THEN bloquethen END_IF",
"seleccion : IF condicionif THEN bloquethen ELSE bloqueejecutable END_IF",
"seleccion : IF '(' condicion THEN bloquethen ELSE bloqueejecutable END_IF",
"seleccion : IF condicion ')' THEN bloquethen ELSE bloqueejecutable END_IF",
"seleccion : IF condicionif bloquethen ELSE bloqueejecutable END_IF",
"seleccion : IF condicionif THEN bloquethen ELSE bloqueejecutable",
"condicionif : '(' condicion ')'",
"condicionwhile : '(' condicion ')'",
"condicion : expresion '>' expresion",
"condicion : expresion '<' expresion",
"condicion : expresion '=' expresion",
"condicion : expresion MAYOR_IGUAL expresion",
"condicion : expresion MENOR_IGUAL expresion",
"condicion : expresion DISTINTO expresion",
"condicion : expresion '>'",
"condicion : expresion '<'",
"condicion : expresion '='",
"condicion : expresion MAYOR_IGUAL",
"condicion : expresion MENOR_IGUAL",
"condicion : expresion DISTINTO",
"parametro : tipodato ID",
"parametro : ID",
"parametro : tipodato",
"retorno : RETURN '(' expresion ')'",
"retorno : RETURN '(' expresion",
"retorno : RETURN expresion ')'",
"asignacion : ID ASSIGN expresion",
"asignacion : ID ASSIGN",
"asignacion : ASSIGN expresion",
"print : PRINT '(' CADENA ')'",
"print : PRINT CADENA ')'",
"print : PRINT '(' CADENA",
"print : PRINT '(' ')'",
"declaracion : tipodato FUN identificadorfunct '(' parametro ')' bloquefunct",
"declaracion : tipodato FUN identificadorfunct '(' ')' bloquefunct",
"declaracion : tipodato listavariables ';'",
"identificadorfunct : ID",
"tipodato : UINTEGER",
"tipodato : LONGINT",
"expresion : termino",
"expresion : expresion '+' termino",
"expresion : expresion '-' termino",
"termino : factor",
"termino : termino '*' factor",
"termino : termino '/' factor",
"factor : ID",
"factor : UINTEGER",
"factor : '-' UINTEGER",
"factor : LONGINT",
"factor : '-' LONGINT",
"factor : invocacion",
"listavariables : ID ',' listavariables",
"listavariables : ID",
"invocacion : ID '(' ')'",
"invocacion : ID '(' expresion ')'",
};

//#line 282 ".\src\main\resources\grammar\gramatica.y"

static Lex lex=null;
static Parser par=null;
int index=0;
static ArrayList<Terceto> reglas=new ArrayList<Terceto>();
static ArrayList<String> variables=new ArrayList<String>();
static Stack<Integer> pila = new Stack<>();
static Stack<String> pilaString=new Stack<>();
static ArrayList<String> colaAmbito=new ArrayList<String>();
static HashMap<String,String> tiposParFunct=new HashMap<>();
static HashMap<String,String> tiposRetFunct=new HashMap<>();

ar.tp.parser.ParserVal PV;


int pointer;
Terceto t;

public int runParser() {
    return yyparse();
}

public static void setLexer(Lex l) {
     lex = l; 
}

int yylex() {
  int token;
  try {
    token = lex.getToken();
    yylval = new ar.tp.parser.ParserVal(lex.yylval);
    System.out.println("TOK " + token + "  lexeme='" + lex.yylval + "'  line=" + lex.line);
  } catch (IOException e) {
    token = -1;
  }
  return token;
}

void yyerror(String s){
    System.out.println(s+" on line "+lex.line);
}

String crear_terceto(String operando,ar.tp.parser.ParserVal a,ar.tp.parser.ParserVal b){
    Terceto t=new Terceto(operando, a, b);
    reglas.add(t);
    return "["+Integer.toString(reglas.indexOf(t))+"]";
}

String crear_terceto(String operando,ar.tp.parser.ParserVal a,ar.tp.parser.ParserVal b,ArrayList<String> errores){
    Terceto t=new Terceto(operando, a, b,errores);
    reglas.add(t);
    return "["+Integer.toString(reglas.indexOf(t))+"]";
}

static int mostrarPila(Stack<Integer> pila){
    if (pila.empty())
        System.out.println("Pila Vacía");
    else
        for (int i=0;i<pila.size();i++)
            System.out.println(pila.get(i));
    return 0;
}

static int mostrarReglas(ArrayList<Terceto> reglas){
    if (reglas.size()==0)
        System.out.println("No hay reglas");
    else
        for (int i=0;i<reglas.size();i++)
            System.out.println("["+i+"] "+reglas.get(i));
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
    do{
        variable=lex.symbols.get(getVariableName(nombre,N++));
    }while(variable==null&&N<colaAmbito.size());
    return variable;
}

/* static String getVariableName(String nombre,int n){
    String sufijo="";
    for (int i=0;i<colaAmbito.size()-n;i++){
        sufijo=sufijo+colaAmbito.get(i);
    }
    nombre=sufijo+nombre;
    System.out.print(nombre);
    return nombre;
} */

// Devuelve "uinteger", "longint" o null si no puede determinarlo
String tipoDe(ar.tp.parser.ParserVal v) {
    if (v == null || v.sval == null) return null;

    String s = v.sval;

    // ¿Es un terceto? "[n]"
    if (s.length() >= 3 && s.charAt(0) == '[' && s.charAt(s.length()-1) == ']') {
        // Si guardás tipo en el terceto, úsalo:
        try {
            int idx = decode(s);              // tu helper para convertir "[n]" -> n
            Terceto tt = reglas.get(idx);
            return tt.tipo;                   // si aún no llevás tipos, retorna null
        } catch (Exception e) { return null; }
    }

    // ¿Es constante de la TS?
    Symbol sc = lex.symbols.get(s);
    if (sc != null) return sc.tipo;

    // ¿Es variable (con manejo de ámbitos)?
    Symbol sv = buscarVariable(s);
    if (sv != null) return sv.tipo;

    return null;
}


static String getVariableName(String nombre,int n){
    String sufijo="";
    for (int i=0;i<colaAmbito.size()-n;i++){
        sufijo=sufijo+colaAmbito.get(i);
    }
    return sufijo+nombre;
}

void guardarVariable(String nombre,Symbol s){
    lex.symbols.put(getVariableName(nombre,0),s);
}

static int decode(String str){
    str = str.substring(1, str.length() - 1);
    return Integer.valueOf(str);
}
//#line 516 "Parser.java"
//###############################################################
// method: yylexdebug : check lexer state
//###############################################################
void yylexdebug(int state,int ch)
{
String s=null;
  if (ch < 0) ch=0;
  if (ch <= YYMAXTOKEN) //check index bounds
     s = yyname[ch];    //now get it
  if (s==null)
    s = "illegal-symbol";
  debug("state "+state+", reading "+ch+" ("+s+")");
}





//The following are now global, to aid in error reporting
int yyn;       //next next thing to do
int yym;       //
int yystate;   //current parsing state from state table
String yys;    //current token string


//###############################################################
// method: yyparse : parse input and execute indicated items
//###############################################################
int yyparse()
{
boolean doaction;
  init_stacks();
  yynerrs = 0;
  yyerrflag = 0;
  yychar = -1;          //impossible char forces a read
  yystate=0;            //initial state
  state_push(yystate);  //save it
  val_push(yylval);     //save empty value
  while (true) //until parsing is done, either correctly, or w/error
    {
    doaction=true;
    if (yydebug) debug("loop"); 
    //#### NEXT ACTION (from reduction table)
    for (yyn=yydefred[yystate];yyn==0;yyn=yydefred[yystate])
      {
      if (yydebug) debug("yyn:"+yyn+"  state:"+yystate+"  yychar:"+yychar);
      if (yychar < 0)      //we want a char?
        {
        yychar = yylex();  //get next token
        if (yydebug) debug(" next yychar:"+yychar);
        //#### ERROR CHECK ####
        if (yychar < 0)    //it it didn't work/error
          {
          yychar = 0;      //change it to default string (no -1!)
          if (yydebug)
            yylexdebug(yystate,yychar);
          }
        }//yychar<0
      yyn = yysindex[yystate];  //get amount to shift by (shift index)
      if ((yyn != 0) && (yyn += yychar) >= 0 &&
          yyn <= YYTABLESIZE && yycheck[yyn] == yychar)
        {
        if (yydebug)
          debug("state "+yystate+", shifting to state "+yytable[yyn]);
        //#### NEXT STATE ####
        yystate = yytable[yyn];//we are in a new state
        state_push(yystate);   //save it
        val_push(yylval);      //push our lval as the input for next rule
        yychar = -1;           //since we have 'eaten' a token, say we need another
        if (yyerrflag > 0)     //have we recovered an error?
           --yyerrflag;        //give ourselves credit
        doaction=false;        //but don't process yet
        break;   //quit the yyn=0 loop
        }

    yyn = yyrindex[yystate];  //reduce
    if ((yyn !=0 ) && (yyn += yychar) >= 0 &&
            yyn <= YYTABLESIZE && yycheck[yyn] == yychar)
      {   //we reduced!
      if (yydebug) debug("reduce");
      yyn = yytable[yyn];
      doaction=true; //get ready to execute
      break;         //drop down to actions
      }
    else //ERROR RECOVERY
      {
      if (yyerrflag==0)
        {
        yyerror("syntax error");
        yynerrs++;
        }
      if (yyerrflag < 3) //low error count?
        {
        yyerrflag = 3;
        while (true)   //do until break
          {
          if (stateptr<0)   //check for under & overflow here
            {
            yyerror("stack underflow. aborting...");  //note lower case 's'
            return 1;
            }
          yyn = yysindex[state_peek(0)];
          if ((yyn != 0) && (yyn += YYERRCODE) >= 0 &&
                    yyn <= YYTABLESIZE && yycheck[yyn] == YYERRCODE)
            {
            if (yydebug)
              debug("state "+state_peek(0)+", error recovery shifting to state "+yytable[yyn]+" ");
            yystate = yytable[yyn];
            state_push(yystate);
            val_push(yylval);
            doaction=false;
            break;
            }
          else
            {
            if (yydebug)
              debug("error recovery discarding state "+state_peek(0)+" ");
            if (stateptr<0)   //check for under & overflow here
              {
              yyerror("Stack underflow. aborting...");  //capital 'S'
              return 1;
              }
            state_pop();
            val_pop();
            }
          }
        }
      else            //discard this token
        {
        if (yychar == 0)
          return 1; //yyabort
        if (yydebug)
          {
          yys = null;
          if (yychar <= YYMAXTOKEN) yys = yyname[yychar];
          if (yys == null) yys = "illegal-symbol";
          debug("state "+yystate+", error recovery discards token "+yychar+" ("+yys+")");
          }
        yychar = -1;  //read another
        }
      }//end error recovery
    }//yyn=0 loop
    if (!doaction)   //any reason not to proceed?
      continue;      //skip action
    yym = yylen[yyn];          //get count of terminals on rhs
    if (yydebug)
      debug("state "+yystate+", reducing "+yym+" by rule "+yyn+" ("+yyrule[yyn]+")");
    if (yym>0)                 //if count of rhs not 'nil'
      yyval = val_peek(yym-1); //get current semantic value
    yyval = dup_yyval(yyval); //duplicate yyval if ParserVal is used as semantic value
    switch(yyn)
      {
//########## USER-SUPPLIED ACTIONS ##########
case 7:
//#line 34 ".\src\main\resources\grammar\gramatica.y"
{

    yyval = new ar.tp.parser.ParserVal(
        crear_terceto(
            "begfunct",
            new ar.tp.parser.ParserVal("-"),
            new ar.tp.parser.ParserVal("-")
        )
    );

    pila.push(reglas.size());
    }
break;
case 8:
//#line 48 ".\src\main\resources\grammar\gramatica.y"
{pila.push(reglas.size());}
break;
case 9:
//#line 51 ".\src\main\resources\grammar\gramatica.y"
{
    /* Cerramos el BF de la condición apuntando al “después del THEN” (salto a ELSE)*/
    int posBF = pila.pop();
    Terceto tBF = reglas.get(posBF);
    /* Creamos BI para saltar al final del IF (después del ELSE)*/
    ParserVal pvBI = new ParserVal( crear_terceto("BI", new ParserVal("-"), new ParserVal("-")) );

    tBF.b = new ParserVal(reglas.size());  /* destino del BF: inicio del bloque ELSE*/
    reglas.set(posBF, tBF);

    pila.push(reglas.size()-1);  /* guardo pos(BI) para backpatch cuando termine ELSE*/
    yyval = pvBI;
}
break;
case 14:
//#line 74 ".\src\main\resources\grammar\gramatica.y"
{
         int posBI = pila.pop();
         Terceto tBI = reglas.get(posBI);
         tBI.b = new ParserVal(reglas.size());  /* fin de todo el IF*/
         reglas.set(posBI, tBI);
     }
break;
case 19:
//#line 84 ".\src\main\resources\grammar\gramatica.y"
{System.out.println("ERROR on line "+lex.line+" sentencia invalida");}
break;
case 20:
//#line 87 ".\src\main\resources\grammar\gramatica.y"
{
     int posInicioDo = pila.pop();

     int posBF = reglas.size();
     crear_terceto("BF", val_peek(0), new ParserVal("-"));

     crear_terceto("BI", new ParserVal("[" + posInicioDo + "]"), new ParserVal("-"));

     Terceto tBF = reglas.get(posBF);
     tBF.b = new ParserVal("[" + reglas.size() + "]");
     reglas.set(posBF, tBF);
   }
break;
case 21:
//#line 99 ".\src\main\resources\grammar\gramatica.y"
{System.out.println("ERROR on line "+lex.line+" 'do' expected");}
break;
case 24:
//#line 104 ".\src\main\resources\grammar\gramatica.y"
{System.out.println("ERROR on line "+lex.line+": ')' expected");}
break;
case 25:
//#line 105 ".\src\main\resources\grammar\gramatica.y"
{System.out.println("ERROR on line "+lex.line+": '(' expected");}
break;
case 26:
//#line 106 ".\src\main\resources\grammar\gramatica.y"
{System.out.println("ERROR on line "+lex.line+": 'then' expected");}
break;
case 27:
//#line 107 ".\src\main\resources\grammar\gramatica.y"
{System.out.println("ERROR on line "+lex.line+": 'end_if' expected");}
break;
case 28:
//#line 110 ".\src\main\resources\grammar\gramatica.y"
{
    ParserVal pv = new ParserVal(
        crear_terceto("BF",
            new ParserVal("[" + (reglas.size()-1) + "]"),
            new ParserVal("-"))
    );
    pila.push(reglas.size()-1);   /* guardo pos(BF) para backpatch en THEN*/
    yyval = pv;
}
break;
case 29:
//#line 121 ".\src\main\resources\grammar\gramatica.y"
{ yyval = val_peek(1); }
break;
case 30:
//#line 124 ".\src\main\resources\grammar\gramatica.y"
{yyval=new ar.tp.parser.ParserVal(crear_terceto(">",val_peek(2),val_peek(0)));}
break;
case 31:
//#line 125 ".\src\main\resources\grammar\gramatica.y"
{yyval=new ar.tp.parser.ParserVal(crear_terceto("<",val_peek(2),val_peek(0)));}
break;
case 32:
//#line 126 ".\src\main\resources\grammar\gramatica.y"
{yyval=new ar.tp.parser.ParserVal(crear_terceto("=",val_peek(2),val_peek(0)));}
break;
case 33:
//#line 127 ".\src\main\resources\grammar\gramatica.y"
{yyval=new ar.tp.parser.ParserVal(crear_terceto(">=",val_peek(2),val_peek(0)));}
break;
case 34:
//#line 128 ".\src\main\resources\grammar\gramatica.y"
{yyval=new ar.tp.parser.ParserVal(crear_terceto("<=",val_peek(2),val_peek(0)));}
break;
case 35:
//#line 129 ".\src\main\resources\grammar\gramatica.y"
{yyval=new ar.tp.parser.ParserVal(crear_terceto("<>",val_peek(2),val_peek(0)));}
break;
case 36:
//#line 130 ".\src\main\resources\grammar\gramatica.y"
{System.out.println("ERROR on line "+lex.line+": second expresion expected");}
break;
case 37:
//#line 131 ".\src\main\resources\grammar\gramatica.y"
{System.out.println("ERROR on line "+lex.line+": second expresion expected");}
break;
case 38:
//#line 132 ".\src\main\resources\grammar\gramatica.y"
{System.out.println("ERROR on line "+lex.line+": second expresion expected");}
break;
case 39:
//#line 133 ".\src\main\resources\grammar\gramatica.y"
{System.out.println("ERROR on line "+lex.line+": second expresion expected");}
break;
case 40:
//#line 134 ".\src\main\resources\grammar\gramatica.y"
{System.out.println("ERROR on line "+lex.line+": second expresion expected");}
break;
case 41:
//#line 135 ".\src\main\resources\grammar\gramatica.y"
{System.out.println("ERROR on line "+lex.line+": second expresion expected");}
break;
case 42:
//#line 138 ".\src\main\resources\grammar\gramatica.y"
{guardarVariable(val_peek(0).sval,new Symbol(val_peek(1).sval,"parametro"));yyval=val_peek(0);pilaString.push(val_peek(1).sval);}
break;
case 43:
//#line 139 ".\src\main\resources\grammar\gramatica.y"
{System.out.println("ERROR on line "+lex.line+": datatype expected");}
break;
case 44:
//#line 140 ".\src\main\resources\grammar\gramatica.y"
{System.out.println("ERROR on line "+lex.line+": identifier expected");}
break;
case 45:
//#line 143 ".\src\main\resources\grammar\gramatica.y"
{yyval=new ar.tp.parser.ParserVal(crear_terceto("ret",val_peek(1),new ar.tp.parser.ParserVal("-")));}
break;
case 46:
//#line 144 ".\src\main\resources\grammar\gramatica.y"
{System.out.println("ERROR on line "+lex.line+": ')' expected");}
break;
case 47:
//#line 145 ".\src\main\resources\grammar\gramatica.y"
{System.out.println("ERROR on line "+lex.line+": '(' expected");}
break;
case 48:
//#line 148 ".\src\main\resources\grammar\gramatica.y"
{yyval=new ar.tp.parser.ParserVal(crear_terceto(":=",val_peek(2),val_peek(0)));}
break;
case 49:
//#line 149 ".\src\main\resources\grammar\gramatica.y"
{System.out.println("ERROR on line "+lex.line+": expresion expected");}
break;
case 50:
//#line 150 ".\src\main\resources\grammar\gramatica.y"
{System.out.println("ERROR on line "+lex.line+": identifier expected");}
break;
case 52:
//#line 154 ".\src\main\resources\grammar\gramatica.y"
{System.out.println("ERROR on line "+lex.line+": '(' expected");}
break;
case 53:
//#line 155 ".\src\main\resources\grammar\gramatica.y"
{System.out.println("ERROR on line "+lex.line+": ')' expected");}
break;
case 54:
//#line 156 ".\src\main\resources\grammar\gramatica.y"
{System.out.println("ERROR on line "+lex.line+": String expected");}
break;
case 55:
//#line 159 ".\src\main\resources\grammar\gramatica.y"
{
        ArrayList<String> errores=new ArrayList<String>();
        if(buscarVariable(val_peek(4).sval)!=null)
            errores.add("declared");
        tiposParFunct.put(val_peek(4).sval,pilaString.pop());
        yyval=new ar.tp.parser.ParserVal(crear_terceto("endfun",val_peek(4),new ar.tp.parser.ParserVal("-"),errores));
        t=reglas.get(pila.peek()-1);
        t.a=val_peek(4);
        t.b=val_peek(2);
        reglas.set(pila.pop()-1,t);
        colaAmbito.remove(colaAmbito.size()-1);
        if (!errores.contains("declared")){
            guardarVariable(val_peek(4).sval,new Symbol(val_peek(6).sval,"Fun"));
        };
    }
break;
case 56:
//#line 174 ".\src\main\resources\grammar\gramatica.y"
{
          ArrayList<String> errores=new ArrayList<String>();
          if(buscarVariable(val_peek(3).sval)!=null)
              errores.add("declared");

          /* Registrar que la función NO tiene parámetros (cadena vacía = 0 params)*/
          tiposParFunct.put(val_peek(3).sval, "");

          yyval=new ar.tp.parser.ParserVal(crear_terceto("endfun",val_peek(3),new ar.tp.parser.ParserVal("-"),errores));

          /* Vincular el begfunct con el nombre de la función*/
          t = reglas.get(pila.peek()-1);
          t.a = new ar.tp.parser.ParserVal(val_peek(3).sval);
          reglas.set(pila.pop()-1, t);

          colaAmbito.remove(colaAmbito.size()-1);

          if (!errores.contains("declared")){
              guardarVariable(val_peek(3).sval,new Symbol(val_peek(5).sval,"Fun"));
          };
      }
break;
case 57:
//#line 195 ".\src\main\resources\grammar\gramatica.y"
{
      for (String nombre : variables) {
          ArrayList<String> errores = new ArrayList<>();
          if (buscarVariable(nombre) != null) errores.add("declared");
          crear_terceto("decl", val_peek(2), new ParserVal(nombre), errores);
          guardarVariable(nombre, new Symbol(val_peek(2).sval, "Var"));
      }
      variables.clear();
  }
break;
case 58:
//#line 206 ".\src\main\resources\grammar\gramatica.y"
{colaAmbito.add(val_peek(0).sval+":");}
break;
case 59:
//#line 210 ".\src\main\resources\grammar\gramatica.y"
{yyval=val_peek(0);}
break;
case 60:
//#line 211 ".\src\main\resources\grammar\gramatica.y"
{yyval=val_peek(0);}
break;
case 62:
//#line 215 ".\src\main\resources\grammar\gramatica.y"
{
        ArrayList<String> errores = new ArrayList<>();
        String t1 = tipoDe(val_peek(2));
        String t3 = tipoDe(val_peek(0));
        if (t1 != null && t3 != null && !t1.equals(t3))
            errores.add("datatype missmatch");
        yyval = new ar.tp.parser.ParserVal(crear_terceto("+", val_peek(2), val_peek(0), errores));
      }
break;
case 63:
//#line 223 ".\src\main\resources\grammar\gramatica.y"
{
        ArrayList<String> errores = new ArrayList<>();
        String t1 = tipoDe(val_peek(2));
        String t3 = tipoDe(val_peek(0));
        if (t1 != null && t3 != null && !t1.equals(t3))
            errores.add("datatype missmatch");
        yyval = new ar.tp.parser.ParserVal(crear_terceto("-", val_peek(2), val_peek(0), errores));
      }
break;
case 65:
//#line 234 ".\src\main\resources\grammar\gramatica.y"
{
        ArrayList<String> errores = new ArrayList<>();
        String t1 = tipoDe(val_peek(2));
        String t3 = tipoDe(val_peek(0));
        if (t1 != null && t3 != null && !t1.equals(t3))
            errores.add("datatype missmatch");
        yyval = new ar.tp.parser.ParserVal(crear_terceto("*", val_peek(2), val_peek(0), errores));
      }
break;
case 66:
//#line 242 ".\src\main\resources\grammar\gramatica.y"
{
        ArrayList<String> errores = new ArrayList<>();
        String t1 = tipoDe(val_peek(2));
        String t3 = tipoDe(val_peek(0));
        if (t1 != null && t3 != null && !t1.equals(t3))
            errores.add("datatype missmatch");
        yyval = new ar.tp.parser.ParserVal(crear_terceto("/", val_peek(2), val_peek(0), errores));
      }
break;
case 73:
//#line 260 ".\src\main\resources\grammar\gramatica.y"
{variables.add(val_peek(2).sval);}
break;
case 74:
//#line 261 ".\src\main\resources\grammar\gramatica.y"
{variables.add(val_peek(0).sval);}
break;
case 75:
//#line 265 ".\src\main\resources\grammar\gramatica.y"
{
     yyval = new ParserVal( crear_terceto("exec", val_peek(2), new ParserVal("-")) );
     /* opcional: tipos/chequeos de cantidad de params*/
 }
break;
case 76:
//#line 269 ".\src\main\resources\grammar\gramatica.y"
{
     ArrayList<String> errores = new ArrayList<>();
     String tArg = tipoDe(val_peek(1));                  /* seguro*/
     String tFormal = tiposParFunct.get(val_peek(3).sval);
     if (tFormal == null) errores.add("func-undeclared");
     else if (tArg != null && !tArg.equals(tFormal)) errores.add("type missmatch");

     yyval = new ParserVal( crear_terceto("exec", val_peek(3), val_peek(1), errores) );
 }
break;
//#line 997 "Parser.java"
//########## END OF USER-SUPPLIED ACTIONS ##########
    }//switch
    //#### Now let's reduce... ####
    if (yydebug) debug("reduce");
    state_drop(yym);             //we just reduced yylen states
    yystate = state_peek(0);     //get new state
    val_drop(yym);               //corresponding value drop
    yym = yylhs[yyn];            //select next TERMINAL(on lhs)
    if (yystate == 0 && yym == 0)//done? 'rest' state and at first TERMINAL
      {
      if (yydebug) debug("After reduction, shifting from state 0 to state "+YYFINAL+"");
      yystate = YYFINAL;         //explicitly say we're done
      state_push(YYFINAL);       //and save it
      val_push(yyval);           //also save the semantic value of parsing
      if (yychar < 0)            //we want another character?
        {
        yychar = yylex();        //get next character
        if (yychar<0) yychar=0;  //clean, if necessary
        if (yydebug)
          yylexdebug(yystate,yychar);
        }
      if (yychar == 0)          //Good exit (if lex returns 0 ;-)
         break;                 //quit the loop--all DONE
      }//if yystate
    else                        //else not done yet
      {                         //get next state and push, for next yydefred[]
      yyn = yygindex[yym];      //find out where to go
      if ((yyn != 0) && (yyn += yystate) >= 0 &&
            yyn <= YYTABLESIZE && yycheck[yyn] == yystate)
        yystate = yytable[yyn]; //get new state
      else
        yystate = yydgoto[yym]; //else go to new defred
      if (yydebug) debug("after reduction, shifting from state "+state_peek(0)+" to state "+yystate+"");
      state_push(yystate);     //going again, so push state & val...
      val_push(yyval);         //for next action
      }
    }//main loop
  return 0;//yyaccept!!
}
//## end of method parse() ######################################



//## run() --- for Thread #######################################
/**
 * A default run method, used for operating this parser
 * object in the background.  It is intended for extending Thread
 * or implementing Runnable.  Turn off with -Jnorun .
 */
public void run()
{
  yyparse();
}
//## end of method run() ########################################



//## Constructors ###############################################
/**
 * Default constructor.  Turn off with -Jnoconstruct .

 */
public Parser()
{
  //nothing to do
}


/**
 * Create a parser, setting the debug to true or false.
 * @param debugMe true for debugging, false for no debug.
 */
public Parser(boolean debugMe)
{
  yydebug=debugMe;
}
//###############################################################



}
//################### END OF CLASS ##############################
