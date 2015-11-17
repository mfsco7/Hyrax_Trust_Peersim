package Hyrax;

import peersim.config.Configuration;
import peersim.core.Control;
import peersim.core.Network;

/**
 * Created by aferreira on 12-11-2015.
 */
public class EDObserver implements Control {
    private static final String PAR_NEIGHBOUR = "neighbour";

    private final int nid;

    public EDObserver(String prefix) {
        nid = Configuration.getPid(prefix + "." + PAR_NEIGHBOUR);
    }

    @Override
    public boolean execute() {
        System.out.println("EDObs:");
        for (int i = 0; i < Network.size(); i++) {
            HyraxNode node = (HyraxNode) Network.get(i);
//            System.out.println(node.getProtocol(nid));
            node.printInteractions();
        }
        return false;
    }
}
