package ar.tp.lexer;

import java.io.*;
import java.util.HashMap;

import ar.tp.acciones.*;
import ar.tp.ast.*;

public class Lex {
        public HashMap<String, Symbol> symbols = new HashMap<>();
        HashMap<String, Integer> reservedWords = new HashMap<>();
        Pointer pointer;
        public String yylval;
        public int line = 1;
        private final BufferedReader sourceCodeReader;
        private String string;

        public Lex(String filename) throws FileNotFoundException {
                this.sourceCodeReader = new BufferedReader(new FileReader(filename));

                reservedWords.put("if", 257);
                reservedWords.put("then", 258);
                reservedWords.put("else", 261);
                reservedWords.put("begin", 262);
                reservedWords.put("end", 263);
                reservedWords.put("end_if", 264);
                reservedWords.put("print", 265);
                reservedWords.put("while", 266);
                reservedWords.put("do", 267);
                reservedWords.put("fun", 268);
                reservedWords.put("return", 269);
                reservedWords.put("uinteger", 272);
                reservedWords.put("longint", 273);

                symbols.put("if", new Symbol("String", "pr"));
                symbols.put("then", new Symbol("String", "pr"));
                symbols.put("else", new Symbol("String", "pr"));
                symbols.put("begin", new Symbol("String", "pr"));
                symbols.put("end", new Symbol("String", "pr"));
                symbols.put("end_if", new Symbol("String", "pr"));
                symbols.put("print", new Symbol("String", "pr"));
                symbols.put("while", new Symbol("String", "pr"));
                symbols.put("do", new Symbol("String", "pr"));
                symbols.put("fun", new Symbol("String", "pr"));
                symbols.put("return", new Symbol("String", "pr"));
                symbols.put("uinteger", new Symbol("String", "pr"));
                symbols.put("longint", new Symbol("String", "pr"));
        }

        SemanticAction none = new None();
        SemanticAction start = new Start();
        SemanticAction startString = new StartString();
        SemanticAction newLine = new NewLine();
        SemanticAction write = new Write();
        SemanticAction finishId = new FinishId();
        SemanticAction finishIdNewLine = new FinishIdNewLine();
        SemanticAction asterisk = new Asterisk();
        SemanticAction literal = new Literal();
        SemanticAction finishAsterisk = new FinishAsterisk();
        SemanticAction greader = new Greader();
        SemanticAction greaderEqual = new GreaderEqual();
        SemanticAction lower = new Lower();
        SemanticAction lowerEqual = new LowerEqual();
        SemanticAction different = new Different();
        SemanticAction warningStringNewLine = new WarningStringNL();
        SemanticAction finishString = new FinishString();
        SemanticAction finishConstant = new FinishConstant();
        SemanticAction finishConstantNewLine = new FinishConstantNewLine();
        SemanticAction finishLower = new FinishLower();
        SemanticAction finishGreader = new FinishGreader();
        SemanticAction assign = new Assign();
        SemanticAction warningDot = new WarningDot();

        // L 1
        // D 2
        // / 3D
        // * 4
        // + 5
        // - 6
        // = 7
        // < 8
        // > 9
        // : 10
        // " 11
        // @ 12
        // ( 13
        // ) 14
        // , 15
        // ; 16
        // otro 17
        // bl/tab/nl 18
        // $ (eof) 19

        // ESTADO TRAMPA -2

