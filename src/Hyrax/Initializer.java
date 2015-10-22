package Hyrax;

import peersim.cdsim.CDSimulator;
import peersim.config.Configuration;
import peersim.core.Control;
import peersim.core.Network;
import peersim.core.Node;
import utils.ReputationMatrix;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

/**
 * This class will read the configuration file and
 * initialize all node according to the parameters.
 * <p>
 * It runs only once at the beginning of the simulation
 */
public class Initializer implements Control {

    /******************************
     * PARAMETERS           *
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
    private static final String PAR_NODE1REPTMATRIX = "node1RepMatrix";
    private static final String PAR_DEVIATION = "deviation";

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
    private static int maxCycles;
    private static String node1RepMatrix;
    private static float deviation;

    /******************************
     * Constructor         *
     ******************************/

    public Initializer(String prefix) {
        attpid = Configuration.getPid(prefix + "." + PAR_PROT);
        kindness = Configuration.getString(prefix + "." + PAR_KIND, null);
        //defaults to null (everyone has random kindness)
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
        node1RepMatrix = Configuration.getString(prefix + "." +
                PAR_NODE1REPTMATRIX, null);
        deviation = Configuration.getInt(prefix + "." + PAR_DEVIATION, 20) /
                100.0f;
    }


    /*******************************
     * 'execute method'         *
     * (code that will be run at the *
     * beginning of the simulation) *
     *******************************/


    public boolean execute() {
        Random rand = new Random();

        Infrastructure.init(bufferSize, attpid,
                parseRepMatrix(node1RepMatrix), deviation);
        Observer.init(maxReports, maxCycles, attpid);

        ArrayList<Integer> nodeKindness = new ArrayList<Integer>();    /*
        Lists that will contain values to be attributed        */
        ArrayList<Boolean> nodeOverstaters = new ArrayList<Boolean>();    /*
         to the nodes. The lists will be shuffled and the      */
        ArrayList<Boolean> nodeRandomRaters = new ArrayList<Boolean>();    /*
          values will be assigned randomly to every node.       */
        ArrayList<Boolean> nodeVictims = new ArrayList<Boolean>();

        // first we'll define the kindness of the nodes according to the
        // parameters provided.
        if (kindness == null) {
            //no kindness parameter was provided which means all nodes have
            // random kindness.
            for (int i = 0; i < Network.size(); i++) {
                nodeKindness.add(rand.nextInt(101));
            }
        } else {
            nodeKindness = parseKindness(kindness, rand);
        }


        //now we'll define how many nodes are overstaters
        if (nEvils > 0) {
            while (nEvils > 0) {
                nodeOverstaters.add(true);
                nEvils--;
            }
            while (nodeOverstaters.size() < Network.size()) {
                nodeOverstaters.add(false);
            }
        }


        //next we'll define how many nodes are random raters
        if (nRandoms > 0) {
            while (nRandoms > 0) {
                nodeRandomRaters.add(true);
                nRandoms--;
            }
            while (nodeRandomRaters.size() < Network.size()) {
                nodeRandomRaters.add(false);
            }
        }


        //finally we'll define how many nodes are victims
        if (nVictims > 0) {
            while (nVictims > 0) {
                nodeVictims.add(true);
                nVictims--;
            }
            while (nodeVictims.size() < Network.size()) {
                nodeVictims.add(false);
            }
        }


        //shuffle all the lists
        Collections.shuffle(nodeKindness);
        Collections.shuffle(nodeOverstaters);
        Collections.shuffle(nodeRandomRaters);
        Collections.shuffle(nodeVictims);


        //assign the values to every node
        for (int i = 0; i < Network.size(); i++) {
            Node n = Network.get(i);
            NodeAttributes atribs = (NodeAttributes) n.getProtocol(attpid);
            //assign kindness
            atribs.setKindness(nodeKindness.get(i));
            //assign Evilness
            if (!nodeOverstaters.isEmpty()) {
                atribs.setOverstater(nodeOverstaters.get(i));
                if (atribs.isEvilOverstater()) {
                    atribs.setMaxOverstates(rand.nextInt(higherEvilValue) + 1);
                }
            }
            //assign randomRaters
            if (!nodeRandomRaters.isEmpty()) {
                atribs.setRandomRater(nodeRandomRaters.get(i));
                if (atribs.isRandomRater()) {
                    atribs.setRandomChance(rand.nextInt(101));
                }
            }
            //assign Victims
            if (!nodeVictims.isEmpty()) {
                atribs.setVictim(nodeVictims.get(i));
            }

        }

        //print of every node's attributes for debugging purposes
        for (int i = 0; i < Network.size(); i++) {
            Node n = Network.get(i);
            NodeAttributes atribs = (NodeAttributes) n.getProtocol(attpid);
            System.out.println("nodeID = " + n.getID());
            System.out.println("kindness = " + atribs.getKindness());
            System.out.println("isEvil = " + atribs.isEvilOverstater());
            System.out.println("maxEvilness = " + atribs.getMaxOverstates());
            System.out.println("isVictim = " + atribs.isVictim());
            System.out.println("isRandomRater = " + atribs.isRandomRater());
            System.out.println("randomChance = " + atribs.getRandomChance());
            System.out.println("Reputation =" + Infrastructure
                    .askForReputation(i));
            System.out.println
                    ("««««««««««««««««««««««««««««««««««««««««««««««««««");
        }


        return false;
    }


    /**********************************
     * 'helper function'         *
     * (for parsing the kindness string *
     * given as a parameter)      *
     **********************************/

    private ArrayList<Integer> parseKindness(String kindness, Random rand) {
        ArrayList<Integer> result = new ArrayList<Integer>();
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
        /* Ricardo's Code
           for (int i = 0; i < params.length; i++) {
            int nNodes = Integer.parseInt(params[i].split("=|%")[0]);
            int chance = Integer.parseInt(params[i].split("=|%")[1]);
            while (nNodes != 0) {
                result.add(chance);
                nNodes--;
            }
        }*/
        while (result.size() < Network.size()) {
            result.add(rand.nextInt(101));
        }

        return result;
    }

    private ReputationMatrix parseRepMatrix(String nodeRepMatrix) {
        if (nodeRepMatrix != null) {
            ReputationMatrix repMatrix = new ReputationMatrix();
            String[] nodesRep = nodeRepMatrix.split(";");
            for (int i = 0; i < nodesRep.length; i++) {
                String[] alphaBeta = nodesRep[i].split("-");
                int alpha = Integer.parseInt(alphaBeta[0]);
                int beta = Integer.parseInt(alphaBeta[1]);
                repMatrix.updateRatings(i, alpha, beta);
            }
            return repMatrix;
        }
        return null;
    }


}
