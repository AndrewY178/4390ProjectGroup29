import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MathServer {
    private static final int THREAD_POOL_SIZE = 10;
    public static void main(String[] args) {
        if (args.length < 1) {
            System.err.println("Usage: java MathServer <port>");
            System.err.println("Example: java MathServer 6789");
            System.exit(1);
        }

        // Validate port number
        int port;
        try {
            port = Integer.parseInt(args[0]);
        }
        catch (NumberFormatException e) {
            System.err.println("Invalid port number: " + args[0]);
            System.exit(1);
            return;
        }

        //Create logger instance
        ServerLogger logger = new ServerLogger();

        // Create a thread pool to handle client connections
        ExecutorService pool = Executors.newFixedThreadPool(THREAD_POOL_SIZE);

        // Log server startup
        logger.logInfo("MathServer starting on port " + port);

        // Add shutdown hook to log server shutdown and close resources
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.logInfo("MathServer shutting down");
            pool.shutdown();
            logger.close();
        }));

        // Start server socket and listen for client connections
        try (ServerSocket welcomeSocket = new ServerSocket(port)) {
            logger.logInfo("Waiting for client connections...");
 
            while (true) {
                // Accept incoming client connection
                Socket connectionSocket = welcomeSocket.accept();
                pool.execute(new ClientHandler(connectionSocket, logger));
            }
        }
        catch (Exception e) {
            logger.logError("SERVER", "Fatal: " + e.getMessage());
        }
    }
}
