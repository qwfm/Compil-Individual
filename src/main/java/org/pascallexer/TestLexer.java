package org.pascallexer;

import java.util.List;

public class TestLexer {
    public static void main(String[] args) {
        String input =
                "VAR x, y: INTEGER;\n" +
                        "BEGIN\n" +
                        "  x := 10;\n" +
                        "  y := x + 20;\n" +
                        "  writeln('Result = ', y);\n" +
                        "END.";

        Lexer lexer = new Lexer(input);
        List<Token> tokens = lexer.tokenize();

        System.out.println("=== Tokens ===");
        for (Token token : tokens) {
            System.out.println(token);
        }
    }
}

