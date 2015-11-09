package Hyrax;

import peersim.cdsim.CDSimulator;
import peersim.config.Configuration;
import peersim.core.Control;
import peersim.core.Network;
import peersim.core.Node;
import utils.ReputationDatabase;
import utils.ReputationMatrix;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Random;


/**
 * Created by aferreira on 03-11-2015.
 */
public class RandomInitializer implements Control {

    private static final String PAR_PROT = "protocol";
    /******************************
     * FIELDS            *
     * (read from the config file) *
     ******************************/
    private static int attpid;
    private static String kindness;
    private static int nEvils;
    private static int higherEvilValue;
    private static int nVictims;
    private static int nRandoms;
    private static int maxReports;
    private static int bufferSize;
    /**
     * the total number of cycles for the simulation
     */
    private static int maxCycles;
    private static String initRepDB;
    private static String initRepMatrices;
    private static float deviation;
    private static int threshold;

    public RandomInitializer(String prefix) {
        Random rand = new Random();
        attpid = Configuration.getPid(prefix + "." + PAR_PROT);
        kindness = null;
        nEvils = rand.nextInt(Network.size());
        higherEvilValue = rand.nextInt(5);
        nVictims = rand.nextInt(Network.size() - nEvils);
        nRandoms = rand.nextInt(Network.size() - nEvils - nVictims);
        bufferSize = rand.nextInt(Network.size()); //defaults to the size of
        // the network
        maxCycles = Configuration.getInt(CDSimulator.PAR_CYCLES);
        maxReports = rand.nextInt(10);
        initRepDB = null;
        initRepMatrices = null;
        deviation = rand.nextFloat() * 100;
        threshold = rand.nextInt(100);
        System.out.println("RInitializer: " + threshold);
    }

    public boolean execute() {

        int reportingInterval = Math.max(maxCycles / maxReports, 1);
        Infrastructure.init(bufferSize, attpid,
                /*parseRepDB(initRepDB)*/ new ReputationDatabase(),
                deviation, reportingInterval);
        Observer.init(attpid);

        setNodeAttribs();

        return false;
    }


    /**
     * Sets the nodes attributes like kindness and ReputationMatrices. It also
     * sets if the node is evil, victim or normal and the respective
     * attributes associated.
     */
    private void setNodeAttribs() {
        //TODO review this way of node attributes setting

        Random rand = new Random();
        HashMap<Integer, ReputationMatrix> nodeRepMatrices = new HashMap<>();

        int nInitNodes = setInitNodes(rand, nodeRepMatrices);
        System.out.println("Infrastructure: " + nInitNodes + " nodes " +
                "kindness read from config file");

        setRemainingNodes(rand, nInitNodes, nodeRepMatrices);
    }

    /**
     * Uses the kindness values provided by config file to set kindness value
     * of nodes. If the kindness value is less than a threshold, then the
     * node will be overstater, if there are overstaters to be attributed. If
     * not the nodes will be randomRaters. If the kindness value is greater
     * than the threshold then the node will be victims, if there are any
     * victims to be assigned. If not the nodes will be normal.
     *
     * @param rand            A Instance of the Random class
     * @param nodeRepMatrices The init ReputationMatrices read from config
     *                        file already parsed
     * @return The number of kindness values in the config file
     */
    private int setInitNodes(Random rand, HashMap<Integer, ReputationMatrix>
            nodeRepMatrices) {
        ArrayList<Integer> nodeKindness = new ArrayList<>();

//        if (kindness != null) {
//            nodeKindness = parseKindness(kindness);
//        }

        Collections.shuffle(nodeKindness);

        for (int i = 0; i < nodeKindness.size(); i++) {
            Node n = Network.get(i);
            NodeAttributes atribs = (NodeAttributes) n.getProtocol(attpid);

            int nodeKind = nodeKindness.get(i);
            atribs.setKindness(nodeKind);

            if (nodeKind < threshold && nEvils > 0) {
                atribs.setOverstater(true);
                atribs.setMaxOverstates(rand.nextInt(higherEvilValue) + 1);
                nEvils--;
            }
            //assign randomRaters
            else if (nodeKind < threshold && nRandoms > 0) {
                atribs.setRandomRater(true);
                atribs.setRandomChance(rand.nextInt(101));
                nRandoms--;
            }
            //assign Victims
            else if (nVictims > 0) {
                atribs.setVictim(true);
                nVictims--;
            }
            if (nodeRepMatrices.containsKey(i)) {
                atribs.setRepMatrix(nodeRepMatrices.get(i));
            }
        }
        return nodeKindness.size();
    }

    /**
     * Just assign attributes for remaining nodes. First evil nodes, then
     * randomRaters, victims and last the normals.
     *
     * @param rand            A Instance of the Random class
     * @param size            The number of kindness values in the config file
     * @param nodeRepMatrices The init ReputationMatrices read from config
     *                        file already parsed
     */
    private void setRemainingNodes(Random rand, int size, HashMap<Integer,
            ReputationMatrix> nodeRepMatrices) {
        for (int i = size; i < Network.size(); i++) {
            Node n = Network.get(i);
            NodeAttributes atribs = (NodeAttributes) n.getProtocol(attpid);

            //assign Evilness
            if (nEvils > 0) {
                atribs.setOverstater(true);
                atribs.setKindness(rand.nextInt(threshold + 1));
                atribs.setMaxOverstates(rand.nextInt(higherEvilValue) + 1);
                nEvils--;
            }
            //assign randomRaters
            else if (nRandoms > 0) {
                atribs.setRandomRater(true);
                atribs.setKindness(rand.nextInt(threshold + 1));
                atribs.setRandomChance(rand.nextInt(101));
                nRandoms--;
            }
            //assign Victims
            else if (nVictims > 0) {
                atribs.setVictim(true);
                atribs.setKindness(rand.nextInt(101 - threshold) + threshold);
                nVictims--;
            } else {
                atribs.setKindness(rand.nextInt(101 - threshold) + threshold);
            }
            if (nodeRepMatrices.containsKey(i)) {
                atribs.setRepMatrix(nodeRepMatrices.get(i));
            }

        }
    }

}
