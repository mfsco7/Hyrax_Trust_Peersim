package Hyrax;

import peersim.core.Node;

/**
 * Created by aferreira on 09-11-2015.
 */
public class Message {

    enum Type {MATRIX, GET_REP, SEND_REP, GET_FILE, SEND_FILE}

    static long ID = 0;

    private long id;
    private Type type;
    private Node sender;

    Message(Type msgType, Node msgSender) {
        type = msgType;
        sender = msgSender;
        id = Message.ID++;
    }

    public Type getType() {
        return type;
    }

    public Node getSender() {
        return sender;
    }

    public long getID() {
        return id;
    }
}
