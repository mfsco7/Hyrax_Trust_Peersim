package Hyrax;

import peersim.cdsim.CDProtocol;
import peersim.config.Configuration;
import peersim.core.Node;

/**
 * Created by aferreira on 22-10-2015.
 * Just a protocol to send initial reputations from nodes to infrastructure
 */
public class EmptySimulation implements CDProtocol{

    public static int atributesID;

    public EmptySimulation(String prefix) {
        super();
        atributesID = Configuration.getPid(prefix + "." + "atributes");
    }

    @Override
    public void nextCycle(Node node, int i) {
        NodeAttributes nAtrib = (NodeAttributes) node.getProtocol(atributesID);
        Infrastructure.sendMatrix((int) node.getID(), nAtrib.getRepMatrix());
        nAtrib.getRepMatrix().clear();
    }

    @Override
    public Object clone() {
        EmptySimulation theClone = null;
        try {
            theClone = (EmptySimulation) super.clone();
        } catch (CloneNotSupportedException e) { //never happens
            e.printStackTrace();
        }
        return theClone;
    }
}
