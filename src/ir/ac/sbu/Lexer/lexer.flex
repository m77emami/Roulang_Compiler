package ir.ac.sbu.Lexer;
import java.io.IOException;
import ir.ac.sbu.Parser.Lexical;

%%

%class SyntaxRecognition
%unicode
%line
%column
%type Symbol
%function next_token
%implements Lexical
%public


%{
    private StringBuilder str = new StringBuilder();
    private char ch;
    private Symbol currentSymbol = null;

    private Symbol symbol(String token) {
        return new Symbol(token);
    }

    private Symbol symbol(String token, Object val) {
        return new Symbol(token, val);
    }

    public Symbol currentToken() {
        return currentSymbol;
    }

    public String nextToken() {
        try {
            currentSymbol = next_token();
            return currentSymbol.getToken();
        } catch (IOException e) {
            throw new RuntimeException("Unable to get next token", e);
        }
    }
%}

LineTerminator = \n | \r | \r\n
whiteSpace = {LineTerminator} | " " | [ \t\f]

//Numbers
Decimal           = [0-9]+
Long              = {Decimal} [lL]
HexInteger        = 0 [xX] [0-9a-fA-F] {1,8}
HexLong           = 0 [xX] [0-9a-fA-F] {1,16} [lL]
DoubleReal        = [0-9]+ \. [0-9]* | \. [0-9]+ | [0-9]+ \.
FloatReal         = {DoubleReal} [fF]
Scientific        = ({DoubleReal} | {Decimal}) [eE] [+-]? {Decimal}

//Comments
LineComment = "//" [^\r\n]*
ParagraphComment = "/*" ~"*/"

//Identifier
Identifier = [a-zA-Z][_a-zA-Z0-9]* //Identifier cannot start with underline

//Character
SpecialCharacter = [\t\r\n\"\'\\]  //\'[\\][^]\'

//States
%state String_state
%state Character_begin
%state Character_finish

%%
//default state : YYINITIAL
<YYINITIAL>{
//Reserved Key words
"int"                     {return symbol("int");}
"short"                   {return symbol("short");}
"long"                    {return symbol("long");}
"float"                   {return symbol("float");}
"double"                  {return symbol("double");}
"char"                    {return symbol("char");}
"string"                  {return symbol("string");}
"const"                   {return symbol("const");}
"for"                     {return symbol("for");}
"foreach"                 {return symbol("foreach");}
"while"                   {return symbol("while");}
"do"                      {return symbol("do");}
"in"                      {return symbol("in");}
"break"                   {return symbol("break");}
"continue"                {return symbol("continue");}
"new"                     {return symbol("new");}
"sizeof"                  {return symbol("sizeof");}
"true"                    {return symbol("true");}
"if"                      {return symbol("if");}
"else"                    {return symbol("else");}
"switch"                  {return symbol("switch");}
"case"                    {return symbol("case");}
"default"                 {return symbol("default");}
"auto"                    {return symbol("auto");}
"volatile"                {return symbol("volatile");}
"static"                  {return symbol("static");}
"goto"                    {return symbol("goto");}
"signed"                  {return symbol("signed");}
"bool"                    {return symbol("bool");}
"void"                    {return symbol("void");}
"return"                  {return symbol("return");}
"record"                  {return symbol("record");}
"repeat"                  {return symbol("repeat");}
"until"                   {return symbol("until");}
"function"                {return symbol("function");}
"println"                 {return symbol("println");}
"false"                   {return symbol("false");}

//symbols
"=="                    {return symbol("==");}
"!="                    {return symbol("!=");}
"<="                    {return symbol("<=");}
"<"                     {return symbol("<");}
">"                     {return symbol(">");}
">="                    {return symbol(">=");}
"="                     {return symbol("=");}
"~"                     {return symbol("~");}
"&"                     {return symbol("&");}
"and"                   {return symbol("and");}
"or"                    {return symbol("or");}
"not"                   {return symbol("not");}
"|"                     {return symbol("|");}
"^"                     {return symbol("^");}
"*"                     {return symbol("++");}
"+"                     {return symbol("+");}
"+="                    {return symbol("+=");}
"."                     {return symbol(".");}
","                     {return symbol(",");}
":"                     {return symbol(":");}
";"                     {return symbol(";");}
"["                     {return symbol("[");}
"]"                     {return symbol("]");}
"++"                    {return symbol("++");}
"--"                    {return symbol("--");}
"-"                     {return symbol("-");}
"-="                    {return symbol("-=");}
"*="                    {return symbol("*=");}
"/="                    {return symbol("/=");}
"/"                     {return symbol("/");}
"%"                     {return symbol("%");}
"begin"                 {return symbol("begin");}
"end"                   {return symbol("end");}
"("                     {return symbol("(");}
")"                     {return symbol(")");}


//Numbers
{Decimal}                 {return symbol("const_int", Integer.parseInt(yytext()));}
{Long}                    {return symbol("const_long", Long.parseLong(yytext().substring(0, yytext().length() - 1)));}
{HexInteger}              {return symbol("const_int", Integer.parseInt(yytext().substring(2), 16));}
{HexLong}                 {return symbol("const_long", Long.parseLong(yytext().substring(2, yytext().length() - 1), 16));}
{DoubleReal}              {return symbol("const_double", Double.parseDouble(yytext()));}
{FloatReal}               {return symbol("const_float", Float.parseFloat(yytext().substring(0, yytext().length() - 1)));}
{Scientific}              {return symbol("const_double", Double.parseDouble(yytext()));}

//String
\"                        {str = new StringBuilder();yybegin(String_state);}

//Character
\'                        {yybegin(Character_begin);}

//Comments
{LineComment}             {/* do nothing */}
{ParagraphComment}        {/* do nothing */}

//Identifiers
{Identifier}              {return symbol("id", yytext());}

//Others
{whiteSpace}              {/* do nothing */}
[^]                       {System.err.println("Error: Unidentified token.");}
}

<String_state>{
\"                        {str = new StringBuilder(); yybegin(YYINITIAL); return symbol("const_string", str.toString());}
[^]                       {str.append(yytext());}
}

<Character_begin>{
\'                        {yybegin(YYINITIAL); System.err.println("Error: Null Character");}
{SpecialCharacter}        {yybegin(Character_finish); ch = yytext().charAt(0);}
[^]                       {yybegin(Character_finish); ch = yytext().charAt(0);}
}

<Character_finish>{
\'                        {yybegin(YYINITIAL); return symbol("const_char", ch);}
[^]                       {yybegin(YYINITIAL); System.err.println("Error: Insufficient number of characters in ''.");}
}
