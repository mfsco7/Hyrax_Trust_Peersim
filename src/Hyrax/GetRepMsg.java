package Hyrax;

import peersim.core.Node;

/**
 * Created by aferreira on 10-11-2015.
 */
public class GetRepMsg extends Message {

    GetRepMsg(Node sender) {
        super(Type.GET_REP, sender);
    }
}
