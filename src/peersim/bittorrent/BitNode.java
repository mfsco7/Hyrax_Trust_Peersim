package peersim.bittorrent;

import peersim.config.Configuration;
import peersim.core.GeneralNode;
import peersim.util.IncrementalFreq;
import utils.Interaction;

import java.util.*;

/**
 * This class belongs to the package ${PACKAGE_NAME} and is for being use on
 * Hyrax Trust Peersim.
 */
public class BitNode extends GeneralNode {

    private static final String PAR_PROT = "protocol";
    private final int pid;

    private ArrayList<Interaction> interactions;
    private HashMap<Long, HashMap<Long, Integer>> nodeInteractions;

//    /**
//     * Used to construct the prototype node. This class currently does not
//     * have specific configuration parameters and so the parameter
//     * <code>prefix</code> is not used. It reads the protocol components
//     * (components that have type {@value Node#PAR_PROT}) from
//     * the configuration.
//     *
//     * @param prefix
//     */
    public BitNode(String prefix) {
        super(prefix);
        pid = Configuration.getPid(prefix + "." + PAR_PROT);
        interactions = new ArrayList<>();
    }

    private static HashMap<Long, Integer> sortByValues(HashMap<Long, Integer>
                                                               map) {
        List list = new LinkedList(map.entrySet());
        // Defined Custom Comparator here
        Collections.sort(list, new Comparator() {
            public int compare(Object o1, Object o2) {
                return ((Comparable) ((Map.Entry) (o1)).getValue()).compareTo
                        (((Map.Entry) (o2)).getValue());
            }
        });

        // Here I am copying the sorted list in HashMap
        // using LinkedHashMap to preserve the insertion order
        HashMap<Long, Integer> sortedHashMap = new LinkedHashMap<>();
        for (Iterator it = list.iterator(); it.hasNext(); ) {
            Map.Entry entry = (Map.Entry) it.next();
            sortedHashMap.put((Long) entry.getKey(), (Integer) entry.getValue
                    ());
        }
        return sortedHashMap;
    }

    public boolean addInteraction(long time, long nodeID, Integer result,
                                  Interaction.TYPE type) {
        Interaction interaction = new Interaction(time, nodeID, result, type);
        interactions.add(interaction);
        return true;
    }

    public boolean turnGoodInteraction(Long time, Long nodeID, Interaction
            .TYPE type) {
        Interaction interaction = getInteraction(time, nodeID, type);
        return interaction != null && interaction.setResult(1);
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

    public void printInteractions(Long nodeID, Interaction.TYPE type) {
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
                " is with node " + nodeID + " type " + type);

        freq.printAll(System.out);
    }

    public Interaction getInteraction(long time, long nodeID, Interaction
            .TYPE type) {
        for (Interaction interaction : interactions) {
            if (interaction.getNodeID() == nodeID && interaction.getType() ==
                    type) {

                if ( interaction.getTime() == time) {
                    return interaction;
                }
            }
        }
        return null;
    }

    public IncrementalFreq getInteractions(Long nodeID, Interaction.TYPE type) {
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
                freq.add(interaction.getResult());
            }
        }

        //        if (freq.getN() > 0) System.out.println("HyraxNode " +
        // getID() + ": " +
        //                interactions.size() + " interactions which " + freq
        // .getN() +
        //                " is with node " + nodeID);

        //        freq.printAll(System.out);

        return freq;
    }

    public int getNumberInteractions(long nodeID, Interaction.TYPE type) {
        int count = 0;
        for (Interaction interaction : interactions) {
            if (interaction.getNodeID() == nodeID && interaction.getType() ==
                    type) {
                count++;
            }
        }
        return count;
    }

    public int getNumberInteractions(long nodeID, Interaction.TYPE type, int
            result) {
        int count = 0;
        for (Interaction interaction : interactions) {
            if (interaction.getNodeID() == nodeID && interaction.getType() ==
                    type && interaction.getResult() == result) {
                count++;
            }
        }
        return count;
    }

    public HashMap<Long, Integer> getSortedInteractions(Interaction.TYPE type) {

        HashMap<Long, Integer> sortedInteractions = new HashMap<>();

        for (Neighbor neighbor : ((BitTorrent) (getProtocol(pid))).getCache()) {
            if (neighbor != null && neighbor.node != null) {
                sortedInteractions.put(neighbor.node.getID(),
                        getNumberInteractions(neighbor.node.getID(), type, 1));
            }
        }
        return sortByValues(sortedInteractions);
    }

    @Override
    public Object clone() {
        BitNode result;
        result = (BitNode) super.clone();
        result.interactions = new ArrayList<>();

        return result;
    }

    /**
     * Add the interactions receive by {@code nodeID}
     * @param nodeID
     * @param nodeInteractions
     */
    public void addNodeInteractions(long nodeID, HashMap<Long, Integer>
            nodeInteractions) {
        this.nodeInteractions.put(nodeID, nodeInteractions);
    }
}
