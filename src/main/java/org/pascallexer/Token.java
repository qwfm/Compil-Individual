package org.pascallexer;

public class Token {
    public final TokenType type;
    public final Object literal;
    public final String lexeme;
    public final int line, column;

    public Token(TokenType type, String lexeme, Object literal, int line, int column) {
        this.type = type;
        this.lexeme = lexeme;
        this.literal = literal;
        this.line = line;
        this.column = column;
    }

    @Override
    public String toString() {
        String lit = (literal != null) ? literal.toString() : "";
        return String.format("(%d:%d) %-15s %-10s '%s'", line, column, type, lit, lexeme);
    }
}
