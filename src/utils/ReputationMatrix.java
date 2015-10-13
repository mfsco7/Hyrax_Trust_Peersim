package utils;

import java.util.HashMap;


public class ReputationMatrix {
    /*
    * Each node has a reputationMatrix to store ratings. For example, if
    * nodeA interacts with nodeB, and nodeB satisfies the 'request',
	*then nodeA will add a positive rating (an alpha) to nodeB in nodeA's
	* reputationMatrix. Else it will be a negative rating (a beta).
	*
	* the key-value pair of the HashMap represent the ID of the node that was
	* rated and the respective alpha and beta (in the form of a list [alpha,
	* beta])
	* alpha = n of successes  ,  beta = n of failures
	* 
	* Here is an example of a ReputationMatrix for node3:
	*        
	* NODE 3 REPUTATION MATRIX
	*---------------------------
	*|   ID     | [alpha,beta] |
	*---------------------------
	*|    0     |   [2 , 3]    |
	*|    1     |   [5 , 1]    |
	*|    2     |   [7 , 9]    |
	*|    4     |   [11, 6]    |
	*---------------------------
	*
	*What this matrix tell us is that, up until now, node3 interacted with 4
	* different neighbors.
	*For example, node3 interacted with node2  16 times.
	*    From those 16 interactions, 7 were successful (node2 satisfied the
	*    request) and 9 were failures.
	*
	*/

    private HashMap<Integer, int[]> matrix;

    public ReputationMatrix() {
        matrix = new HashMap<Integer, int[]>();
    }

    public int size() {
        return matrix.size();
    }

    public void addRating(int nodeId, boolean rating) {
        int[] alphaBeta;
        if (matrix.containsKey(nodeId)) {
            alphaBeta = matrix.get(nodeId);
            if (rating) {
                alphaBeta[0] = alphaBeta[0] + 1;
            } else {
                alphaBeta[1] = alphaBeta[1] + 1;
            }
        } else {
            alphaBeta = new int[2];
            if (rating) {
                alphaBeta[0] = 1;
                alphaBeta[1] = 0;
            } else {
                alphaBeta[0] = 0;
                alphaBeta[1] = 1;
            }


        }
        matrix.put(nodeId, alphaBeta);

    }

    public Integer[] getRatedNodes() {
        return matrix.keySet().toArray(new Integer[0]);
    }


    public void updateRatings(int nodeId, int alpha, int beta) {
        if (matrix.containsKey(nodeId)) {
            int[] alphaBeta = matrix.get(nodeId);
            int[] newAlphaBeta = {alpha + alphaBeta[0], beta + alphaBeta[1]};
            matrix.put(nodeId, newAlphaBeta);
        } else {
            int[] alphaBeta = {alpha, beta};
            matrix.put(nodeId, alphaBeta);
        }
    }


    public int[] getAlphaBeta(int nodeID) {
        if (matrix.containsKey(nodeID)) {
            return matrix.get(nodeID);
        }
        return null;
    }

    public boolean containsID(int ID) {
        return matrix.containsKey(ID);
    }

    public void clear() {
        matrix.clear();
    }


    public ReputationMatrix clone() {
        ReputationMatrix matrixClone = new ReputationMatrix();
        for (int key : this.matrix.keySet()) {
            matrixClone.matrix.put(key, this.matrix.get(key).clone());
        }
        return matrixClone;
    }


}



