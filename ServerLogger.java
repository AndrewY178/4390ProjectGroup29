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

    public synchronized void logJoin(String clientName, String address) {
        String msg = String.format("[CONNECT]    Name: %-15s | IP: %-15s | Time: %s",
            clientName, address, now());
        print(msg);
    }

    public synchronized void logRequest(String clientName, String expression) {
        String msg = String.format("[REQUEST]    Name: %-15s | Expression: %s",
            clientName, expression);
        print(msg);
    }

    public synchronized void logResponse(String clientName, String expression, String result) {
        String msg = String.format("[RESPONSE]   Name: %-15s | Expression: %-20s | Result: %s",
            clientName, expression, result);
        print(msg);
    }

    public synchronized void logDisconnect(String clientName, Instant connectedAt) {
        long seconds = Duration.between(connectedAt, Instant.now()).getSeconds();
        String msg = String.format("[DISCONNECT] Name: %-15s | Duration: %d sec | Time: %s",
            clientName, seconds, now());
        print(msg);
    }

    public synchronized void logInfo(String message) {
        String msg = String.format("[INFO]       %s | Time: %s", message, now());
        print(msg);
    }

    public synchronized void logError(String clientName, String error) {
        String msg = String.format("[ERROR]      Name: %-15s | %s | Time: %s",
            clientName, error, now());
        print(msg);
    }

    public synchronized void close() {
        if (fileWriter != null) fileWriter.close();
    }

    private String now() {
        return LocalDateTime.now().format(FORMATTER);
    }

    private void print(String msg) {
        System.out.println(msg);
        if (fileWriter != null) fileWriter.println(msg);
    }
}
