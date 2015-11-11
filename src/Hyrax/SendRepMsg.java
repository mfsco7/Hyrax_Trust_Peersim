package Hyrax;

import peersim.core.Node;

/**
 * Created by aferreira on 10-11-2015.
 */
public class SendRepMsg extends Message {

    int reputation;

    SendRepMsg(Node msgSender, int nodeReputation) {
        super(Type.SEND_REP, msgSender);
        reputation = nodeReputation;
    }

    public int getReputation() {
        return reputation;
    }
}
