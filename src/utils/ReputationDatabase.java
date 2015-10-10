package utils;

import java.util.Hashtable;


public class ReputationDatabase {
    //For every nodeX, there exists a tuple [TotalAlpha , TotalBeta] which is the accumulated alphas and betas of every nodeY that gave a rating to nodeX (excluding malicious ratings)
    //The final reputation of nodes shall be their (TotalAlpha / TotalAlpha + TotalBeta)
    Hashtable<Integer, int[]> reputations;

    //this 'credibilities' hashtable will serve to store counters for the number of times a node is caught lying (his ratings were disapproved by the filter algorithms).
    // it will also register the number of times that the ratings are deemed trustworthy.
    // So basically every nodeID will have a tuple of [numberOfApprovedRatings , numberOfDisapprovedRatings]
    // In the future, these values might be useful as metrics for assigning rater weight when averaging ratings.
    Hashtable<Integer, int[]> credibilities;


    public ReputationDatabase() {
        reputations = new Hashtable<Integer, int[]>();
        credibilities = new Hashtable<Integer, int[]>();
    }


/*
public void updateMatrix(int nodeID , ReputationMatrix mat){
	if(database.contains(nodeID)){
		for(int rated : mat.getRatedNodes()){
			database.get(nodeID).updateRatings(rated, mat.getAlphaBeta(rated)[0], mat.getAlphaBeta(rated)[1]);
		}System.out.println("no victims found");
	}
	else{
		ReputationMatrix newMat = mat.clone();
		database.put(nodeID, newMat);
	}
}

*/

    public String getReputation(int nodeID) {

        if (reputations.containsKey(nodeID)) {
            int[] alphaBeta = reputations.get(nodeID);
            double alpha = (double) alphaBeta[0];
            double beta = (double) alphaBeta[1];
            double reputation = (alpha + 1) / (alpha + beta + 2);
            return Double.toString(reputation);
        } else {
            return "Not Rated";
        }

    }

    public int[] getCredibility(int nodeID) {
        if (credibilities.containsKey(nodeID)) {
            return credibilities.get(nodeID);
        } else {
            return new int[]{0, 0};
        }
    }

    public void addRatings(int nodeID, int alpha, int beta) {

        if (reputations.containsKey(nodeID)) {
            int[] oldAlphaBeta = reputations.get(nodeID);
            int[] newAlphaBeta = {oldAlphaBeta[0] + alpha, oldAlphaBeta[1] + beta};
            reputations.put(nodeID, newAlphaBeta);
        } else {
            int[] alphaBeta = {alpha, beta};
            reputations.put(nodeID, alphaBeta);
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


}