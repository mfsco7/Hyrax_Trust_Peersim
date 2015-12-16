package peersim.bittorrent;

import peersim.core.Node;

import java.util.HashMap;

/**
 * This class belongs to the package peersim.bittorrent and is for being use
 * on Hyrax Trust Peersim.
 */
public class RequestMsg extends IntMsg {

    private HashMap<Long, Integer> download;
    private HashMap<Long, Integer> upload;

    /**
     * Constructor of a RequestMsg
     * @param sender
     * @param value
     * @param time
     * @param download
     * @param upload
     */
    public RequestMsg(Node sender, int value, long time,
                      HashMap<Long, Integer> download, HashMap<Long, Integer>
                              upload) {
        super(8, sender, value, time);
        this.download = download;
        this.upload = upload;
    }

    public HashMap<Long, Integer> getDownload() {
        return download;
    }

    public HashMap<Long, Integer> getUpload() {
        return upload;
    }
}
