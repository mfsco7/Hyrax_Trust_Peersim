package Hyrax;

import peersim.core.Node;
import peersim.edsim.EDProtocol;
import peersim.edsim.EDSimulator;

/**
 * Created by aferreira on 09-11-2015.
 */
public class EventSimulation implements EDProtocol {

    public EventSimulation(String prefix) {
        //        super();
    }

    @Override
    public void processEvent(Node node, int pid, Object event) {
        Message msg = (Message) event;
        Node node2 = msg.getSender();
        switch (msg.getType()) {
            case GET_FILE:
                String fileName = ((GetFileMsg) msg).getFileName();
                Message msg2 = new SendFileMsg(node, fileName);
                EDSimulator.add(1000, msg2, node2, pid);
                break;
            case SEND_FILE:
                break;
        }
        //        if (msg.getType() == Message.Type.GET_REP) {
        //            Node node2 = msg.getSender();
        //            System.out.println("message from " + node2.getID() + "
        // to " +
        //                    node.getID());
        //            ((Transport) node.getProtocol(FastConfig
        //                    .getTransport(pid))).
        //                    send(node, aem.sender, new AverageMessage
        // (value, null),
        //                            pid);
        //    }

    }

    @Override
    public EventSimulation clone() {
        EventSimulation theClone;
        try {
            theClone = (EventSimulation) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new Error("Cloning HyraxSimulation was not successful");
        }
        return theClone;
    }
}
