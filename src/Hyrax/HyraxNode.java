package Hyrax;

import peersim.core.GeneralNode;
import peersim.core.Node;
import peersim.util.IncrementalFreq;
import utils.Interaction;

import java.util.*;

/**
 * Created by aferreira on 11-11-2015.
 */
public class HyraxNode extends GeneralNode {

    private ArrayList<Interaction> interactions;

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
                                  Interaction.TYPE type) {
        Interaction interaction = new Interaction(time, nodeID, result, type);
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

    public Interaction getInteraction(Long time, Long nodeID, Interaction
            .TYPE type) {
        for (Interaction interaction : interactions) {
            //TODO remove casts
            if (interaction.getNodeID() == nodeID && interaction.getType() ==
                    type) {

                if ((long) interaction.getTime() == time) {
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
                freq.add((Integer) interaction.getResult());
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

    public HashMap<Long, Long> getSortedInteractions(Interaction
    .TYPE type) {

        HashMap<Long, Long> sortedInteractions = new HashMap<>();
//
//        HyraxNode node = ((HyraxNode) (Network.get(i)));
//        for (Neighbor neighbor : ((BitTorrent) (Network.get(i)
//                .getProtocol(pid))).getCache()) {
//            if (neighbor != null && neighbor.node != null) {
//                node.printInteractions(neighbor.node.getID(),
//                        Interaction.TYPE.DOWNLOAD);
//                //                        neighbor.node
//                // .printInteractions();
//                //                        System.out.print(neighbor
//                // .node.getID() + " ");
//                node.printInteractions(neighbor.node.getID(),
//                        Interaction.TYPE.UPLOAD);
//            }
//        }

        return sortedInteractions;
    }

    public boolean turnGoodInteraction(Long time, Long nodeID, Interaction
            .TYPE type) {
        Interaction interaction = getInteraction(time, nodeID, type);
        return interaction != null && interaction.setResult(1);
    }


    private static HashMap sortByValues(HashMap map) {
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
        HashMap sortedHashMap = new LinkedHashMap();
        for (Iterator it = list.iterator(); it.hasNext(); ) {
            Map.Entry entry = (Map.Entry) it.next();
            sortedHashMap.put(entry.getKey(), entry.getValue());
        }
        return sortedHashMap;
    }
}
