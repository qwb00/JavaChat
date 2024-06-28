package client;

import Chat.Connection;
import Chat.Message;
import Chat.MessageType;

import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    protected Connection connection;
    private volatile boolean clientConnected = false;
    public class SocketThread extends Thread {
        protected void processIncomingMessage(String message) {
            System.out.println(message);
        }

        protected void informAboutAddingNewUser(String userName) {
            System.out.println(userName + " has joined the chat.");
        }

        protected void informAboutDeletingNewUser(String userName) {
            System.out.println(userName + " has left the chat.");
        }

        protected void notifyConnectionStatusChanged(boolean clientConnected) {
            synchronized (Client.this) {
                Client.this.clientConnected = clientConnected;
                Client.this.notify();
            }
        }

        protected void clientHandshake() throws IOException, ClassNotFoundException {
            while (true) {
                Message message = connection.receive();
                if(message.getType().equals(MessageType.NAME_REQUEST.toString())) {
                    connection.send(new Message(MessageType.USER_NAME, getUserName()));
                } else if(message.getType().equals(MessageType.NAME_ACCEPTED.toString())) {
                    notifyConnectionStatusChanged(true);
                    return;
                } else {
                    throw new IOException("Unexpected MessageType");
                }
            }
        }

        protected void clientMainLoop() throws IOException, ClassNotFoundException {
            while (true) {
                Message message = connection.receive();
                if(message.getType().equals(MessageType.TEXT.toString())) {
                    processIncomingMessage(message.getData());
                } else if(message.getType().equals(MessageType.USER_ADDED.toString())) {
                    informAboutAddingNewUser(message.getData());
                } else if(message.getType().equals(MessageType.USER_REMOVED.toString())) {
                    informAboutDeletingNewUser(message.getData());
                } else {
                    throw new IOException("Unexpected MessageType");
                }
            }
        }

        public void run() {
            try {
                String address = getServerAddress();
                int port = getServerPort();
                connection = new Connection(new Socket(address, port));
                clientHandshake();
                clientMainLoop();
            } catch (IOException | ClassNotFoundException e) {
                notifyConnectionStatusChanged(false);
            }
        }
    }

    public void run() {
        SocketThread socketThread = getSocketThread();
        socketThread.setDaemon(true);
        socketThread.start();
        try {
            synchronized (this) {
                wait();
            }
        } catch (InterruptedException e) {
            System.out.println("An error occurred while waiting for the connection.");
            return;
        }
        if(clientConnected) {
            System.out.println("Connection established. To exit, enter 'exit'.");
        } else {
            System.out.println("An error occurred while working with the client.");
        }
        while(clientConnected) {
            String text = new Scanner(System.in).nextLine();
            if(text.equalsIgnoreCase("exit")) {
                break;
            }
            if(shouldSendTextFromConsole()) sendTextMessage(text);
        }
    }

    public static void main(String[] args) {
        Client client = new Client();
        client.run();
    }

    protected String getServerAddress() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter the server address:");
        return scanner.nextLine();
    }

    protected int getServerPort() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter the server port:");
        return scanner.nextInt();
    }

    protected String getUserName() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter your username:");
        return scanner.nextLine();
    }

    protected boolean shouldSendTextFromConsole() {
        return true;
    }

    protected SocketThread getSocketThread() {
        return new SocketThread();
    }

    protected void sendTextMessage(String text) {
        try {
            connection.send(new Message(MessageType.TEXT, text));
        } catch (Exception e) {
            clientConnected = false;
            System.out.println("An error occurred while sending a message.");
        }
    }
}
