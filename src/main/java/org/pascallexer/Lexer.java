package org.pascallexer;

import java.util.*;

public class Lexer {
    private final String input;
    private int pos = 0, line = 1, column = 1;
    private final Map<String, TokenType> keywords = Map.ofEntries(
            Map.entry("BEGIN", TokenType.BEGIN),
            Map.entry("END",   TokenType.END),
            Map.entry("IF",    TokenType.IF),
            // … інші ключові слова
            Map.entry("VAR",   TokenType.VAR)
    );
    private final List<Token> symbolTable = new ArrayList<>();

    public Lexer(String input) {
        this.input = input;
    }

    public List<Token> tokenize() {
        List<Token> tokens = new ArrayList<>();
        Token tok;
        do {
            tok = nextToken();
            tokens.add(tok);
        } while (tok.type != TokenType.EOF);
        return tokens;
    }

    private Token nextToken() {
        skipWhitespaceAndComments();
        if (pos >= input.length()) {
            return new Token(TokenType.EOF, "", line, column);
        }
        char ch = peek();
        // Буквені (DFA для ідентифікаторів/ключових слів)
        if (Character.isLetter(ch)) {
            return identifierOrKeyword();
        }
        // Цифри (DFA для чисел)
        if (Character.isDigit(ch)) {
            return number();
        }
        // Рядок у апострофах
        if (ch == '\'') {
            return stringLiteral();
        }
        // Оператори й роздільники
        switch (ch) {
            case '+': return simpleToken(TokenType.PLUS, "+");
            case '-': return simpleToken(TokenType.MINUS, "-");
            case '*': return simpleToken(TokenType.MUL, "*");
            case '/': return simpleToken(TokenType.DIV, "/");
            case '(' : return simpleToken(TokenType.LPAREN, "(");
            case ')' : return simpleToken(TokenType.RPAREN, ")");
            case ';' : return simpleToken(TokenType.SEMICOLON, ";");
            case ',' : return simpleToken(TokenType.COMMA, ",");
            case ':' :
                if (peekNext() == '=') return compositeToken(TokenType.ASSIGN, ":=");
                else return simpleToken(TokenType.COLON, ":");
            case '.' : return simpleToken(TokenType.DOT, ".");
            case '<':
                if (peekNext() == '=') return compositeToken(TokenType.LE, "<=");
                else if (peekNext() == '>') return compositeToken(TokenType.NE, "<>");
                else return simpleToken(TokenType.LT, "<");
            case '>':
                if (peekNext() == '=') return compositeToken(TokenType.GE, ">=");
                else return simpleToken(TokenType.GT, ">");
            default:
                // Невідомий символ – лексична помилка
                return errorToken("Unexpected character: '" + ch + "'");
        }
    }

    // Приклад DFA для ідентифікаторів і ключових слів
    private Token identifierOrKeyword() {
        int startLine = line, startCol = column;
        StringBuilder sb = new StringBuilder();
        while (pos < input.length() &&
                (Character.isLetterOrDigit(peek()) || peek() == '_')) {
            sb.append(nextChar());
        }
        String lexeme = sb.toString().toUpperCase();
        TokenType type = keywords.getOrDefault(lexeme, TokenType.IDENT);
        Token tok = new Token(type, lexeme, startLine, startCol);
        if (type == TokenType.IDENT) {
            symbolTable.add(tok);
        }
        return tok;
    }

    // DFA для чисел (цілі та дійсні)
    private Token number() {
        int startLine = line, startCol = column;
        StringBuilder sb = new StringBuilder();
        while (pos < input.length() && Character.isDigit(peek())) {
            sb.append(nextChar());
        }
        if (peek() == '.') {
            sb.append(nextChar());
            while (pos < input.length() && Character.isDigit(peek())) {
                sb.append(nextChar());
            }
        }
        String lexeme = sb.toString();
        Token tok = new Token(TokenType.NUMBER, lexeme, startLine, startCol);
        symbolTable.add(tok);
        return tok;
    }

    // DFA для рядкових літералів
    private Token stringLiteral() {
        int startLine = line, startCol = column;
        nextChar(); // відкидаємо початковий '
        StringBuilder sb = new StringBuilder();
        while (pos < input.length() && peek() != '\'') {
            sb.append(nextChar());
        }
        if (peek() == '\'') {
            nextChar(); // кінець літералу
            Token tok = new Token(TokenType.STRING, sb.toString(), startLine, startCol);
            symbolTable.add(tok);
            return tok;
        } else {
            return errorToken("Unterminated string literal");
        }
    }

    // Допоміжні методи
    private void skipWhitespaceAndComments() {
        while (pos < input.length()) {
            char ch = peek();
            // Пробіли/табуляції/переноси рядка
            if (Character.isWhitespace(ch)) {
                nextChar();
                continue;
            }
            // Коментарі { … }
            if (ch == '{') {
                nextChar();
                while (pos < input.length() && peek() != '}') {
                    nextChar();
                }
                if (peek() == '}') nextChar();
                continue;
            }
            break;
        }
    }
    private Token simpleToken(TokenType type, String lexeme) {
        int l = line, c = column;
        for (int i = 0; i < lexeme.length(); i++) nextChar();
        return new Token(type, lexeme, l, c);
    }
    private Token compositeToken(TokenType type, String lexeme) {
        return simpleToken(type, lexeme);
    }
    private Token errorToken(String msg) {
        Token tok = new Token(TokenType.ERROR, msg, line, column);
        pos++; column++;
        return tok;
    }
    private char peek() {
        return input.charAt(pos);
    }
    private char peekNext() {
        return pos+1 < input.length() ? input.charAt(pos+1) : '\0';
    }
    private char nextChar() {
        char ch = input.charAt(pos++);
        if (ch == '\n') { line++; column = 1; }
        else column++;
        return ch;
    }
}

