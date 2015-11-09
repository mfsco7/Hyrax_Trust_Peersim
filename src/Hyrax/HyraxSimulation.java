package Hyrax;

import peersim.cdsim.CDProtocol;
import peersim.config.Configuration;
import peersim.config.FastConfig;
import peersim.core.Linkable;
import peersim.core.Node;

import java.util.ArrayList;
import java.util.Random;

/* This component will run once for each node in each cycle. 
 * 
 * For each node, we'll grab his 'atributes protocol' and check whether the
 * node is malicious or not.
 * 
 * If the node is deemed malicious, he'll fill his reputationMatrix with fake
  * ratings and sometimes overflow them.
 * 
 * If the node is not malicious, then we'll simply grab a random neighbor and
  * simulate an interaction.
 * The result of the interaction (success or fail) will be random according
 * to the neighbor 'kindness' value.
 *     For example, if node1 interacts with node2, and node2 has a kindness
 *     of 40%, then there's a 40% chance
 *     of node1 adding an 'alpha' point (success) and a 60% chance of adding
 *     a 'beta' point (fail) to his reputation matrix.
 *
 * 
 */
public class HyraxSimulation implements CDProtocol {
    /*******************
     * FIELDS      *
     *******************/
    public static Random rand;
    public static int atributesID;

    /************************
     * Constructor      *
     ************************/
    public HyraxSimulation(String prefix) {
        super();
        atributesID = Configuration.getPid(prefix + "." + "atributes");

        rand = new Random();
    }


    /******************************
     * Next Cycle method      *
     ******************************/

	/*
     * this method will be called by the simulator once for every node in
     * every cycle.
	 */
    @Override
    public void nextCycle(Node n, int protocolID) {

        //first we'll grab the current node's attributes to check if he's a
        // malicious node.
        NodeAttributes nAtrib = (NodeAttributes) n.getProtocol(atributesID);

        //then we'll grab the list of current neighbors.
        int linkableID = FastConfig.getLinkable(protocolID);
        Linkable links = (Linkable) n.getProtocol(linkableID);


        //if the node is an overstater...then he'll overstate ratings for a
        // victim neighbor node.
        //if there are no victims as neighbors, then he'll simply choose a
        // random neighbor
        /******************************
         *      overstater behavior    *
         ******************************/
        if (nAtrib.isEvilOverstater()) {
            ArrayList<Integer> victimIDs = new ArrayList<>();
            for (int i = 0; i < links.degree(); i++) {
                //search for victims
                Node neighbour = links.getNeighbor(i);
                NodeAttributes neighborAtrb = (NodeAttributes) neighbour
                        .getProtocol(atributesID);
                if (neighborAtrb.isVictim()) {
                    victimIDs.add(i);
                }
            }
            Node victim;
            if (victimIDs.isEmpty()) {
                //if no victim nodes are found, then he'll just overstate a
                // random neighbor
                victim = links.getNeighbor(rand.nextInt(links.degree()));
            } else {
                int chosenVictimID = victimIDs.get(rand.nextInt(victimIDs
                        .size()));
                victim = links.getNeighbor(chosenVictimID);
            }

            //now that we have our target, its time to overstate like a boss
            NodeAttributes victimAtribs = (NodeAttributes) victim.getProtocol
                    (atributesID);
            int numberOfOverstates = rand.nextInt(nAtrib.getMaxOverstates())
                    + 1;
            while (numberOfOverstates > 0) {
                int result = rand.nextInt(101);
                if (result > victimAtribs.getKindness()) {
                    nAtrib.getRepMatrix().addRating(victim.getIndex(),
                            false);
                } else {
                    nAtrib.getRepMatrix().addRating(victim.getIndex(),
                            true);
                }
                numberOfOverstates--;
            }
        }

        /******************************
         *      randomRater behavior   *
         ******************************/
        if (nAtrib.isRandomRater()) {
            //grab a random neighbor, add one random rating according to this
            // node's own randomChance (lol this is so random)
            Node neighbor = links.getNeighbor(rand.nextInt(links.degree()));
            if (rand.nextInt(101) > nAtrib.getRandomChance()) {
                nAtrib.getRepMatrix().addRating(neighbor.getIndex(),
                        false);
            } else {
                nAtrib.getRepMatrix().addRating(neighbor.getIndex(),
                        true);
            }
        }

        /******************************
         *      normal behavior        *
         ******************************/
        //grab a random neighbor, add a rating based on the neighbor's
        // kindness value
        if (!nAtrib.isEvilOverstater() && !nAtrib.isRandomRater()) {
            Node neighbor = links.getNeighbor(rand.nextInt(links.degree()));
            NodeAttributes neighborAtribs = (NodeAttributes) neighbor
                    .getProtocol(atributesID);
            if (rand.nextInt(101) > neighborAtribs.getKindness()) {
                //interaction was a failure.

                nAtrib.getRepMatrix().addRating(neighbor.getIndex(), false);
            } else {
                //interaction was a success

                nAtrib.getRepMatrix().addRating(neighbor.getIndex(), true);
            }
        }

        /**********************************************
         * check if this node is supposed to report    *
         *     his matrix to the infrastructure        *
         **********************************************/
        if (Infrastructure.isTimeToReport()) {
            Infrastructure.sendMatrix(n.getIndex(), nAtrib.getRepMatrix());
            nAtrib.getRepMatrix().clear();
        }
    }


    public Object clone() {
        HyraxSimulation theClone;
        try {
            theClone = (HyraxSimulation) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new Error("Cloning HyraxSimulation was not successful");
        }
        return theClone;
    }
}
