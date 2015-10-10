package Hyrax;

import peersim.core.Protocol;
import utils.ReputationMatrix;


public class NodeAttributes implements Protocol {


    /*****************************
     * FIELDS           *
     *****************************/
    private int kindness; // integer ranging from 0 to 100 which determines the probability this node will satisfy a neighbour's request for an interaction

    private boolean evilOverstater; //these kind of evil nodes will overfill their reputationMatrixes with unfair ratings larger than the actual number of interactions for specific victim nodes

    private int maxOverstates; //represents the highest number of ratings that an evil node may add to his reputation per cycle

    /**
     * is this node being a target of unfair reputations given by Evil nodes?
     */
    private boolean isVictim; //is this node being a target of unfair reputations given by Evil nodes?

    private boolean isRandomRater; //another type of malicious node. this one will just add a random rating to a random node per cycle without caring about the result of the interaction
    private int randomChance; // if this node is a randomRater, he has a randomChance of adding a positive rating. Else it will add a negative rating.

    private ReputationMatrix repMatrix; //the node's ReputationMatrix is where he'll store ratings for the other nodes.


    /*****************************
     * Constructor         *
     *****************************/
    public NodeAttributes(String prefix) {
        this.kindness = 100;
        this.evilOverstater = false;
        this.maxOverstates = 0;
        this.isVictim = false;
        this.isRandomRater = false;
        this.randomChance = 0;
        this.repMatrix = new ReputationMatrix();
    }


    /*****************************
     * Clone method        *
     * *
     * Peersim generates nodes  *
     * by cloning, so we need   *
     * to define this method    *
     *****************************/

    public Object clone() {
        NodeAttributes nodeClone = null;
        try {
            nodeClone = (NodeAttributes) super.clone();
        } catch (CloneNotSupportedException e) {
            //never happens
        }
        nodeClone.setRepMatrix(this.repMatrix.clone());
        return nodeClone;

    }


    /*****************************
     * SETTERS / GETTERS      *
     *****************************/
    public int getKindness() {
        return kindness;
    }

    public void setKindness(int kindness) {
        this.kindness = kindness;
    }


    public boolean isEvilOverstater() {
        return evilOverstater;
    }

    public void setOverstater(boolean isEvil) {
        this.evilOverstater = isEvil;
    }


    public int getMaxOverstates() {
        return maxOverstates;
    }

    public void setMaxOverstates(int maxEvil) {
        this.maxOverstates = maxEvil;
    }


    public boolean isVictim() {
        return isVictim;
    }

    public void setVictim(boolean isVictim) {
        this.isVictim = isVictim;
    }


    public boolean isRandomRater() {
        return isRandomRater;
    }

    public void setRandomRater(boolean isRandomRater) {
        this.isRandomRater = isRandomRater;
    }


    public int getRandomChance() {
        return randomChance;
    }

    public void setRandomChance(int randomChance) {
        this.randomChance = randomChance;
    }


    public ReputationMatrix getRepMatrix() {
        return repMatrix;
    }

    public void setRepMatrix(ReputationMatrix repMatrix) {
        this.repMatrix = repMatrix;
    }


}
