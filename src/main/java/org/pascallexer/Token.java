package org.pascallexer;

public class Token {
    public final TokenType type;
    public final String lexeme;
    public final int line, column;

    public Token(TokenType type, String lexeme, int line, int column) {
        this.type = type;
        this.lexeme = lexeme;
        this.line = line;
        this.column = column;
    }

    @Override
    public String toString() {
        return String.format("(%d:%d) %-12s '%s'", line, column, type, lexeme);
    }
}
