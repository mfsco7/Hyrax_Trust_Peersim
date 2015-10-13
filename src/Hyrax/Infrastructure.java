package Hyrax;

import org.apache.commons.math3.distribution.BetaDistribution;
import utils.ReputationDatabase;
import utils.ReputationMatrix;

import java.util.*;

public class Infrastructure {
    /*******************
     * FIELDS      *
     *******************/
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


    /*******************
     * init method   *
     *******************/
    public static void init(int bSize, int attPid, ReputationMatrix
            repMatrixNode1) {
        repBuffer = new HashMap<>();
        bufferSize = bSize;
        repDatabase = new ReputationDatabase();
        atrbPid = attPid;
        nodesToUpdate = new ArrayList<>();
        if (repMatrixNode1 != null) {
            sendMatrix(4, repMatrixNode1);
        }
    }


    /******************************************
     * this method will be called by node   *
     * once in a while during the simulation  *
     ******************************************/
    public static void sendMatrix(int nodeId, ReputationMatrix mat) {
        ReputationMatrix matClone = mat.clone();
        repBuffer.put(nodeId, matClone);

        for (int rated : matClone.getRatedNodes()) {
            if (!nodesToUpdate.contains(rated)) {
                nodesToUpdate.add(rated);
            }
        }
        if (bufferSize <= repBuffer.size()) { //when buffer is full... time
            // to update reputations in the infrastructure's database
            System.out.println("INFRASTRUCTURE: updating reputations");
            computeReputations();
            repBuffer.clear();
            nodesToUpdate.clear();
        }
    }

    //called by the observer to print the final reputations at the last cycle
    public static String askForReputation(int nodeID) {
        return repDatabase.getReputation(nodeID);
    }

    //called by the observer to print the number of times ratings were
    // accepted and rejected
    public static int[] askForCredibility(int nodeID) {
        return repDatabase.getCredibility(nodeID);
    }


    /*********************************
     * Update the database with     *
     * new values for the reputations *
     * after the buffer is full.    *
     *********************************/
    private static void computeReputations() {

        for (int rated : nodesToUpdate) {
            //the list of raters for this specific 'rated node' starts off as
            // containing all the nodes that submitted a matrix this round.
            ArrayList<Integer> raters = new ArrayList<>(repBuffer
                    .keySet());
            //we shall promptly remove the raters that did not rate this node.
            raters.remove((Integer) rated); //remove itself (if present on
            // the list)
            Iterator<Integer> it = raters.iterator();
            while (it.hasNext()) {
                int rater = it.next();
                if (!repBuffer.get(rater).containsID(rated)) {
                    it.remove();
                }
            }

            //now we will apply the filter algorithms to the rated node in
            // order to filter dubious raters.

            //overflowerFilter(rated, raters);
            unfairRatingFilter(rated, raters);

            //hopefully the raters that remained on the list are considered
            // 'fairRaters' and as such, we'll update the database with their
            // ratings
            for (int fairRater : raters) {
                int[] fairRaterAlphaBeta = repBuffer.get(fairRater)
                        .getAlphaBeta(rated);
                repDatabase.addCounter(fairRater, true); //this rater gave a
                // 'fairRating', so we update his credibility.
                repDatabase.addRatings(rated, fairRaterAlphaBeta[0],
                        fairRaterAlphaBeta[1]);
            }


        }
    }

    /**********************
     * Filter Algorithms  *
     **********************/

    /**
     * For every rated Node, this method attempts to filter raters that contain
     * very high alphas and betas when compared to other raters.
     *
     * @param rated  Node to be evaluated
     * @param raters List of nodes that
     */
    private static void overflowerFilter(int rated, ArrayList<Integer> raters) {
        boolean change = true; //we will reapply the algorithm until no rater
        // is excluded from the list
        while (change && raters.size() > 2) {
            change = false;

            HashMap<Integer, Integer> numberOfRatings = new HashMap<>();
            //contains the total number of ratings for
            // every rater (regarding a given rated node).
            //first Integer is the raterID and second integer is the total
            // number of ratings (interactions)
            for (int rater : raters) {
                int[] raterAlphaBeta = repBuffer.get(rater).getAlphaBeta(rated);
                int totalInteractions = raterAlphaBeta[0] + raterAlphaBeta[1];
                numberOfRatings.put(rater, totalInteractions);
            }

            //compute median
            double median;
            Integer[] values = numberOfRatings.values().toArray(
                    new Integer[numberOfRatings.values().size()]);
            Arrays.sort(values);
            int size = values.length;
            int middle = size / 2;
            if ((size % 2) == 1) {
                median = values[middle];
            } else {
                median = (values[middle - 1] + values[middle]) / 2.0;
            }

            //compute deviations
            ArrayList<Double> deviations = new ArrayList<>();
            for (int value : numberOfRatings.values()) {
                double dev = Math.abs(median - value);
                deviations.add(dev);
            }

            //compute MAD and threshold
            double mad;
            Collections.sort(deviations);
            if (size % 2 == 1) {
                mad = deviations.get(middle);
            } else {
                mad = (deviations.get(middle - 1) + deviations.get(middle)) /
                        2.0;
            }
            double threshold = median + 2 * mad;

            //remove raters whose values surpass the threshold
            Iterator<Integer> it = raters.iterator();
            while (it.hasNext()) {
                int rater = it.next();
                int value = numberOfRatings.get(rater);
                if (value > threshold) {
                    it.remove();
                    repDatabase.addCounter(rater, false);
                    change = true;
                }

            }
        }
    }

