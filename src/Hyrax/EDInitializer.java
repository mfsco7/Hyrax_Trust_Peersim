package Hyrax;

import peersim.config.Configuration;
import peersim.core.*;
import peersim.edsim.EDSimulator;
import utils.Interaction;

/**
 * Created by aferreira on 09-11-2015.
 */
public class EDInitializer implements Control {

    private static final String PAR_PROTOCOL = "protocol";
    private static final String PAR_NEIGHBOUR = "neighbour";
    private static final String PAR_SPAWN_TIME = "spawn";

    private final int pid;
    private final int nid;
    private final int spawn;

    public EDInitializer(String prefix) {
        pid = Configuration.getPid(prefix + "." + PAR_PROTOCOL);
        nid = Configuration.getPid(prefix + "." + PAR_NEIGHBOUR);
        spawn = Configuration.getInt(prefix + "." + PAR_SPAWN_TIME);
    }

    @Override
    public boolean execute() {
        //        InfrastructureNode
        for (int i = 0; i < Network.size(); i++) {
            HyraxNode node = (HyraxNode) Network.get(i);
            IdleProtocol ip = (IdleProtocol) node.getProtocol(nid);
            Message msg = new GetFileMsg(node, "deprecated-list.html");
            for (int j = 0; j < ip.degree(); j++) {
                Node neighbor = ip.getNeighbor(j);
                for (int time = 1000; time < CommonState.getEndTime(); time
                        += spawn) {
                    EDSimulator.add(time, msg, neighbor, pid);
                    node.addInteraction(time, neighbor.getID(), 0,
                            Interaction.TYPE.DOWNLOAD);
                }
            }
        }
        return false;
    }
}
