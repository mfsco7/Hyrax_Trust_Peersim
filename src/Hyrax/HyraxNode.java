package Hyrax;

import peersim.core.GeneralNode;
import peersim.core.Node;
import peersim.util.IncrementalFreq;

import java.util.ArrayList;

/**
 * Created by aferreira on 11-11-2015.
 */
public class HyraxNode extends GeneralNode {

    private ArrayList<Interaction<Long, Long, Integer, Integer>> interactions;


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

    public boolean addInteraction(long time, long nodeID, Integer result,
                                  Integer type) {
        Interaction<Long, Long, Integer, Integer> interaction = new
                Interaction<>(time, nodeID, result, type);
        interactions.add(interaction);
        return true;
    }

    public void printInteractions() {
        System.out.println("HyraxNode " + getID() + ": " + interactions.size() +
                " interactions");
        for (Interaction interaction : interactions) {
            System.out.println("HyraxNode" + getID() + ": (" + interaction
                    .getTime() + "," + interaction.getNodeID() + "," +
                    interaction.getResult() + "," + interaction.getType() +
                    ")");
        }
    }

    public void printInteractions(Long nodeID, Integer type) {
        IncrementalFreq freq = new IncrementalFreq();
        for (Interaction interaction : interactions) {
            if (interaction.getNodeID() == nodeID && interaction.getType() ==
                    type) {
                //                System.out.println("HyraxNode: (" +
                // interaction.getTime() +
                //                        "," +
                //                        interaction.getNodeID() + "," +
                // interaction.getResult
                //                        () +
                //                        "," + interaction.getType() + ")");
                freq.add((Integer) interaction.getResult());
            }
        }

        if (freq.getN() > 0) System.out.println("HyraxNode " + getID() + ": " +
                interactions.size() + " interactions which " + freq.getN() +
                " is with node " + nodeID);

        freq.printAll(System.out);
    }

    public Interaction<Long, Long, Integer, Integer> getInteraction(Long time, Long nodeID, Integer type) {
        for (Interaction interaction : interactions) {
            //TODO remove casts
            if ((long) interaction.getNodeID() == nodeID && (int) interaction.getType() == type) {

                if ((long) interaction.getTime() == time) {
                    return interaction;
                }
            }
        }
        return null;
    }

    public boolean turnGoodInteraction(Long time, Long nodeID, Integer type) {
        Interaction<Long, Long, Integer, Integer> interaction =
                getInteraction(time, nodeID, type);
        return interaction != null && interaction.setResult(1);
    }

    class Interaction<Time, NodeID, Result, Type> {

        private Time time;
        private NodeID nodeID;
        private Result result;
        private Type type;

        Interaction(Time time, NodeID nodeID, Result result, Type type) {
            this.time = time;
            this.nodeID = nodeID;
            this.result = result;
            this.type = type;
        }

        public Time getTime() {
            return time;
        }

        public NodeID getNodeID() {
            return nodeID;
        }

        public Result getResult() {
            return result;
        }

        public boolean setResult(Result result) {
            this.result = result;
            return true;
        }

        public Type getType() {
            return type;
        }

    }

}
