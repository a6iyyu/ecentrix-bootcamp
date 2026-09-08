package com.bootcamp.calculator;

import java.util.Scanner;

import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CalculatorApplication implements CommandLineRunner {
    private static final Logger log = LoggerFactory.getLogger(CalculatorApplication.class);

    /**
     * The main entry point of the application.
     *
     * @param args the command-line arguments passed to the application
     */
    static void main(String[] args) {
        SpringApplication.run(CalculatorApplication.class, args);
    }

    /**
     * Runs the core logic of the interactive command-line calculator.
     * Continuously prompts the user sequentially for first number, an operator,
     * and second number, then executes the calculation.
     *
     * @param args the command-line arguments injected by Spring Boot
     */
    @Override
    public void run(String @NonNull ... args) {
        try (Scanner input = new Scanner(System.in)) {
            boolean continueCalculation = true;

            while (continueCalculation) {
                int a = readNumber(input, "Enter first number : ");
                String operator = readOperator(input);
                int b = readNumber(input, "Enter second number : ");

                execute(a, operator, b);
                System.out.println();

                continueCalculation = chooseToContinue(input);
            }

            System.out.println("Thank you for using the calculator!");
        } catch (Exception e) {
            log.error("A system error occurred: ", e);
        }
    }

    /**
     * Prompts the user whether they want to continue using the calculator.
     *
     * @param scanner the {@link Scanner} instance used to read console input
     * @return true if user chooses 'y', false if 'n'
     */
    private boolean chooseToContinue(Scanner scanner) {
        while (true) {
            System.out.print("Do you want to continue? (y/n) : ");
            String response = scanner.nextLine().trim().toLowerCase();

            if (response.equals("y")) {
                return true;
            } else if (response.equals("n")) {
                return false;
            }

            log.warn("Invalid input. Please enter 'y' or 'n'.");
        }
    }

    /**
     * Reads and validates numeric input from the user to ensure it is a valid integer.
     * If the input is not a valid integer, logs a warning and prompts the user to try again.
     *
     * @param scanner the {@link Scanner} instance used to read console input
     * @param prompt  the instruction message displayed to the user
     * @return a valid integer value entered by the user
     */
    private int readNumber(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);

            try {
                String input = scanner.nextLine().trim();

                if (input.isEmpty()) {
                    log.warn("An empty value isn't accepted, please try again.");
                    continue;
                }

                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                log.warn("Input is not a number, please try again");
            }
        }
    }

    /**
     * Reads and validates operator input from the user.
     * Ensures the input matches one of the supported operators (+, -, x, :).
     *
     * @param scanner the {@link Scanner} instance used to read console input
     * @return a valid operator string (+, -, x, or :)
     */
    private String readOperator(Scanner scanner) {
        while (true) {
            System.out.print("Enter operator (+ - x :) : ");
            String operator = scanner.nextLine().trim();

            if (operator.equals("+") || operator.equals("-") || operator.equals("x") || operator.equals(":")) {
                return operator;
            }

            log.warn("Operator '{}' is unknown, please try again", operator);
        }
    }

    /**
     * Performs the calculated operation based on the provided operands and operator.
     * Displays the outcome to the console and logs the operation details.
     *
     * @param a        the first integer operand
     * @param operator the mathematical operator (+, -, x, :)
     * @param b        the second integer operand
     */
    private void execute(int a, String operator, int b) {
        int result;
        String operationName;

        switch (operator) {
            case "+":
                operationName = "Addition";
                result = a + b;
                break;
            case "-":
                operationName = "Subtraction";
                result = a - b;
                break;
            case "x":
                operationName = "Multiplication";
                result = a * b;
                break;
            case ":":
                operationName = "Division";

                if (b == 0) {
                    log.error("Division by zero is not allowed!");
                    return;
                }

                result = a / b;
                break;
            default:
                return;
        }

        System.out.println(operationName + " of " + a + " " + operator + " " + b + " = " + result);
        log.info("{} of {} {} {} = {}", operationName, a, operator, b, result);
    }
}