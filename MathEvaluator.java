public class MathEvaluator {

    // MathEvaluator parses and evaluates simple two operand math expressions
    // In the form of: <operand1> [operator] <operand2>
    public static double evaluate(String expression) {
        if (expression == null || expression.trim().isEmpty()) {
            throw new IllegalArgumentException("Empty expression");
        }

        //split on any run of whitespace
        String[] tokens = expression.trim().split("\\s+");

        // enforce strict format of exactly three tokens required
        // so rejects inputs like "3+5"
        if (tokens.length != 3) {
            throw new IllegalArgumentException(
                "Expected format: <operand1> <operator> <operand2> "
                + "(got " + tokens.length + " token(s))");
        }
        
        // parse two operands and grab the operator token
        double left  = parseOperand(tokens[0], "first");
        String op    = tokens[1];
        double right = parseOperand(tokens[2], "second");

        // dispatch to the correct arithmetic operation based on the operator
        switch (op) {
            case "+": return left + right;
            case "-": return left - right;
            case "*": return left * right;
            case "/":
                //guard against division by zero before perofrming the operation
                if (right == 0.0) {
                    throw new IllegalArgumentException("Division by zero");
                }
                return left / right;
            default:
                throw new IllegalArgumentException(
                    "Unsupported operator '" + op + "' (use +, -, *, or /)");
        }
    }

    // helper that parses a single token as a double giving a descriptive error if the token isnt a valid number
    private static double parseOperand(String token, String which) {
        try {
            return Double.parseDouble(token);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                "Invalid " + which + " operand '" + token + "' (not a number)");
        }
    }
}