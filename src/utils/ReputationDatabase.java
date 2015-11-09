package utils;

import Hyrax.HyraxSimulation;
import Hyrax.NodeAttributes;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.math3.stat.descriptive.moment.GeometricMean;
import peersim.core.Network;
import peersim.util.MedianStats;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Hashtable;


public class ReputationDatabase {
    //For every nodeX, there exists a tuple [TotalAlpha , TotalBeta] which is
    // the accumulated alphas and betas of every nodeY that gave a rating to
    // nodeX (excluding malicious ratings)
    //The final reputation of nodes shall be their (TotalAlpha / TotalAlpha +
    // TotalBeta)
    Hashtable<Integer, int[]>[] reputations;

    //this 'credibilities' hashtable will serve to store counters for the
    // number of times a node is caught lying (his ratings were disapproved
    // by the filter algorithms).
    // it will also register the number of times that the ratings are deemed
    // trustworthy.
    // So basically every nodeID will have a tuple of
    // [numberOfApprovedRatings , numberOfDisapprovedRatings]
    // In the future, these values might be useful as metrics for assigning
    // rater weight when averaging ratings.
    Hashtable<Integer, int[]> credibilities;

    public ReputationDatabase() {
        //TODO redo this assignment
        reputations = new Hashtable[Network.size()];
        for (int i = 0; i < Network.size(); i++) {
            reputations[i] = new Hashtable<>();
        }
        credibilities = new Hashtable<>();
    }

/*
public void updateMatrix(int nodeID , ReputationMatrix mat){
	if(database.contains(nodeID)){
		for(int rated : mat.getRatedNodes()){
			database.get(nodeID).updateRatings(rated, mat.getAlphaBeta(rated)
			[0], mat.getAlphaBeta(rated)[1]);
		}System.out.println("no victims found");
	}
	else{
		ReputationMatrix newMat = mat.clone();
		database.put(nodeID, newMat);
	}
}

*/

    public String getReputation(int rater, int rated) {

        if (reputations[rated].containsKey(rater)) {
            int[] alphaBeta = reputations[rated].get(rater);
            double alpha = (double) alphaBeta[0];
            double beta = (double) alphaBeta[1];
            double reputation = (alpha + 1) / (alpha + beta + 2);
            return Double.toString(reputation);
        } else {
            return "Not Rated";
        }
    }

    public String getAvgReputation(int rated) {

        int numReps = 0;
        double reputationSum = 0;

        Hashtable<Integer, Double> goodReputations = unfairRatingFilter(rated);

        System.out.println("RepDB: AvgReputation");
        for (int rater : goodReputations.keySet()) {
            Double rep = goodReputations.get(rater);
            reputationSum += rep;
            numReps++;
            System.out.println("RepDB: " + rater + "->" + rated + " " +
                    rep);
        }
        return (numReps == 0) ? "Not Rated" : (Double.toString(reputationSum
                / numReps));
    }

    public int[] getCredibility(int nodeID) {
        if (credibilities.containsKey(nodeID)) {
            return credibilities.get(nodeID);
        } else {
            return new int[]{0, 0};
        }
    }

    public void addRatings(int rater, int rated, int alpha, int beta) {
        if (reputations[rated].containsKey(rater)) {
            int[] oldAlphaBeta = reputations[rated].get(rater);
            int[] newAlphaBeta = {oldAlphaBeta[0] + alpha, oldAlphaBeta[1] +
                    beta};
            reputations[rated].put(rater, newAlphaBeta);
        } else {
            int[] alphaBeta = {alpha, beta};
            reputations[rated].put(rater, alphaBeta);
        }
    }

    public void addCounter(int nodeID, boolean result) {
        if (result) {
            if (credibilities.containsKey(nodeID)) {
                credibilities.get(nodeID)[0]++;
            } else {
                credibilities.put(nodeID, new int[]{1, 0});
            }
        } else {
            if (credibilities.containsKey(nodeID)) {
                credibilities.get(nodeID)[1]++;
            } else {
                credibilities.put(nodeID, new int[]{0, 1});
            }
        }
    }

