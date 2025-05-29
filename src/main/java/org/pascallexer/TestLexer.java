package org.pascallexer;

import java.util.List;

import java.nio.file.*;
import java.io.IOException;

public class TestLexer {
    public static void main(String[] args) {
        if (args.length == 0) {
            System.err.println("Usage: java TestLexer <source-file>");
            System.exit(1);
        }
        try {
            String content = Files.readString(Path.of(args[0]));
            Lexer lexer = new Lexer(content);
            List<Token> tokens = lexer.tokenize();
            tokens.forEach(System.out::println);

            System.out.println("\n--- Symbol Table ---");
            lexer.getSymbolTable().forEach(System.out::println);
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        }
    }
}