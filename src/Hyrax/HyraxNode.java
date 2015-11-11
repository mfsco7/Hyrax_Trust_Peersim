package Hyrax;

import peersim.core.GeneralNode;
import peersim.core.Node;

import java.util.ArrayList;

/**
 * Created by aferreira on 11-11-2015.
 */
public class HyraxNode extends GeneralNode {

    ArrayList<Interaction<Integer, Long, Integer, Integer>> interactions;


    /**
     * Used to construct the prototype node. This class currently does not
     * have specific configuration parameters and so the parameter
     * <code>prefix</code> is not used. It reads the protocol components
     * (components that have type {@value Node#PAR_PROT}) from
     * the configuration.
     *
     * @param prefix
     */
    public HyraxNode(String prefix) {
        super(prefix);
        interactions = new ArrayList<>();
    }

    public boolean addInteraction(Integer time, long nodeID, Integer result,
                                  Integer type) {
        Interaction<Integer, Long, Integer, Integer> interaction = new
                Interaction<>(time, nodeID, result, type);
        interactions.add(interaction);
        return true;
    }
}
