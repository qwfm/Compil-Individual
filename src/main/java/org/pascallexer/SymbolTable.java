package org.pascallexer;

import java.util.*;

public class SymbolTable {
    private final Map<String, Token> table = new LinkedHashMap<>();

    public void add(Token token) {
        if (token.type == TokenType.IDENT ||
                token.type == TokenType.INTEGER_LITERAL ||
                token.type == TokenType.REAL_LITERAL ||
                token.type == TokenType.CHAR_LITERAL ||
                token.type == TokenType.STRING_LITERAL) {
            table.putIfAbsent(token.lexeme, token);
        }
    }

    public Collection<Token> values() {
        return table.values();
    }
}

