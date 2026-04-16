import java.io.*;
import java.time.*;
import java.time.format.*;

public class ServerLogger {
    private static final String LOG_FILE = "server.log";
    private static final DateTimeFormatter FORMATTER =
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        private final PrintWriter fileWriter;
 
    public ServerLogger() {
        PrintWriter tempWriter = null;
        try {
            tempWriter = new PrintWriter(new FileWriter(LOG_FILE, true), true);
        } catch (IOException e) {
            System.err.println("[LOGGER] Could not open log file: " + e.getMessage());
        }
        this.fileWriter = tempWriter;
    }

    // Logs client connection with name, IP, and timestamp
    public synchronized void logJoin(String clientName, String address, Instant connectedAt) {
        String msg = String.format(
            "[CONNECT]    Name: %-15s | Start: %s | IP: %s",
            clientName, format(connectedAt), address);
        print(msg);
    }

    // Logs client requests with name, expression, and timestamp
    public synchronized void logRequest(String clientName, String expression) {
        String msg = String.format("[REQUEST]    Name: %-15s | Expression: %s",
            clientName, expression);
        print(msg);
    }

    // Logs client responses with name, expression, result, and timestamp
    public synchronized void logResponse(String clientName, String expression, String result) {
        String msg = String.format("[RESPONSE]   Name: %-15s | Expression: %-20s | Result: %s",
            clientName, expression, result);
        print(msg);
    }

    // Logs client disconnection with name, connection duration, and timestamp
    public synchronized void logDisconnect(String clientName, Instant connectedAt) {
        Instant disconnectedAt = Instant.now();
        long seconds = Duration.between(connectedAt, disconnectedAt).getSeconds();
 
        String msg = String.format(
            "[DISCONNECT] Name: %-15s | Start: %s | End: %s | Duration: %d sec",
            clientName,format(connectedAt),format(disconnectedAt),seconds);
        print(msg);
    }

    // Logs general informational messages with timestamp
    public synchronized void logInfo(String message) {
        String msg = String.format("[INFO]       %s | Time: %s", message, now());
        print(msg);
    }

    // Logs errors with client name, error message, and timestamp
    public synchronized void logError(String clientName, String error) {
        String msg = String.format("[ERROR]      Name: %-15s | %s | Time: %s",
            clientName, error, now());
        print(msg);
    }

    // Closes the log file writer
    public synchronized void close() {
        if (fileWriter != null) fileWriter.close();
    }

    // Helper method to format Instant to a readable string
    private String format(Instant instant) {
        return LocalDateTime.ofInstant(instant, ZoneId.systemDefault()).format(FORMATTER);
    }

    // Helper method to get current time as a formatted string
    private String now() {
        return LocalDateTime.now().format(FORMATTER);
    }

    // Helper method to print messages to console and log file
    private void print(String msg) {
        System.out.println(msg);
        if (fileWriter != null) fileWriter.println(msg);
    }
}
