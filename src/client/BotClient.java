package client;

import client.Client.SocketThread;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class BotClient extends Client {
    public class BotSocketThread extends SocketThread {
        @Override
        protected void clientMainLoop() throws IOException, ClassNotFoundException {
            sendTextMessage("Hello, I'm a bot. I understand the following commands: date, day, month, year, time, hour, minutes, seconds.");
            super.clientMainLoop();
        }

        @Override
        protected void processIncomingMessage(String message) {
            System.out.println(message);
            if (message == null || !message.contains(": ")) return;
            String[] parts = message.split(": ");
            if (parts.length != 2) return;
            String userName = parts[0];
            String text = parts[1];
            String pattern = "Information for %s: %s";
            switch (text) {
                case "date":
                    sendTextMessage(String.format(pattern, userName, new SimpleDateFormat("d.MM.YYYY").format(new Date())));
                    break;
                case "day":
                    sendTextMessage(String.format(pattern, userName, new SimpleDateFormat("d").format(new Date())));
                    break;
                case "month":
                    sendTextMessage(String.format(pattern, userName, new SimpleDateFormat("MMMM").format(new Date())));
                    break;
                case "year":
                    sendTextMessage(String.format(pattern, userName, new SimpleDateFormat("YYYY").format(new Date())));
                    break;
                case "time":
                    sendTextMessage(String.format(pattern, userName, new SimpleDateFormat("H:mm:ss").format(new Date())));
                    break;
                case "hour":
                    sendTextMessage(String.format(pattern, userName, new SimpleDateFormat("H").format(new Date())));
                    break;
                case "minutes":
                    sendTextMessage(String.format(pattern, userName, new SimpleDateFormat("m").format(new Date())));
                    break;
                case "seconds":
                    sendTextMessage(String.format(pattern, userName, new SimpleDateFormat("s").format(new Date())));
                    break;
            }
        }
    }
    public static void main(String[] args) {
        new BotClient().run();
    }

    protected BotSocketThread getSocketThread() {
        return new BotSocketThread();
    }

    @Override
    protected String getUserName() {
        return "date_bot_" + (int) (Math.random() * 100);
    }

    @Override
    protected boolean shouldSendTextFromConsole() {
        return false;
    }
}
