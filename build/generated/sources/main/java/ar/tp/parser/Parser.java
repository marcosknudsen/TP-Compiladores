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



//#line 2 "gramatica.y"
    import java.io.FileNotFoundException;
    import java.io.IOException;
    import java.util.ArrayList;
    import java.util.Stack;
    import java.util.HashMap;
    import ar.tp.lexer.Lex;
    import ar.tp.ast.*;
    import ar.tp.acciones.*;
//#line 26 "Parser.java"




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
    0,    1,    3,    4,    6,    7,    5,    8,    2,    2,
    9,    9,   11,   11,   11,   11,   11,   11,   13,   13,
   12,   12,   12,   12,   12,   12,   18,   17,   19,   19,
   19,   19,   19,   19,   19,   19,   19,   19,   19,   19,
   21,   21,   21,   14,   14,   14,   15,   15,   15,   16,
   16,   16,   16,   10,   10,   10,   23,   22,   22,   20,
   20,   20,   25,   25,   25,   26,   26,   26,   26,   26,
   26,   24,   24,   27,   27,
};
final static short yylen[] = {                            2,
    2,    3,    3,    3,    3,    1,    1,    3,    2,    1,
    1,    1,    2,    2,    2,    2,    2,    2,    4,    3,
    5,    7,    8,    8,    6,    6,    3,    3,    3,    3,
    3,    3,    3,    3,    2,    2,    2,    2,    2,    2,
    2,    1,    1,    4,    3,    3,    3,    2,    2,    4,
    3,    3,    3,    7,    6,    3,    1,    1,    1,    1,
    3,    3,    1,    3,    3,    1,    1,    2,    1,    2,
    1,    3,    1,    3,    4,
};
final static short yydefred[] = {                         0,
    0,    0,    0,    1,    0,    0,    0,    0,    7,    0,
    0,    0,   58,   59,    0,    0,    0,   10,   11,   12,
    0,    0,    0,    0,    0,    0,   18,    0,   67,   69,
    0,    0,    0,    0,    0,    0,   63,   71,    0,    0,
    0,    0,    0,    0,    0,    2,    9,    0,    0,   13,
   14,   15,   16,   17,    0,    0,    0,    0,    0,   68,
   70,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,   51,    0,   53,    0,
    0,   46,    0,   20,    4,    0,   57,    0,   56,   74,
    0,    0,   27,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,   64,   65,   50,   19,   44,
    0,   72,    0,   75,    0,    0,   21,    8,    0,    0,
    0,   28,   42,    0,    0,    0,    0,    0,    0,   25,
    0,    6,   55,    0,    0,   41,    0,   22,    3,    0,
    0,   54,   23,   24,    5,
};
final static short yydgoto[] = {                          2,
    4,   15,  120,   16,   17,  133,  134,   64,   18,   19,
   20,   21,   22,   23,   24,   25,   84,   33,   34,   35,
  125,   26,   88,   57,   36,   37,   38,
};
final static short yysindex[] = {                      -242,
 -219,    0,  -67,    0,   30,  -18, -201,   16,    0,  -29,
 -167,    1,    0,    0, -181, -168,  -67,    0,    0,    0,
   64,   66,   68,   75,   78, -210,    0,  100,    0,    0,
   16, -162, -175,  102,   -6,   38,    0,    0,   16,   28,
  104,  -25, -119,   16,   73,    0,    0,  115, -160,    0,
    0,    0,    0,    0,  114,  -96,  117,   -1,  -38,    0,
    0,  -89,  -67,  -82,  -77,   16,   16,   16,   16,   16,
   16,   16,   16,   16,   16,   28,    0,  142,    0,  115,
   76,    0,   16,    0,    0,  -68,    0,  154,    0,    0,
   81,  -89,    0, -204, -121,  -66,  -89,   28,   28,   28,
   28,   28,   28,   38,   38,    0,    0,    0,    0,    0,
  156,    0,    4,    0,  -62,  -66,    0,    0,  -67,  -63,
  -58,    0,    0,  -55,  163,  -51,  -66,  -54, -103,    0,
  -66,    0,    0,  -67,  -55,    0,  -53,    0,    0,  -52,
  -85,    0,    0,    0,    0,
};
final static short yyrindex[] = {                         0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,  -41,    0,    0,
    0,    0,    0,    0,    0,  -36,    0,    0,  150,  155,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,  157,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,  -33,  -28,  -26,   -8,    6,
   21,    0,    0,    0,    0,  159,    0,   68,    0,    0,
  160,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,   22,   23,   24,
   25,   26,   27,  -31,   -9,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,  172,    0,  162,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,
};
final static short yygindex[] = {                         0,
    0,   31,  -23,  204,    0,   88,    0,  -20,   20,    0,
    0,    0,    0,    0,    0,    0,  144,    0,    7,   62,
    0,  113,    0,  143,   95,  111,    0,
};
final static int YYTABLESIZE=289;
static short yytable[];
static { yytable();}
static void yytable(){
yytable = new short[]{                         66,
   66,   66,   93,   66,   60,   66,   60,   38,   60,   61,
   42,   61,   39,   61,   40,   79,    1,   66,   66,   66,
   66,   31,   60,   60,   60,   60,   32,   61,   61,   61,
   61,   62,   35,   62,   47,   62,   72,   59,   73,   90,
   44,   94,    3,   32,  124,   32,   36,   49,   55,   62,
   62,   62,   62,   70,   71,   69,  116,   56,   39,  117,
   32,   37,   32,   33,   34,   29,   30,   31,   47,   40,
   72,  115,   73,   45,    5,    6,  121,    7,    8,   74,
    9,   46,   62,   10,   75,   11,   63,   12,   27,  111,
   13,   14,  128,   95,    9,    5,    6,   48,    7,    8,
   76,    9,   85,  137,   10,   81,   11,  140,   12,   60,
   61,   13,   14,   82,   47,   72,  110,   73,   72,   91,
   73,  114,   50,   72,   51,   73,   52,   98,   99,  100,
  101,  102,  103,   53,    5,    6,   54,    7,    8,   58,
    9,  118,   65,   10,   77,   11,   80,   12,   47,  129,
   13,   14,    5,    6,   83,    7,    8,   86,    9,  139,
   47,   10,   87,   11,  141,   12,  104,  105,   13,   14,
    5,    6,   63,    7,    8,   89,    9,  145,   96,   10,
   97,   11,  108,   12,  106,  107,   13,   14,    5,    6,
   55,    7,    8,  113,    9,  119,  122,   10,  127,   11,
  130,   12,  131,  135,   13,   14,  132,  136,   48,  138,
  143,  144,   43,   49,   43,   73,   66,   47,   45,   92,
   26,   60,  142,  109,   38,  126,   61,    0,  112,   39,
    0,   40,   66,   66,   66,    0,    0,   60,   60,   60,
   28,   41,   61,   61,   61,   78,    0,    0,   62,   35,
    0,    0,    0,   29,   30,    0,    0,   28,    0,   28,
    0,    0,  123,   36,   62,   62,   62,   66,   67,   68,
   29,   30,   29,   30,   28,   13,   14,    0,   37,   32,
   33,   34,   29,   30,   31,    0,    0,   29,   30,
};
}
static short yycheck[];
static { yycheck(); }
static void yycheck() {
yycheck = new short[] {                         41,
   42,   43,   41,   45,   41,   47,   43,   41,   45,   41,
   40,   43,   41,   45,   41,   41,  259,   59,   60,   61,
   62,   40,   59,   60,   61,   62,   45,   59,   60,   61,
   62,   41,   41,   43,   15,   45,   43,   31,   45,   41,
   40,   62,  262,   45,   41,   45,   41,   17,  259,   59,
   60,   61,   62,   60,   61,   62,  261,  268,  260,  264,
   45,   41,   41,   41,   41,   41,   41,   41,   49,    8,
   43,   92,   45,   12,  256,  257,   97,  259,  260,   42,
  262,  263,  258,  265,   47,  267,  262,  269,   59,   83,
  272,  273,  116,   63,  262,  256,  257,  266,  259,  260,
   39,  262,  263,  127,  265,   44,  267,  131,  269,  272,
  273,  272,  273,   41,   95,   43,   41,   45,   43,   58,
   45,   41,   59,   43,   59,   45,   59,   66,   67,   68,
   69,   70,   71,   59,  256,  257,   59,  259,  260,   40,
  262,  263,   41,  265,   41,  267,  266,  269,  129,  119,
  272,  273,  256,  257,   40,  259,  260,   44,  262,  263,
  141,  265,  259,  267,  134,  269,   72,   73,  272,  273,
  256,  257,  262,  259,  260,   59,  262,  263,  261,  265,
  258,  267,   41,  269,   74,   75,  272,  273,  256,  257,
  259,  259,  260,   40,  262,  262,   41,  265,  261,  267,
  264,  269,  261,   41,  272,  273,  262,  259,   59,  264,
  264,  264,   41,   59,   11,   59,  258,   59,   59,  258,
   59,  258,  135,   80,  258,  113,  258,   -1,   86,  258,
   -1,  258,  274,  275,  276,   -1,   -1,  274,  275,  276,
  259,  271,  274,  275,  276,  271,   -1,   -1,  258,  258,
   -1,   -1,   -1,  272,  273,   -1,   -1,  259,   -1,  259,
   -1,   -1,  259,  258,  274,  275,  276,  274,  275,  276,
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

//#line 241 "gramatica.y"

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

public static void main(String[] args) throws FileNotFoundException{
    colaAmbito.add("m:");
    System.out.println("Iniciando compilacion...");
    lex=new Lex(args[0]);
    par=new Parser(false);
    par.run();
    System.out.println("Fin compilacion");
    System.out.println();
    System.out.println("Lista de Reglas");
    mostrarReglas(reglas);
    System.out.println();
    System.out.println("Tabla de Simbolos");
    mostrarTS();
}


int yylex(){
    int token;
    try {
      token = lex.getToken();
      yylval=new ar.tp.parser.ParserVal(lex.yylval);
    } catch (IOException e) {
      token=-1;
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
//#line 493 "Parser.java"
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
case 6:
//#line 32 "gramatica.y"
{yyval=new ar.tp.parser.ParserVal(crear_terceto("begfunct",new ar.tp.parser.ParserVal("-"),new ar.tp.parser.ParserVal("-")));pila.push(reglas.size());}
break;
case 7:
//#line 35 "gramatica.y"
{pila.push(reglas.size());}
break;
case 8:
//#line 38 "gramatica.y"
{
    pointer=pila.pop();
    Terceto t = reglas.get(pointer);
    PV = new ar.tp.parser.ParserVal(
      crear_terceto("BI",new ar.tp.parser.ParserVal("-"),new ar.tp.parser.ParserVal("-"))
    );
    t.b = new ar.tp.parser.ParserVal(reglas.size());
    reglas.set(pointer, t);
    pila.push(reglas.size()-1);yyval=PV;}
break;
case 13:
//#line 57 "gramatica.y"
{pointer=pila.pop();
    Terceto t = reglas.get(pointer);
    t.b = new ar.tp.parser.ParserVal(reglas.size());
    reglas.set(pointer, t);}
break;
case 18:
//#line 65 "gramatica.y"
{System.out.println("ERROR on line "+lex.line+" sentencia invalida");}
break;
case 19:
//#line 68 "gramatica.y"
{    
    crear_terceto(
      "BF",
      new ar.tp.parser.ParserVal("[" + (reglas.size() - 1) + "]"),
      new ar.tp.parser.ParserVal("-")
    );
    pila.push(reglas.size());
    crear_terceto(
      "BI",
      new ar.tp.parser.ParserVal("-"),
      new ar.tp.parser.ParserVal("-")
    );
    t=reglas.get(pila.peek()-1);
    t.b=new ar.tp.parser.ParserVal(reglas.size());
    reglas.set(pila.pop()-1,t);
    t=reglas.get(reglas.size()-1);
    t.b=new ar.tp.parser.ParserVal("["+pila.pop()+"]");
    }
break;
case 20:
//#line 86 "gramatica.y"
{System.out.println("ERROR on line "+lex.line+" 'do' expected");}
break;
case 23:
//#line 91 "gramatica.y"
{System.out.println("ERROR on line "+lex.line+": ')' expected");}
break;
case 24:
//#line 92 "gramatica.y"
{System.out.println("ERROR on line "+lex.line+": '(' expected");}
break;
case 25:
//#line 93 "gramatica.y"
{System.out.println("ERROR on line "+lex.line+": 'then' expected");}
break;
case 26:
//#line 94 "gramatica.y"
{System.out.println("ERROR on line "+lex.line+": 'end_if' expected");}
break;
case 27:
//#line 97 "gramatica.y"
{ar.tp.parser.ParserVal PV = new ar.tp.parser.ParserVal(crear_terceto("BF",new ar.tp.parser.ParserVal("["+(reglas.size()-1)+"]"),new ar.tp.parser.ParserVal("-")));pila.push(reglas.size()-1);yyval=PV;}
break;
case 29:
//#line 103 "gramatica.y"
{yyval=new ar.tp.parser.ParserVal(crear_terceto(">",val_peek(2),val_peek(0)));}
break;
case 30:
//#line 104 "gramatica.y"
{yyval=new ar.tp.parser.ParserVal(crear_terceto("<",val_peek(2),val_peek(0)));}
break;
case 31:
//#line 105 "gramatica.y"
{yyval=new ar.tp.parser.ParserVal(crear_terceto("=",val_peek(2),val_peek(0)));}
break;
case 32:
//#line 106 "gramatica.y"
{yyval=new ar.tp.parser.ParserVal(crear_terceto(">=",val_peek(2),val_peek(0)));}
break;
case 33:
//#line 107 "gramatica.y"
{yyval=new ar.tp.parser.ParserVal(crear_terceto("<=",val_peek(2),val_peek(0)));}
break;
case 34:
//#line 108 "gramatica.y"
{yyval=new ar.tp.parser.ParserVal(crear_terceto("<>",val_peek(2),val_peek(0)));}
break;
case 35:
//#line 109 "gramatica.y"
{System.out.println("ERROR on line "+lex.line+": second expresion expected");}
break;
case 36:
//#line 110 "gramatica.y"
{System.out.println("ERROR on line "+lex.line+": second expresion expected");}
break;
case 37:
//#line 111 "gramatica.y"
{System.out.println("ERROR on line "+lex.line+": second expresion expected");}
break;
case 38:
//#line 112 "gramatica.y"
{System.out.println("ERROR on line "+lex.line+": second expresion expected");}
break;
case 39:
//#line 113 "gramatica.y"
{System.out.println("ERROR on line "+lex.line+": second expresion expected");}
break;
case 40:
//#line 114 "gramatica.y"
{System.out.println("ERROR on line "+lex.line+": second expresion expected");}
break;
case 41:
//#line 117 "gramatica.y"
{guardarVariable(val_peek(0).sval,new Symbol(val_peek(1).sval,"parametro"));yyval=val_peek(0);pilaString.push(val_peek(1).sval);}
break;
case 42:
//#line 118 "gramatica.y"
{System.out.println("ERROR on line "+lex.line+": datatype expected");}
break;
case 43:
//#line 119 "gramatica.y"
{System.out.println("ERROR on line "+lex.line+": identifier expected");}
break;
case 44:
//#line 122 "gramatica.y"
{yyval=new ar.tp.parser.ParserVal(crear_terceto("ret",val_peek(1),new ar.tp.parser.ParserVal("-")));}
break;
case 45:
//#line 123 "gramatica.y"
{System.out.println("ERROR on line "+lex.line+": ')' expected");}
break;
case 46:
//#line 124 "gramatica.y"
{System.out.println("ERROR on line "+lex.line+": '(' expected");}
break;
case 47:
//#line 127 "gramatica.y"
{yyval=new ar.tp.parser.ParserVal(crear_terceto(":=",val_peek(2),val_peek(0)));}
break;
case 48:
//#line 128 "gramatica.y"
{System.out.println("ERROR on line "+lex.line+": expresion expected");}
break;
case 49:
//#line 129 "gramatica.y"
{System.out.println("ERROR on line "+lex.line+": identifier expected");}
break;
case 51:
//#line 133 "gramatica.y"
{System.out.println("ERROR on line "+lex.line+": '(' expected");}
break;
case 52:
//#line 134 "gramatica.y"
{System.out.println("ERROR on line "+lex.line+": ')' expected");}
break;
case 53:
//#line 135 "gramatica.y"
{System.out.println("ERROR on line "+lex.line+": String expected");}
break;
case 54:
//#line 138 "gramatica.y"
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
case 55:
//#line 153 "gramatica.y"
{
        ArrayList<String> errores=new ArrayList<String>();
        if(buscarVariable(val_peek(3).sval)!=null)
            errores.add("declared");
        if(pilaString.pop().compareTo(val_peek(5).sval)!=0)
            errores.add("typeNotMatch");
        yyval=new ar.tp.parser.ParserVal(crear_terceto("endfun",val_peek(3),new ar.tp.parser.ParserVal("-"),errores));
        t=reglas.get(pila.peek()-1);
        t.a=new ar.tp.parser.ParserVal(val_peek(3).sval);
        reglas.set(pila.pop()-1,t);
        colaAmbito.remove(colaAmbito.size()-1);
        if (!errores.contains("declared")){
            guardarVariable(val_peek(3).sval,new Symbol(val_peek(5).sval,"Fun"));
        };
    }
break;
case 56:
//#line 168 "gramatica.y"
{
        for (int i=0; i<variables.size(); i++) {
            ArrayList<String> errores=new ArrayList<String>();
            if(buscarVariable(val_peek(0).sval)!=null)
                errores.add("declared");
            crear_terceto("decl",val_peek(2),new ar.tp.parser.ParserVal(variables.get(i)),errores);
            guardarVariable(variables.get(i),new Symbol(val_peek(2).sval,"Var"));
        }
        variables.clear();
    }
break;
case 57:
//#line 180 "gramatica.y"
{colaAmbito.add(val_peek(0).sval+":");}
break;
case 58:
//#line 184 "gramatica.y"
{yyval=val_peek(0);}
break;
case 59:
//#line 185 "gramatica.y"
{yyval=val_peek(0);}
break;
case 61:
//#line 189 "gramatica.y"
{
        ArrayList<String> errores=new ArrayList<String>();
        if (buscarVariable(val_peek(2).sval).tipo!=buscarVariable(val_peek(0).sval).tipo){
            errores.add("datatype missmatch");    
        }
        yyval=new ar.tp.parser.ParserVal(crear_terceto("+",val_peek(2),val_peek(0),errores));}
break;
case 62:
//#line 195 "gramatica.y"
{
        ArrayList<String> errores=new ArrayList<String>();
        if (buscarVariable(val_peek(2).sval).tipo!=buscarVariable(val_peek(0).sval).tipo){
            errores.add("datatype missmatch");    
        }
        yyval=new ar.tp.parser.ParserVal(crear_terceto("-",val_peek(2),val_peek(0),errores));}
break;
case 64:
//#line 204 "gramatica.y"
{
        ArrayList<String> errores=new ArrayList<String>();
        if (buscarVariable(val_peek(2).sval).tipo!=buscarVariable(val_peek(0).sval).tipo){
            errores.add("datatype missmatch");    
        }
        yyval=new ar.tp.parser.ParserVal(crear_terceto("*",val_peek(2),val_peek(0),errores));}
break;
case 65:
//#line 210 "gramatica.y"
{
        ArrayList<String> errores=new ArrayList<String>();
        if (buscarVariable(val_peek(2).sval).tipo!=buscarVariable(val_peek(0).sval).tipo){
            errores.add("datatype missmatch");    
        }
        yyval=new ar.tp.parser.ParserVal(crear_terceto("/",val_peek(2),val_peek(0),errores));}
break;
case 72:
//#line 226 "gramatica.y"
{variables.add(val_peek(2).sval);}
break;
case 73:
//#line 227 "gramatica.y"
{variables.add(val_peek(0).sval);}
break;
case 74:
//#line 230 "gramatica.y"
{yyval=new ar.tp.parser.ParserVal(crear_terceto("exec",val_peek(2),new ar.tp.parser.ParserVal("-")));pilaString.push(buscarVariable(val_peek(2).sval).tipo);}
break;
case 75:
//#line 231 "gramatica.y"
{
        ArrayList<String> errores=new ArrayList<String>();
        if (lex.symbols.get(val_peek(1).sval).uso!="Constante"){
            if (buscarVariable(val_peek(1).sval).tipo.compareTo(tiposParFunct.get(val_peek(3).sval))!=0){
                errores.add("type missmatch");
            }
        }
        yyval=new ar.tp.parser.ParserVal(crear_terceto("exec",val_peek(3),val_peek(1)));}
break;
//#line 934 "Parser.java"
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
