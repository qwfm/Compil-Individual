package org.pascallexer;

public class ErrorHandler {
    public static void error(int line, int column, String message) {
        System.err.printf("[Error at %d:%d] %s\n", line, column, message);
    }
}
