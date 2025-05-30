package org.pascallexer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

public class Lexer {
    private final BufferedReader reader;
    private int currentChar;
    private int line = 1;
    private int column = 0;

    private static final Map<String, TokenType> keywords = new HashMap<>();

    static {
        for (TokenType tt : TokenType.values()) {
            if (tt.name().chars().allMatch(Character::isLetter)) {
                keywords.put(tt.name(), tt);
            }
        }
    }

    public Lexer(Reader reader) throws IOException {
        if (reader instanceof BufferedReader) {
            this.reader = (BufferedReader) reader;
        } else {
            this.reader = new BufferedReader(reader);
        }
        this.reader.mark(1);
        advance();
    }

    private void advance() throws IOException {
        currentChar = reader.read();
        column++;
    }

    public Token nextToken() throws IOException {
        while (currentChar != -1 && Character.isWhitespace(currentChar)) {
            if (currentChar == '\n') {
                line++;
                column = 0;
            }
            advance();
        }
        if (currentChar == -1) {
            return new Token(TokenType.EOF, "", null, line, column);
        }

        // Single-line comments //
        if (currentChar == '/') {
            reader.mark(2);
            advance();
            if (currentChar == '/') {
                while (currentChar != -1 && currentChar != '\n') advance();
                return nextToken();
            } else {
                reader.reset();
                column--;
                currentChar = '/';
            }
        }

        // Brace comments { ... }
        if (currentChar == '{') {
            advance();
            while (currentChar != -1 && currentChar != '}') {
                if (currentChar == '\n') {
                    line++;
                    column = 0;
                }
                advance();
            }
            if (currentChar == '}') advance();
            return nextToken();
        }

        // Delphi comments (* ... *)
        if (currentChar == '(') {
            reader.mark(2);
            advance();
            if (currentChar == '*') {
                // skip comment
                advance();
                while (currentChar != -1) {
                    if (currentChar == '*') {
                        advance();
                        if (currentChar == ')') {
                            advance();
                            break;
                        }
                    } else {
                        if (currentChar == '\n') {
                            line++;
                            column = 0;
                        }
                        advance();
                    }
                }
                return nextToken();
            } else {
                reader.reset();
                column--;
                currentChar = '(';
            }
        }

        int startCol = column;
        // Identifiers and keywords
        if (Character.isLetter(currentChar) || currentChar == '_') {
            StringBuilder sb = new StringBuilder();
            while (currentChar != -1 && (Character.isLetterOrDigit(currentChar) || currentChar == '_')) {
                sb.append((char) currentChar);
                advance();
            }
            String text = sb.toString().toUpperCase();
            TokenType type = keywords.getOrDefault(text, TokenType.IDENT);
            return new Token(type, text, text, line, startCol);
        }

        // Numbers
        if (Character.isDigit(currentChar)) {
            StringBuilder sb = new StringBuilder();
            boolean isReal = false;
            while (currentChar != -1 && Character.isDigit(currentChar)) {
                sb.append((char) currentChar);
                advance();
            }
            if (currentChar == '.') {
                advance();
                if (currentChar == '.') {
                    // range operator
                    return new Token(TokenType.INTEGER_LITERAL, sb.toString(), Integer.parseInt(sb.toString()), line, startCol);
                }
                isReal = true;
                sb.append('.');
                while (currentChar != -1 && Character.isDigit(currentChar)) {
                    sb.append((char) currentChar);
                    advance();
                }
            }
            if (isReal) {
                return new Token(TokenType.REAL_LITERAL, sb.toString(), Double.parseDouble(sb.toString()), line, startCol);
            } else {
                return new Token(TokenType.INTEGER_LITERAL, sb.toString(), Integer.parseInt(sb.toString()), line, startCol);
            }
        }

        // String literals
        if (currentChar == '\'') {
            StringBuilder sb = new StringBuilder();
            advance();
            while (currentChar != -1 && currentChar != '\'') {
                sb.append((char) currentChar);
                advance();
            }
            if (currentChar != '\'') {
                ErrorHandler.error(line, column, "Unterminated string literal");
                return new Token(TokenType.ERROR, sb.toString(), null, line, startCol);
            }
            advance();
            return new Token(TokenType.STRING_LITERAL, sb.toString(), sb.toString(), line, startCol);
        }

        // Operators and punctuation
        switch (currentChar) {
            case '+':
                advance();
                return new Token(TokenType.PLUS, "+", null, line, startCol);
            case '-':
                advance();
                return new Token(TokenType.MINUS, "-", null, line, startCol);
            case '*':
                advance();
                return new Token(TokenType.MUL, "*", null, line, startCol);
            case '/':
                advance();
                return new Token(TokenType.DIV_OP, "/", null, line, startCol);
            case '(':
                advance();
                return new Token(TokenType.LParen, "(", null, line, startCol);
            case ')':
                advance();
                return new Token(TokenType.RParen, ")", null, line, startCol);
            case '[':
                advance();
                return new Token(TokenType.LBracket, "[", null, line, startCol);
            case ']':
                advance();
                return new Token(TokenType.RBracket, "]", null, line, startCol);
            case ';':
                advance();
                return new Token(TokenType.SEMICOLON, ";", null, line, startCol);
            case ':':
                advance();
                if (currentChar == '=') {
                    advance();
                    return new Token(TokenType.ASSIGN, ":=", null, line, startCol);
                }
                return new Token(TokenType.COLON, ":", null, line, startCol);
            case '<':
                advance();
                if (currentChar == '=') {
                    advance();
                    return new Token(TokenType.LE, "<=", null, line, startCol);
                }
                if (currentChar == '>') {
                    advance();
                    return new Token(TokenType.NE, "<>", null, line, startCol);
                }
                return new Token(TokenType.LT, "<", null, line, startCol);
            case '>':
                advance();
                if (currentChar == '=') {
                    advance();
                    return new Token(TokenType.GE, ">=", null, line, startCol);
                }
                return new Token(TokenType.GT, ">", null, line, startCol);
            case '=':
                advance();
                return new Token(TokenType.EQ, "=", null, line, startCol);
            case ',':
                advance();
                return new Token(TokenType.COMMA, ",", null, line, startCol);
            case '.':
                advance();
                if (currentChar == '.') {
                    advance();
                    return new Token(TokenType.DOTDOT, "..", null, line, startCol);
                }
                return new Token(TokenType.DOT, ".", null, line, startCol);
            default:
                char bad = (char) currentChar;
                ErrorHandler.error(line, column, "Unexpected character '" + bad + "'");
                advance();
                return new Token(TokenType.ERROR, String.valueOf(bad), null, line, startCol);
        }
    }
}