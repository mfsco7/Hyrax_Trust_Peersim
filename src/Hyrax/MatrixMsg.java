package Hyrax;

import peersim.core.Node;
import utils.ReputationMatrix;

/**
 * Created by aferreira on 10-11-2015.
 */
public class MatrixMsg extends Message {
    private ReputationMatrix reputationMatrix;

    MatrixMsg(Node sender, ReputationMatrix msgMatrix) {
        super(Type.MATRIX, sender);
        reputationMatrix = msgMatrix;
    }

    public ReputationMatrix getReputationMatrix() {
        return reputationMatrix;
    }
}
