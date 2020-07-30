package ir.ac.sbu.Parser;

import ir.ac.sbu.Lexer.Symbol;

public interface Lexical {
    String nextToken();
    Symbol currentToken();
}