package org.pascallexer;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

public class TestLexer {
    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Usage: java org.pascallexer.TestLexer <source-file>");
            System.exit(1);
        }
        String filename = args[0];
        try (Reader reader = new FileReader(filename)) {
            Lexer lexer = new Lexer(reader);
            Token token;
            while ((token = lexer.nextToken()).type != TokenType.EOF) {
                System.out.println(token);
            }
        } catch (IOException e) {
            System.err.println("IO Error: " + e.getMessage());
        }
    }
}