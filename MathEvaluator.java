public class MathEvaluator {


    public static double evaluate(String expression) {
        if (expression == null || expression.trim().isEmpty()) {
            throw new IllegalArgumentException("Empty expression");
        }

        //split on any run of whitespace
        String[] tokens = expression.trim().split("\\s+");

        if (tokens.length != 3) {
            throw new IllegalArgumentException(
                "Expected format: <operand1> <operator> <operand2> "
                + "(got " + tokens.length + " token(s))");
        }

        double left  = parseOperand(tokens[0], "first");
        String op    = tokens[1];
        double right = parseOperand(tokens[2], "second");

        switch (op) {
            case "+": return left + right;
            case "-": return left - right;
            case "*": return left * right;
            case "/":
                if (right == 0.0) {
                    throw new IllegalArgumentException("Division by zero");
                }
                return left / right;
            default:
                throw new IllegalArgumentException(
                    "Unsupported operator '" + op + "' (use +, -, *, or /)");
        }
    }

    private static double parseOperand(String token, String which) {
        try {
            return Double.parseDouble(token);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                "Invalid " + which + " operand '" + token + "' (not a number)");
        }
    }
}