import java.io.*;
import java.net.*;
import java.time.*;
 

public class ClientHandler implements Runnable {
 
    // Variables to store client information and logger reference
    private final Socket socket;
    private final ServerLogger logger;
 
    private String clientName = "UNKNOWN";
    private Instant connectedAt;
    
    // Constructor to initialize client handler with socket and logger
    public ClientHandler(Socket socket, ServerLogger logger) {
        this.socket = socket;
        this.logger = logger;
    }
 
    // Main method to handle client communication
    @Override
    public void run() {
        // Use try-with-resources to ensure streams are closed properly
        try (
            // Set up input and output streams for communication with the client
            BufferedReader in  = new BufferedReader(
                new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(
                new OutputStreamWriter(socket.getOutputStream()), true)

        ) {
            // Read the initial message from the client, which should be a JOIN message
            String firstMessage = in.readLine();
 
            // If not a JOIN, log an error and return
            if (firstMessage == null || !firstMessage.startsWith("JOIN|")) {
                logger.logError("???", "Expected JOIN, got: " + firstMessage);
                return;
            }
            // Extract client name from the JOIN message and log the connection
            clientName  = firstMessage.substring(5).trim();
            connectedAt = Instant.now();
            String clientIP = socket.getInetAddress().getHostAddress();
            logger.logJoin(clientName, clientIP, connectedAt);
            out.println("ACK|" + clientName);

            String message;
            // Main loop to read messages from the client until disconnection
            while ((message = in.readLine()) != null) {
                
                // If the client sends an EXIT message, log the disconnection and break the loop
                if (message.equalsIgnoreCase("EXIT")) {
                    out.println("BYE|" + clientName);
                    logger.logDisconnect(clientName, connectedAt);
                    break;
                }

                // If the message starts with CALC, handle the calculation request
                if (message.startsWith("CALC|")) {
                    handleCalc(message, out);
                } else {
                    logger.logError(clientName, "Unknown message: " + message);
                    out.println("ERROR|UNKNOWN|Unrecognised message format");
                }
            }
        }
        // Handle socket exceptions (e.g., client disconnects abruptly) and log the error and disconnection if applicable
        catch (SocketException e) {
            logger.logError(clientName, "Client disconnected abruptly");
            if (connectedAt != null) {
                logger.logDisconnect(clientName, connectedAt);
            }
        }
        // Handle other IO exceptions and log the error
        catch (IOException e) {
            logger.logError(clientName, "IO error: " + e.getMessage());
        }
        // Ensure the socket is closed when done, even if an exception occurs
        finally {
            try {
                socket.close();
            } catch (IOException ignored){}
        }
    }

    // Handle CALC messages by sending it to MathEvaluator and returning the result or error to the client
    private void handleCalc(String message, PrintWriter out) {
        String expression = message.substring(5).trim();
        logger.logRequest(clientName, expression);
        try {
            double result = MathEvaluator.evaluate(expression);
            String resultStr = (result == Math.floor(result) && !Double.isInfinite(result)) ? String.valueOf((long) result) : String.format("%.2f", result);
            out.println("RESULT| " + expression + " = " + resultStr);
            logger.logResponse(clientName, expression, resultStr);
        } catch (IllegalArgumentException e) {
            String reason = e.getMessage();
            logger.logError(clientName, "Bad expression [" + expression + "]: " + reason);
            out.println("ERROR| " + expression + "|" + reason);
        }
    }
}
