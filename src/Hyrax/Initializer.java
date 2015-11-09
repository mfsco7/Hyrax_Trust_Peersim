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
 * This class will read the configuration file and initialize all node
 * according to the parameters.
 * It runs only once at the beginning of the simulation
 */
public class Initializer implements Control {

    /******************************
     * PARAMETERS
     * (for reading the config file)*
     ******************************/
    private static final String PAR_PROT = "protocol";
    private static final String PAR_KIND = "kindness";
    private static final String PAR_OVERSTATERS = "numberOfOverstaters";
    private static final String PAR_MAX_OVERSTATES = "maxOverstates";
    private static final String PAR_VICTIM = "victims";
    private static final String PAR_RANDOMS = "randomRaters";
    private static final String PAR_REPORTS = "maxReports";
    private static final String PAR_BUFFERSIZE = "infrastructureBufferSize";
    private static final String PAR_INIT_REP_DB = "initRepDB";
    private static final String PAR_INIT_REP_MAT = "initRepMatrices";
    private static final String PAR_DEVIATION = "deviation";
    private static final String PAR_THRESHOLD = "threshold";

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

    /******************************
     * Constructor         *
     ******************************/

    public Initializer(String prefix) {
        attpid = Configuration.getPid(prefix + "." + PAR_PROT);
        kindness = Configuration.getString(prefix + "." + PAR_KIND, null);
        //defaults to null
        nEvils = Configuration.getInt(prefix + "." + PAR_OVERSTATERS, 0);
        //defaults to 0 (no one is overstating)
        higherEvilValue = Configuration.getInt(prefix + "." +
                PAR_MAX_OVERSTATES, 5); //defaults to 5
        nVictims = Configuration.getInt(prefix + "." + PAR_VICTIM, 0);
        //defaults to 0 (no victims for overstater nodes)
        nRandoms = Configuration.getInt(prefix + "." + PAR_RANDOMS, 0);
        //defaults to 0
        bufferSize = Configuration.getInt(prefix + "." + PAR_BUFFERSIZE,
                Network.size()); //defaults to the size of the network
        maxCycles = Configuration.getInt(CDSimulator.PAR_CYCLES);
        maxReports = Configuration.getInt(prefix + "." + PAR_REPORTS, 4);
        //the number of times that the nodes report their matrixes to the
        // infrastructure per simulation. Defaults to 4.
        initRepDB = Configuration.getString(prefix + "." +
                PAR_INIT_REP_DB, "");
        initRepMatrices = Configuration.getString(prefix + "." +
                PAR_INIT_REP_MAT, "");
        deviation = Configuration.getInt(prefix + "." + PAR_DEVIATION, 20) /
                100.0f;
        threshold = Configuration.getInt(prefix + "." + PAR_THRESHOLD, 50);
    }


    /*******************************
     * 'execute method'         *
     * (code that will be run at the *
     * beginning of the simulation) *
     *******************************/


