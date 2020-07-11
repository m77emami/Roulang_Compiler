
%%

%public
%class SyntaxRecognition
%type returnValue
%unicode


%{
    StringBuilder str = new StringBuilder();//use for saving the string content
    class returnValue{
        String type;//can be keyword, numbers, strings and etc
        String token;//the exact token that has been read

        //constructor
        public returnValue(String type , String token){
            this.type = type;
            this.token = token;
        }

    }
%}

LineTerminator = \n | \r | \r\n
whiteSpace = " " | [ \t\f]

//Numbers
Decimal           = [0-9]+
Long              = [0-9]+ [lL]
HexInteger        = 0 [xX] [0-9a-fA-F] {1,8}
HexLong           = 0 [xX] [0-9a-fA-F] {1,16} [lL]
DoubleReal        = [0-9]+ \. [0-9]* | \. [0-9]+ | [0-9]+ \.
FloatReal         = [0-9]+ \. [0-9]* [1F] | \. [0-9]+ [1F] | [0-9]+ \. [1F]
Scientific        = {DoubleReal} [eE] [+-]? [0-9]+

//Comments
LineComment = "//" [^\r\n]*
ParagraphComment = "/*" ~"*/"

//Identifier
Identifier = [a-zA-Z][_a-zA-Z0-9]* //Identifier cannot start with underline

//Character
Character = \'[^\\]\'
SpecialCharacter = \'[\\][^]\'

//String
%state String

%%
//default state : YYINITIAL
<YYINITIAL>{
//Reserved Key words
"int"                     {return new returnValue("Keywords",yytext());}
"short"                   {return new returnValue("Keywords",yytext());}
"long"                    {return new returnValue("Keywords",yytext());}
"float"                   {return new returnValue("Keywords",yytext());}
"double"                  {return new returnValue("Keywords",yytext());}
"char"                    {return new returnValue("Keywords",yytext());}
"string"                  {return new returnValue("Keywords",yytext());}
"const"                   {return new returnValue("Keywords",yytext());}
"for"                     {return new returnValue("Keywords",yytext());}
"foreach"                 {return new returnValue("Keywords",yytext());}
"while"                   {return new returnValue("Keywords",yytext());}
"do"                      {return new returnValue("Keywords",yytext());}
"in"                      {return new returnValue("Keywords",yytext());}
"break"                   {return new returnValue("Keywords",yytext());}
"continue"                {return new returnValue("Keywords",yytext());}
"new"                     {return new returnValue("Keywords",yytext());}
"sizeof"                  {return new returnValue("Keywords",yytext());}
"true"                    {return new returnValue("Keywords",yytext());}
"if"                      {return new returnValue("Keywords",yytext());}
"else"                    {return new returnValue("Keywords",yytext());}
"switch"                  {return new returnValue("Keywords",yytext());}
"case"                    {return new returnValue("Keywords",yytext());}
"default"                 {return new returnValue("Keywords",yytext());}
"auto"                    {return new returnValue("Keywords",yytext());}
"volatile"                {return new returnValue("Keywords",yytext());}
"static"                  {return new returnValue("Keywords",yytext());}
"goto"                    {return new returnValue("Keywords",yytext());}
"signed"                  {return new returnValue("Keywords",yytext());}
"bool"                    {return new returnValue("Keywords",yytext());}
"void"                    {return new returnValue("Keywords",yytext());}
"return"                  {return new returnValue("Keywords",yytext());}
"record"                  {return new returnValue("Keywords",yytext());}
"repeat"                  {return new returnValue("Keywords",yytext());}
"until"                   {return new returnValue("Keywords",yytext());}
"function"                {return new returnValue("Keywords",yytext());}
"println"                 {return new returnValue("Keywords",yytext());}
"false"                   {return new returnValue("Keywords",yytext());}

//these should be black
"and"                     {return new returnValue("Others",yytext());}
"or"                      {return new returnValue("Others",yytext());}
"not"                     {return new returnValue("Others",yytext());}
"begin"                   {return new returnValue("Others",yytext());}
"end"                     {return new returnValue("Others",yytext());}

//Numbers
{Decimal}                 {return new returnValue("Integer",yytext());}
{Long}                    {return new returnValue("Integer",yytext());}
{HexInteger}              {return new returnValue("Integer",yytext());}
{HexLong}                 {return new returnValue("Integer",yytext());}
{DoubleReal}              {return new returnValue("Real",yytext());}
{FloatReal}               {return new returnValue("Real",yytext());}
{Scientific}              {return new returnValue("Real",yytext());}

//Characters
{Character}               {return new returnValue("Character",yytext());}
{SpecialCharacter}        {return new returnValue("SpecialCharacter",yytext());}

//String
\"                        {str = new StringBuilder();yybegin(String);}

//Comments
{LineComment}             {return new returnValue("Comments",yytext());}
{ParagraphComment}        {return new returnValue("Comments",yytext());}

//Identifiers
{Identifier}              {return new returnValue("Identifiers",yytext());}

//Others
{whiteSpace}              {return new returnValue("whiteSpace",yytext());}
{LineTerminator}          {return new returnValue("LineTerminator",yytext());}
[^]                       {return new returnValue("Others",yytext());}
}

<String>{
\"                        {yybegin(YYINITIAL);return new returnValue("String",str.toString());}
//special character should be italic
\\[^]                     {str.append("<i>");str.append(yytext());str.append("</i>");}
[^]                       {str.append(yytext());}
}


