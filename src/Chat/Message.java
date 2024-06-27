package Chat;

import java.io.Serializable;

public class Message implements Serializable {
    final private MessageType type;
    final private String data;

    public Message(MessageType type) {
        this.type = type;
        this.data = null;
    }

    public Message(MessageType type, String data) {
        this.type = type;
        this.data = data;
    }

    public String getData() {
        return data;
    }

    public String getType() {
        return type.toString();
    }
}