    public boolean execute() {

        int reportingInterval = Math.max(maxCycles / maxReports, 1);
        Infrastructure.init(bufferSize, attpid,
                /*parseRepDB(initRepDB)*/ new ReputationDatabase(),
                deviation, reportingInterval);
        Observer.init(attpid);

        setNodeAttribs();

        //print of every node's attributes for debugging purposes
        System.out.println
                ("««««««««««««««««««««««««««««««««««««««««««««««««««");
        for (int i = 0; i < Network.size(); i++) {
            Node n = Network.get(i);
            NodeAttributes atribs = (NodeAttributes) n.getProtocol(attpid);
            System.out.println("nodeID = " + n.getID());
            System.out.println("nodeIndex = " + n.getIndex());
            System.out.println("kindness = " + atribs.getKindness());
            System.out.println("isEvil = " + atribs.isEvilOverstater());
            System.out.println("maxEvilness = " + atribs.getMaxOverstates());
            System.out.println("isVictim = " + atribs.isVictim());
            System.out.println("isRandomRater = " + atribs.isRandomRater());
            System.out.println("randomChance = " + atribs.getRandomChance());
//            System.out.println("Reputation =" + Infrastructure
//                    .askForAvgReputation(i));
            System.out.println(atribs.getRepMatrix().size());
            System.out.println
                    ("««««««««««««««««««««««««««««««««««««««««««««««««««");
        }

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
        HashMap<Integer, ReputationMatrix> nodeRepMatrices = parseRepMatrices
                (initRepMatrices);

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

        if (kindness != null) {
            nodeKindness = parseKindness(kindness);
        }

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
                atribs.setKindness(rand.nextInt(101 - threshold) +
                        threshold);
                nVictims--;
            } else {
                atribs.setKindness(rand.nextInt(101 - threshold) + threshold);
            }
            if (nodeRepMatrices.containsKey(i)) {
                atribs.setRepMatrix(nodeRepMatrices.get(i));
            }

        }
    }

    /**
     * Helper function for parsing the kindness string given as a parameter
     *
     * @param kindness The string to be parsed
     * @return Kindness values to be attribute to nodes
     */
    private ArrayList<Integer> parseKindness(String kindness) {
        ArrayList<Integer> result = new ArrayList<>();
        kindness = kindness.replaceAll("\\s", ""); // remove whitespaces
        String[] params = kindness.split(",");
        int nNodes;
        int chance;
        for (String param : params) {
            nNodes = Integer.parseInt(param.split("=|%")[0]);
            chance = Integer.parseInt(param.split("=|%")[1]);
            while (nNodes != 0) {
                result.add(chance);
                nNodes--;
            }
        }
        return result;
    }

    //    /**
    //     * Parses a string by the form of "node1:alpha-beta;..."
    //     * and creates a ReputationDatabase accordingly
    //     * @param initRepDB String to parse containing the ReputationDatabase
    //     * @return A ReputationDatabase
    //     */
    //    private ReputationDatabase parseRepDB(String initRepDB) {
    //        ReputationDatabase repDB = new ReputationDatabase();
    //        if (initRepDB != null && !initRepDB.equals("")) {
    //            String[] nodesRep = initRepDB.split(";");
    //            for (String nodeRep : nodesRep) {
    //                String[] nodeRep2 = nodeRep.split(":");
    //                int node = Integer.parseInt(nodeRep2[0]);
    //
    //                String[] alphaBeta = nodeRep2[1].split("-");
    //                int alpha = Integer.parseInt(alphaBeta[0]);
    //                int beta = Integer.parseInt(alphaBeta[1]);
    //
    //                repDB.addRatings(node, alpha, beta);
    //            }
    //        }
    //        return repDB;
    //    }

    /**
     * Parses a string by the form of node1:{node2:alpha-beta;...},... and
     * creates a HashMap. Each pair of the HashMap is a integer representing
     * the node and a ReputationMatrix of that node.
     * For example, if the string is "3:{1:3-2}", that will result in node 3
     * having a ReputationMatrix with 3 positive interactions with node 1 and
     * 2 negative interactions
     *
     * @param initRepMatrices String to be parsed
     * @return A HashMap containing a ReputationMatrix associated with
     * respective node
     */
    private HashMap<Integer, ReputationMatrix> parseRepMatrices(String initRepMatrices) {
        HashMap<Integer, ReputationMatrix> repMatrices = new HashMap<>();
        if (initRepMatrices != null && !initRepMatrices.equals("")) {
            String[] nodesMatrices = initRepMatrices.split(",");
            for (String nodeMatrix : nodesMatrices) {
                String[] nodeRepMatrix = nodeMatrix.split(":\\{|\\}");

                int node = Integer.parseInt(nodeRepMatrix[0]);
                String[] nodesRep = nodeRepMatrix[1].split(";");
                ReputationMatrix repMatrix = new ReputationMatrix();

                for (String nodeRep : nodesRep) {
                    String[] nodeRep2 = nodeRep.split(":");
                    int node2 = Integer.parseInt(nodeRep2[0]);

                    String[] alphaBeta = nodeRep2[1].split("-");
                    int alpha = Integer.parseInt(alphaBeta[0]);
                    int beta = Integer.parseInt(alphaBeta[1]);

                    repMatrix.updateRatings(node2, alpha, beta);
                }
                repMatrices.put(node, repMatrix);
            }
        }
        return repMatrices;
    }
}
