package Hyrax;


import peersim.core.Node;
import peersim.core.Protocol;
import utils.ReputationDatabase;
import utils.ReputationMatrix;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by aferreira on 09-11-2015.
 */
public class InfrastructureNode implements Node {

    private static HashMap<Integer, ReputationMatrix> repBuffer; //matrixes
    // sent by nodes are placed in a reputation buffer
    private static int bufferSize; //when reputation buffer is 'full', the
    // infrastructure filters malicious ratings and adds the rest to the
    // reputationDatabase
    private static ReputationDatabase repDatabase; //all nodes's reputations
    // are stored here
    private static int atrbPid; //the protocolID to access nodes's attributes
    private static ArrayList<Integer> nodesToUpdate; //contains all nodeIDs
    // that received at least one rating from other nodes this round.
    private static float deviation;
    /**
     * Defines how frequently must the nodes report their matrix to the
     * infrastructure
     */
    private static int reportingInterval;

    private InfrastructureNode() {

    }

    public static void init(int bSize, int attPid, ReputationDatabase repDB,
                            float dev, int reportInterval) {
        //TODO pass this method to a initializer
        repBuffer = new HashMap<>();
        bufferSize = bSize;
        repDatabase = repDB;
        atrbPid = attPid;
        nodesToUpdate = new ArrayList<>();
        deviation = dev;
        reportingInterval = reportInterval;
    }

    //called by the observer to print the final reputations at the last cycle
    public static String askForReputation(int rater, int rated) {
        return repDatabase.getReputation(rater, rated);
    }

    public static String askForAvgReputation(int rated) {
        return repDatabase.getAvgReputation(rated);
    }

    //called by the observer to print the number of times ratings were
    // accepted and rejected
    public static int[] askForCredibility(int nodeID) {
        return repDatabase.getCredibility(nodeID);
    }

    @Override
    public Protocol getProtocol(int i) {
        return null;
    }

    @Override
    public int protocolSize() {
        return 0;
    }

    @Override
    public void setIndex(int i) {

    }

    @Override
    public int getIndex() {
        return 0;
    }

    @Override
    public long getID() {
        return 0;
    }

    @Override
    public int getFailState() {
        return 0;
    }

    @Override
    public void setFailState(int i) {

    }

    @Override
    public boolean isUp() {
        return false;
    }

    @Override
    public Object clone() {
        InfrastructureNode clone = null;

        try {
            clone = (InfrastructureNode) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }

        //TODO check if cloning can happen on this class, if yes clone fields

        return clone;
    }
}
