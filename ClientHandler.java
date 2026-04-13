import java.io.*;
import java.net.*;
import java.time.*;
 

public class ClientHandler implements Runnable {
 
    private final Socket socket;
    private final ServerLogger logger;
 
    private String clientName = "UNKNOWN";
    private Instant connectedAt;
 
    public ClientHandler(Socket socket, ServerLogger logger) {
        this.socket = socket;
        this.logger = logger;
    }
 
    @Override
    public void run() {
        try (
            BufferedReader in  = new BufferedReader(
                new InputStreamReader(socket.getInputStream()));
            PrintWriter    out = new PrintWriter(
                new OutputStreamWriter(socket.getOutputStream()), true)
                
        ) {
            String firstMessage = in.readLine();
 
            if (firstMessage == null || !firstMessage.startsWith("JOIN|")) {
                logger.logError("???", "Expected JOIN, got: " + firstMessage);
                return;
            }
            clientName  = firstMessage.substring(5).trim();
            connectedAt = Instant.now();
            String clientIP = socket.getInetAddress().getHostAddress();
            logger.logJoin(clientName, clientIP);
            out.println("ACK|" + clientName);

            String message;
            while ((message = in.readLine()) != null) {
 
                if (message.equalsIgnoreCase("QUIT")) {
                    out.println("BYE|" + clientName);
                    logger.logDisconnect(clientName, connectedAt);
                    break;
                }
 
                if (message.startsWith("CALC|")) {
                    handleCalc(message, out);
                } else {
                    logger.logError(clientName, "Unknown message: " + message);
                    out.println("ERROR|UNKNOWN|Unrecognised message format");
                }
            }
        }

        catch (SocketException e) {
            logger.logError(clientName, "Client disconnected abruptly");
            if (connectedAt != null) {
                logger.logDisconnect(clientName, connectedAt);
            }
        }
        catch (IOException e) {
            logger.logError(clientName, "IO error: " + e.getMessage());
        }
        finally {
            try {
                socket.close();
            } catch (IOException ignored){}
        }
    }

    private void handleCalc(String message, PrintWriter out) {
        String expression = message.substring(5).trim();
        logger.logRequest(clientName, expression);
        try {
            double result = MathEvaluator.evaluate(expression);
            String resultStr = (result == Math.floor(result) && !Double.isInfinite(result)) ? String.valueOf((long) result) : String.format("%.2f", result);
            out.println("RESULT|" + expression + "|" + resultStr);
            logger.logResponse(clientName, expression, resultStr);
        } catch (IllegalArgumentException e) {
            String reason = e.getMessage();
            logger.logError(clientName, "Bad expression [" + expression + "]: " + reason);
            out.println("ERROR|" + expression + "|" + reason);
        }
    }
}