    /**
     * This filter acts on ratings that are deemed unfair. By unfair, we mean a
     * ratio of alpha-beta that is not consistent with the rest of the raters.
     * @param rated Node in question
     * @param raters List of nodes that interact with rated node
     */
    private static void unfairRatingFilter(int rated, ArrayList<Integer>
            raters) {
        boolean change = true; //we will reapply the algorithm until there
        // are no rater is excluded from the list
        while (change && raters.size() > 2) {
            change = false;
            //first we shall compute the cumulative expected value (score).
            // To achieve this, we sum up all alphas and betas from every
            // rater (save the one that's being judged)
            //and then we'll divide the totalAlpha / (totalAlpha + totalBeta)
            double totalAlpha = 0.0;
            double totalBeta = 0.0;
            for (int rater : raters) {
                int[] raterAlphaBeta = repBuffer.get(rater).getAlphaBeta(rated);
                totalAlpha += raterAlphaBeta[0];
                totalBeta += raterAlphaBeta[1];
            }
            System.out.println("INFRASTRUCTURE: totalAlpha = " + totalAlpha +
                    " totalBeta = " + totalBeta);

            //next, we will draw the alpha-beta distribution for each rater.
            // if the expectedValue deviates lower than the 1% quantile of
            // the distribution OR deviates higher than the 99% quantile...
            // ... then we conclude that the alpha-beta provided by this
            // rater is inconsistent with the other alphas and betas. This
            // rater is excluded.

            //we shall make use of an external library to compute the
            // alpha-beta distribution, namely 'apache commons Math'
            //if the 'apache commons library' is absent, download it from the
            // official website and import the folder.
            //in eclipse: project -> properties -> java build path -> source
            // -> link source -> search for the apache root folder

            double expectedValue = totalAlpha / (totalAlpha + totalBeta);
            Iterator<Integer> it = raters.iterator();
            while (it.hasNext()) {
                int rater = it.next();
                int[] raterAlphaBeta = repBuffer.get(rater).getAlphaBeta(rated);
                int raterAlpha = raterAlphaBeta[0];
                int raterBeta = raterAlphaBeta[1];
                System.out.println("INFRASTRUCTURE " + rater + "->" + rated +
                        " raterAlpha = " + raterAlpha + " raterBeta = " +
                        raterBeta);
                //When drawing the distribution, the alpha and beta can never
                // be Zero. Normally, the number 1 is added to prevent this.
                //However, we find that adding 0.1 works aswell, and c	auses
                // less impact on the distribution.
                BetaDistribution bDist = new BetaDistribution(raterAlpha + 0.1,
                                                              raterBeta + 0.1);

                double q = 0.05; //the q parameter may be modified.
                //A higher 'q' means a higher number of FalsePositives
                //A lower  'q' means a higher number of FalseNegatives
                double lowerQuantile = bDist.inverseCumulativeProbability(q);
                double upperQuantile = bDist.inverseCumulativeProbability(1 -
                        q);
                if (upperQuantile < expectedValue || lowerQuantile >
                        expectedValue) {
                    //this rater's distribution deviates considerably from
                    // the cumulative expected value. So we reject this rating.
                    it.remove();
                    System.out.println("INFRASTRUCTURE: expectedValue = " +
                            expectedValue + " mean = " + bDist
                            .getNumericalMean());
                    repDatabase.addCounter(rater, false);
                    change = true;
                }
            }


        }
    }
}
