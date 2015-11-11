package Hyrax;

import peersim.core.Node;

/**
 * Created by aferreira on 11-11-2015.
 */
public class GetFileMsg extends Message {
    private String fileName;

    GetFileMsg(Node msgSender, String fileName) {
        super(Type.GET_FILE, msgSender);
        this.fileName = fileName;
    }

    public String getFileName() {
        return fileName;
    }
}
