package utils;

/**
 * This class belongs to the package utils and is for being use on Hyrax
 * Trust Peersim.
 */
public class Interaction {

    private Long time;
    private Long nodeID;
    private Integer result;
    private TYPE type;

    public Interaction(Long time, Long nodeID, Integer result, TYPE type) {
        this.time = time;
        this.nodeID = nodeID;
        this.result = result;
        this.type = type;
    }

    public Long getTime() {
        return time;
    }

    public Long getNodeID() {
        return nodeID;
    }

    public Integer getResult() {
        return result;
    }

    public boolean setResult(Integer result) {
        this.result = result;
        return true;
    }

    public TYPE getType() {
        return type;
    }

    public enum TYPE {DOWNLOAD, UPLOAD}
}