    /**
     * Fetch the ratings of each rater, computes their respective frequency
     * and then calculates the nth root of frequency, being n the number of
     * raters of that node
     *
     * @param rated The node we want to now the Geometric Mean
     * @return Geometric Mean for rated node
     */
    private double calcGeoMean(int rated) {
        double freq = 1;
        for (int[] rating : reputations[rated].values()) {
            freq *= (rating[0] + 1.0) / (rating[0] + rating[1] + 2);
        }
        return Math.pow(freq, (1.0 / reputations[rated].size()));
    }

    private double calcGeoMean2(int rated) {
        double freq;
        GeometricMean geometricMean = new GeometricMean();
        for (int[] rating : reputations[rated].values()) {
            freq = (rating[0] + 1.0) / (rating[0] + rating[1] + 2);
            geometricMean.increment(freq);
        }
        return geometricMean.getResult();
    }

    /**
     * Calculates the rated Geometric Mean and check the outlier raters by
     * comparing their frequency with a deviation parameter using the
     * formula: | rater_freq - geo_mean| > deviation * geo_mean. If previous
     * formula succeeds then rater is outlier and is not returned
     *
     * @param rated The node which the filter will be applied
     * @return A pair of <rater,freq> that passed in this filter
     */
    private Hashtable<Integer, Double> unfairRatingFilter(int rated) {

        //        double geoMean = calcGeoMean(rated);
        //        double median = calcMedian(rated);

        //        calcAvgDeviations(rated, geoMean, median);
        Hashtable<Integer, Double> goodReputations = new Hashtable<>();

        try {
            CSVPrinter csv = new CSVPrinter(new FileWriter("HyraxConfig.csv",
                    true), CSVFormat.DEFAULT);
            csv.printRecord("node" + rated, ((NodeAttributes) Network.get
                    (rated).getProtocol(HyraxSimulation.atributesID))
                    .getKindness() + " kindness");

            for (Integer rater : reputations[rated].keySet()) {
                int[] alphaBeta = reputations[rated].get(rater);
                int alpha = alphaBeta[0];
                int beta = alphaBeta[1];

                double rep = (alpha + 1d) / (alpha + beta + 2d);
                csv.printRecord(rater + "->" + rated,rep);
                //            if (Math.abs(rep - geoMean) >
                // arithMeanDeviation) {
                //            } else {
                //            goodReputations.put(rater, rep);
                //            }
            }
            csv.printRecord();
            csv.flush();
            csv.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return goodReputations;
    }

    private double calcArithMeanDeviations(HashMap<Integer, Double>
                                                   deviations) {

        double totalValue = 0;
        for (double value : deviations.values()) {
            totalValue += Math.abs(value);
        }
        return totalValue / deviations.size();
    }

    //    private double calcAvgDeviations(int rated, double geoMean, double
    // median) {
    //        IncrementalStats arithMeanDev = new IncrementalStats();
    //        IncrementalStats arithMeanDev2 = new IncrementalStats();
    //        GeometricMean geometricMean = new GeometricMean();
    //        Median medianDev = new Median();
    //        medianDev.evaluate();
    //        ArrayList<Double> deviations = new ArrayList<>
    // (reputations[rated].size());
    //        ArrayList<Double> deviations2 = new ArrayList<>(reputations[rated]
    //                .size());
    //        for (Integer rater : reputations[rated].keySet()) {
    //            int[] alphaBeta = reputations[rated].get(rater);
    //            int alpha = alphaBeta[0];
    //            int beta = alphaBeta[1];
    //            double rep = (alpha + 1d) / (alpha + beta + 2d);
    //            System.out.println("RepDB: " + Math.abs(rep - geoMean));
    //            deviations.add(Math.abs(rep - geoMean));
    //            deviations.add(Math.abs(rep - median));
    //        }
    //        Double[] devs = (Double[]) deviations.toArray();
    //        Double[] devs2 = (Double[]) deviations2.toArray();
    //
    //        IncrementalStats stats = new IncrementalStats();
    //        for (Double deviation : deviations.values()) {
    //
    //        }
    //        if (typeOfAvgDev == 0) {
    //
    //        }
    //    }

    private double calcMedian(int rated) {
        MedianStats stats = new MedianStats();

        for (Integer rater : reputations[rated].keySet()) {
            int[] alphaBeta = reputations[rated].get(rater);
            int alpha = alphaBeta[0];
            int beta = alphaBeta[1];
            double rep = (alpha + 1d) / (alpha + beta + 2d);
            stats.add(rep, 1);
        }

        return (reputations.length > 0 ? 0 : stats.getMedian());
    }
}