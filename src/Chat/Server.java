package Chat;

import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    public static class Handler extends Thread {
        private Socket socket;

        public Handler(Socket socket) {
            this.socket = socket;
        }
    }

    public static void main(String[] args) {
        ConsoleHelper.writeMessage("Enter the server port:");
        int port = ConsoleHelper.readInt();
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            ConsoleHelper.writeMessage("Server started.");
            while (true) {
                new Handler(serverSocket.accept()).start();
            }
        } catch (Exception e) {
            ConsoleHelper.writeMessage("An error occurred while starting the server.");
        }
    }
}
