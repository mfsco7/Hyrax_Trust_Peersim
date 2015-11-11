package Hyrax;

import peersim.core.Node;

import java.io.File;

/**
 * Created by aferreira on 11-11-2015.
 */
public class SendFileMsg extends Message {
    private File file;

    SendFileMsg(Node msgSender, File file) {
        super(Type.SEND_FILE, msgSender);
        this.file = file;
    }

    SendFileMsg(Node msgSender, String fileName) {
        super(Type.SEND_FILE, msgSender);
        this.file = new File(fileName);
    }

    public File getFile() {
        return file;
    }
}