    int[][] states = {
            //           L, D, /, *, +, -, =, <, >, :, ", @, (, ), ;, o , \t, \n, .
            /* 0 */ {  1,  7, -1, -2, -1, -1, -6,  6,  5,  8,  9,  1, -1, -1, -1,  0,  0,  0,  0 },
            /* 1 */ {  1,  1, -1, -1, -1, -1, -1, -1, -1, -1, -1,  1, -1, -1, -1, -1, -1, -1, -1 },
            /* 2 */ { -1, -1,  3, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,  2 },
            /* 3 */ {  3,  3,  4,  3,  3,  3,  3,  3,  3,  3,  3,  3,  3,  3,  3, -2,  3,  3,  3 },
            /* 4 */ {  3,  3,  0,  3,  3,  3,  3,  3,  3,  3,  3,  3,  3,  3,  3, -2,  4,  4,  4 },
            /* 5 */ { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
            /* 6 */ { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,  6 },
            /* 7 */ { -1,  7, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,  7 },
            /* 8 */ { -2, -2, -2, -2, -2, -2, -1, -2, -2, -2, -2, -2, -2, -2, -2, -2, -2, -2, -2 },
            /* 9 */ {  9,  9,  9,  9,  9,  9,  9,  9,  9,  9, -1,  9,  9,  9,  9,  9,  9,  9,  9 }
    };

        SemanticAction[][] actions = {
                //   L         D         /         *         +         -         =         <         >         :         "         @         (         )         ,         ;         otro      bl/tab    nl        eof       .
                /* 0 */ {  start,    start,    literal,  none,     literal,  literal,  literal,  none,     none,     none,     startString, start,    literal,  literal,  literal,  literal,  null,     none,     newLine,  none,     warningDot }, 
                /* 1 */ {  write,    write,    finishId, finishId, finishId, finishId, finishId, finishId, finishId, finishId, finishId,   write,    finishId, finishId, finishId, finishId, finishId, finishId, finishIdNewLine, finishId, warningDot },
                /* 2 */ {  asterisk, asterisk, none,     asterisk, asterisk, asterisk, asterisk, asterisk, asterisk, asterisk, asterisk, asterisk, asterisk, asterisk, asterisk, asterisk, asterisk, asterisk, finishAsterisk, asterisk, warningDot }, 
                /* 3 */ {  none,     none,     none,     none,     none,     none,     none,     none,     none,     none,     none,     none,     none,     none,     none,     none,     none,     newLine,  none,     warningDot }, 
                /* 4 */ {  none,     none,     none,     none,     none,     none,     none,     none,     none,     none,     none,     none,     none,     none,     none,     none,     none,     newLine,  none,     warningDot }, 
                /* 5 */ {  greader,  greader,  greader,  greader,  greader,  greader,  greaderEqual, greader, greader, greader, greader,  greader,  greader,  greader,  greader,  greader,  greader,  greader,  finishGreader, greader, warningDot },
                /* 6 */ {  lower,    lower,    lower,    lower,    lower,    lower,    lowerEqual, lower,   different, lower, lower,    lower,    lower,    lower,    lower,    lower,    lower,    lower,    finishLower,  lower,    warningDot }, 
                /* 7 */ {  finishConstant, write, finishConstant, finishConstant, finishConstant, finishConstant, finishConstant, finishConstant, finishConstant, finishConstant, finishConstant, finishConstant, finishConstant, finishConstant, finishConstant, finishConstant, finishConstant, finishConstant, finishConstantNewLine, finishConstant, warningDot }, 
                /* 8 */ {  none,     none,     none,     none,     none,     none,     assign,    none,     none,     none,     none,     none,     none,     none,     none,     none,     none,     none,     none,     none,     warningDot },
                /* 8 */ {  write,    write,    write,    write,    write,    write,    write,    write,    write,    finishString,    write, write,   write,    write,    write,    write,    write,    write,    warningStringNewLine, none,     warningDot },
        };

        public int getToken() throws IOException {
                int currentState = 0;
                int currentChar;
                int charValue;
                SemanticAction action;
                while (currentState != -1) {
                        sourceCodeReader.mark(1);
                        currentChar = sourceCodeReader.read();
                        charValue = decode(currentChar);
                        action = actions[currentState][charValue];
                        pointer = action.ejecutar(sourceCodeReader, this, currentChar, symbols, reservedWords);
                        currentState = states[currentState][charValue];
                        if (currentState == -2) {
                                System.out.println("Error: line N°" + this.line);
                                currentState = 0;
                        }
                }
                return pointer.token;
        }

        public void setString(String c) {
                this.string = c;
        }

        public String getString() {
                return this.string;
        }

        private int decode(int currentChar) {// A partir de un caracter devuelve su valor de matriz correspondiente
                int value;
                if (Character.isLetter((char) currentChar) || currentChar == '_') {
                        value = 0;
                } else if (Character.isDigit((char) currentChar)) {
                        value = 1;
                } else if (currentChar == -1) {
                        value = 19;
                } else
                    value = switch ((char) currentChar) {
                        case '.' -> 20;
                        case '/' -> 2;
                        case '*' -> 3;
                        case '+' -> 4;
                        case '-' -> 5;
                        case '=' -> 6;
                        case '<' -> 7;
                        case '>' -> 8;
                        case ':' -> 9;
                        case '"' -> 10;
                        case '@' -> 11;
                        case '(' -> 12;
                        case ')' -> 13;
                        case ',' -> 14;
                        case ';' -> 15;
                        case ' ', '\t' -> 17;
                        case '\n', '\r' -> 18;
                        default -> 16;
                    };
                return value;
        }
}
