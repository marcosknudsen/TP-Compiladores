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
        private BufferedReader sourceCodeReader;
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

        int trapState = -2;
        int finishState = -1;

        int states[][] = {
                //           L, D, /, *, +, -, =, <, >, :, ", @, (, ), ;, o , \t, \n, .
                /* 0 */ {  1, 7,finishState,-2,finishState,finishState,-6, 6, 5, 8, 9, 1,finishState,finishState,finishState, trapState, 0, 0, finishState },
                /* 1 */ {  1,finishState,finishState,finishState,finishState,finishState,finishState,finishState,finishState,finishState,finishState,finishState,finishState,finishState,finishState, finishState, finishState, finishState, finishState },
                /* 2 */ { finishState,finishState, 3,finishState,finishState,finishState,finishState,finishState,finishState,finishState,finishState,finishState,finishState,finishState,finishState, finishState, finishState, finishState, 2 },
                /* 3 */ {  3, 3, 4, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, trapState, 3, 3, 3 },
                /* 4 */ {  3, 3, 0, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, trapState, 4, 4, 4 },
                /* 5 */ { finishState,finishState,finishState,finishState,finishState,finishState,finishState,finishState,finishState,finishState,finishState,finishState,finishState,finishState,finishState, finishState, finishState, finishState, finishState },
                /* 6 */ { finishState,finishState,finishState,finishState,finishState,finishState,finishState,finishState,finishState,finishState,finishState,finishState,finishState,finishState,finishState, finishState, finishState, finishState, 6 },
                /* 7 */ { finishState, 7,finishState,finishState,finishState,finishState,finishState,finishState,finishState,finishState,finishState,finishState,finishState,finishState,finishState, finishState, finishState, finishState, 7 },
                /* 9 */ {  9, 9, 9, 9, 9, 9, 9, 9, 9, finishState, 9, 9, 9, 9, 9, 9, finishState, 9, 9 }
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
                /* 8 */ {  write,    write,    write,    write,    write,    write,    write,    write,    write,    write,    finishString, write,   write,    write,    write,    write,    write,    write,    warningStringNewLine, none,     warningDot }, 
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
                        pointer = action.ejecutar(sourceCodeReader, (Lex) this, currentChar, symbols, reservedWords);
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
                        switch ((char) currentChar) {
                                case '.':
                                        value = 20;
                                        break;
                                case '/':
                                        value = 2;
                                        break;
                                case '*':
                                        value = 3;
                                        break;
                                case '+':
                                        value = 4;
                                        break;
                                case '-':
                                        value = 5;
                                        break;
                                case '=':
                                        value = 6;
                                        break;
                                case '<':
                                        value = 7;
                                        break;
                                case '>':
                                        value = 8;
                                        break;
                                case ':':
                                        value = 9;
                                        break;
                                case '"':
                                        value = 10;
                                        break;
                                case '@':
                                        value = 11;
                                        break;
                                case '(':
                                        value = 12;
                                        break;
                                case ')':
                                        value = 13;
                                        break;
                                case ',':
                                        value = 14;
                                        break;
                                case ';':
                                        value = 15;
                                        break;
                                case ' ':
                                case '\t':
                                        value = 17;
                                        break;
                                case '\n':
                                case '\r':
                                        value = 18;
                                        break;
                                default:
                                        value = 16;
                                        break;

                        }
                return value;
        }
}
