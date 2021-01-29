package org.calypsonet.certification.readerlayer.procedures;

import java.util.Scanner;

/**
 * Helper class to manage console inputs and outputs.
 */
public class Console {
    private static Scanner scanner = new Scanner(System.in);
    // constants for results color output
    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_RED = "\u001B[31m";
    private static final String ANSI_GREEN = "\u001B[32m";
    private static final String ANSI_YELLOW = "\u001B[33m";
    private static final String ANSI_BLUE = "\u001B[34m";
    private static final String ANSI_PURPLE = "\u001B[35m";

    private static String getTestName() {
        String className = Thread.currentThread().getStackTrace()[3].getClassName();
        int pos = className.lastIndexOf ('.') + 1;
        return className.substring(pos);
    }

    /**
     * Displays the test name
     */
    public static void displayTestName() {
        System.out.println(ANSI_BLUE + "Test " + ANSI_PURPLE + getTestName() + ANSI_RESET);
    }

    /**
     * Displays a message
     * @param message string containing the message to display
     */
    public static void display(String message) {
        System.out.println(ANSI_BLUE + message + ANSI_RESET);
    }

    /**
     * Waits the user to press the enter key
     * @param message string containing the message to display
     */
    public static void waitEnter(String message) {
        System.out.println(ANSI_YELLOW + message + ANSI_RESET);
        scanner.nextLine();
    }

    /**
     * Displays a message in the console indicating that the test was successful.
     */
    public static void notifySuccess() {
        System.out.println(ANSI_GREEN + getTestName() + " succeeded." + ANSI_RESET);
    }

    /**
     * Displays a message in the console indicating that the test has failed.
     * <br>The origin of the error is indicated.
     * @param message string of characters describing the origin of the error
     */
    public static void notifyFailure(String message) {
        System.out.println(ANSI_RED + getTestName() + " failed due to the following reason:\n" + message + ANSI_RESET);
    }
}
