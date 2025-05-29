package org.pascallexer;

import java.util.*;

public class Lexer {
    private final String input;
    private int pos = 0, line = 1, column = 1;
    private final SymbolTable symbols = new SymbolTable();
    private static final Map<String, TokenType> keywords = new HashMap<>();

    static {
        String[] keys = {
                "and","array","begin","case","const","div","do","downto",
                "else","end","file","for","function","goto","if","in",
                "label","mod","nil","not","of","or","packed","procedure",
                "program","record","repeat","set","then","to","type","until",
                "var","while","with"
        };
        for (String k : keys) {
            keywords.put(k, TokenType.valueOf(k.toUpperCase()));
        }
    }

    public Lexer(String input) {
        this.input = input;
    }

    public List<Token> tokenize() {
        List<Token> tokens = new ArrayList<>();
        Token tok;
        do {
            tok = nextToken();
            tokens.add(tok);
            symbols.add(tok);
        } while (tok.type != TokenType.EOF);
        return tokens;
    }

    public Collection<Token> getSymbolTable() {
        return symbols.values();
    }

    private Token nextToken() {
        skipWhitespaceAndComments();
        if (isAtEnd()) {
            return makeToken(TokenType.EOF, "");
        }
        char c = peek();
        if (isAlpha(c)) return identifierOrKeyword();
        if (Character.isDigit(c)) return number();
        switch (c) {
            case '\'': return stringLiteral();
            case '+': return simple("+", TokenType.PLUS);
            case '-': return simple("-", TokenType.MINUS);
            case '*': return simple("*", TokenType.MUL);
            case '/': return simple("/", TokenType.DIV_OP);
            case '=': return simple("=", TokenType.EQ);
            case '<':
                if (match('=')) return simple("<=", TokenType.LE);
                if (match('>')) return simple("<>", TokenType.NE);
                return simple("<", TokenType.LT);
            case '>':
                if (match('=')) return simple(">=", TokenType.GE);
                return simple(">", TokenType.GT);
            case '(':
                if (match('*')) return commentMultiline();
                return simple("(", TokenType.LParen);
            case ')': return simple(")", TokenType.RParen);
            case '[': return simple("[", TokenType.LBracket);
            case ']': return simple("]", TokenType.RBracket);
            case '{': return commentBrace();
            case '}':
                ErrorHandler.error(line, column, "Unexpected '}'");
                advance();
                return makeToken(TokenType.ERROR, "}");
            case ',': return simple(",", TokenType.COMMA);
            case ';': return simple(";", TokenType.SEMICOLON);
            case ':': return match('=') ? simple(":=", TokenType.ASSIGN)
                    : simple(":", TokenType.COLON);
            case '.': return match('.') ? simple("..", TokenType.DOTDOT)
                    : simple(".", TokenType.DOT);
        }
        // Якщо не співпадає
        ErrorHandler.error(line, column, "Unknown character: '" + c + "'");
        advance();
        return makeToken(TokenType.ERROR, String.valueOf(c));
    }

    private Token identifierOrKeyword() {
        int startCol = column;
        while (isAlphaNumeric(peek())) advance();
        String lex = input.substring(pos - (column - startCol), pos);
        TokenType type = keywords.getOrDefault(lex.toLowerCase(), TokenType.IDENT);
        return makeToken(type, lex);
    }

    private Token number() {
        int startCol = column;
        while (Character.isDigit(peek())) advance();
        boolean isReal = false;
        if (peek() == '.' && Character.isDigit(peekNext())) {
            isReal = true;
            advance();
            while (Character.isDigit(peek())) advance();
        }
        String lex = input.substring(pos - (column - startCol), pos);
        if (isReal) {
            double val = Double.parseDouble(lex);
            return makeToken(TokenType.REAL_LITERAL, lex, val);
        } else {
            int val = Integer.parseInt(lex);
            return makeToken(TokenType.INTEGER_LITERAL, lex, val);
        }
    }

    private Token stringLiteral() {
        int startCol = column;
        advance();
        while (!isAtEnd() && peek() != '\'') {
            if (peek() == '\n') line++;
            advance();
        }
        if (isAtEnd()) {
            ErrorHandler.error(line, column, "Unterminated string literal");
            return makeToken(TokenType.ERROR, "");
        }
        advance();
        String lex = input.substring(pos - (column - startCol), pos);
        String val = lex.substring(1, lex.length() - 1);
        TokenType ttype = (val.length() == 1) ? TokenType.CHAR_LITERAL : TokenType.STRING_LITERAL;
        return new Token(ttype, lex, val, line, startCol);
    }

    private Token commentBrace() {
        advance();
        while (!isAtEnd() && peek() != '}') {
            if (peek() == '\n') line++;
            advance();
        }
        if (peek() == '}') advance();
        return nextToken();
    }

    private Token commentMultiline() {
        while (!isAtEnd() && !(peek() == '*' && peekNext() == ')')) {
            if (peek() == '\n') line++;
            advance();
        }
        if (peek() == '*' && peekNext() == ')') {
            advance(); advance();
        } else {
            ErrorHandler.error(line, column, "Unterminated comment");
        }
        return nextToken();
    }

    private void skipWhitespaceAndComments() {
        while (!isAtEnd()) {
            char c = peek();
            if (c == ' ' || c == '\r' || c == '\t' || c == '\n') {
                if (c == '\n') line++;
                advance();
            } else if (c == '/' && peekNext() == '/') {
                // single-line comment
                while (!isAtEnd() && peek() != '\n') advance();
            } else {
                break;
            }
        }
    }

    private boolean isAtEnd() {
        return pos >= input.length();
    }
    private char peek() { return isAtEnd() ? '\0' : input.charAt(pos); }
    private char peekNext() { return (pos+1 >= input.length()) ? '\0' : input.charAt(pos+1); }
    private char advance() {
        char c = peek(); pos++;
        if (c == '\n') column = 1; else column++;
        return c;
    }
    private boolean match(char expected) {
        if (isAtEnd() || peek() != expected) return false;
        advance(); return true;
    }
    private boolean isAlpha(char c) { return Character.isLetter(c); }
    private boolean isAlphaNumeric(char c) { return Character.isLetterOrDigit(c); }

    private Token simple(String lex, TokenType type) {
        int startCol = column;
        for (char ch : lex.toCharArray()) advance();
        return new Token(type, lex, null, line, startCol);
    }
    private Token makeToken(TokenType type, String lex) {
        return new Token(type, lex, null, line, column - lex.length());
    }
    private Token makeToken(TokenType type, String lex, Object lit) {
        return new Token(type, lex, lit, line, column - lex.length());
    }
}