package Hyrax;

/**
 * Created by aferreira on 11-11-2015.
 */
public class Interaction<Time, NodeID, Result, Type> {


    private Time time;
    private NodeID nodeID;
    private Result result;
    private Type type;

    Interaction(Time time, NodeID nodeID, Result result, Type type) {
        this.time = time;
        this.nodeID = nodeID;
        this.result = result;
        this.type = type;
    }

    public Time getTime() {
        return time;
    }

    public NodeID getNodeID() {
        return nodeID;
    }

    public Result getResult() {
        return result;
    }

    public Type getType() {
        return type;
    }
}
