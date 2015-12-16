package peersim.bittorrent;/*
 * Copyright (c) 2007-2008 Fabrizio Frioli, Michele Pedrolli
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License version 2 as
 * published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 *
 * --
 *
 * Please send your questions/suggestions to:
 * {fabrizio.frioli, michele.pedrolli} at studenti dot unitn dot it
 *
 */

import peersim.config.Configuration;
import peersim.core.CommonState;
import peersim.core.Control;
import peersim.core.Network;
import peersim.util.IncrementalFreq;
import peersim.util.IncrementalStats;
import utils.Interaction;

import java.util.Map;

/**
 * This {@link Control} provides a way to keep track of some
 * parameters of the peersim.BitTorrent network.
 */
public class BTObserver implements Control {

    /**
     * The protocol to operate on.
     *
     * @config
     */
    private static final String PAR_PROT = "protocol";

    /**
     * Protocol identifier, obtained from config property
     */
    private final int pid;

    /**
     * The basic constructor that reads the configuration file.
     *
     * @param prefix the configuration prefix for this class
     */
    public BTObserver(String prefix) {
        pid = Configuration.getPid(prefix + "." + PAR_PROT);
    }

    /**
     * Prints information about the peersim.BitTorrent network
     * and the number of leechers and seeders.
     * Please refer to the code comments for more details.
     *
     * @return always false
     */
    public boolean execute() {
        IncrementalFreq nodeStatusStats = new IncrementalFreq();
        IncrementalStats neighborStats = new IncrementalStats();

        int numberOfNodes = Network.size();
        int numberOfCompletedPieces = 0;

        // cycles from 1, since the node 0 is the tracker
        for (int i = 1; i < numberOfNodes; ++i) {

            // stats on number of leechers and seeders in the network
            // and consequently also on number of completed files in the network
            nodeStatusStats.add(((BitTorrent) (Network.get(i).getProtocol
                    (pid))).getPeerStatus());

            // stats on number of neighbors per peer
            neighborStats.add(((BitTorrent) (Network.get(i).getProtocol(pid))
            ).getNNodes());
        }

        // number of the pieces of the file, equal for every node, here 1 is
        // chosen,
        // since 1 is the first "normal" node (0 is the tracker)
        int numberOfPieces = ((BitTorrent) (Network.get(1).getProtocol(pid)))
                .nPieces;

        for (int i = 1; i < numberOfNodes; ++i) {
            numberOfCompletedPieces = 0;

            // discovers the status of the current peer (leecher or seeder)
            int ps = ((BitTorrent) (Network.get(i).getProtocol(pid)))
                    .getPeerStatus();
            String peerStatus;
            if (ps == 0) {
                peerStatus = "L"; //leecher
            } else {
                peerStatus = "S"; //seeder
            }


            if (Network.get(i) != null) {

                // counts the number of completed pieces for the i-th node
                for (int j = 0; j < numberOfPieces; j++) {
                    if (((BitTorrent) (Network.get(i).getProtocol(pid)))
                            .getFileStatus()[j] == 16) {
                        numberOfCompletedPieces++;
                    }
                }

				/*
                 * Put here the output lines of the Observer. An example is
				 * provided with
				 * basic information and stats.
				 * CommonState.getTime() is used to print out time references
				 * (useful for graph plotting).
				 */

                System.out.println("OBS: node " + ((BitTorrent) (Network.get
                        (i).getProtocol(pid))).getThisNodeID() + "(" +
                        peerStatus + ")" + "\t pieces completed: " +
                        numberOfCompletedPieces + "\t \t down: " + (
                        (BitTorrent) (Network.get(i).getProtocol(pid)))
                        .nPiecesDown + "\t up: " + ((BitTorrent) (Network.get
                        (i).getProtocol(pid))).nPiecesUp + " time: " +
                        CommonState.getTime());
                //System.out.println("[OBS] t " + CommonState.getTime() + "\t
                // pc " + numberOfCompletedPieces + "\t n " + ((peersim
                // .BitTorrent)(Network.get(i).getProtocol(pid)))
                // .getThisNodeID());
                //				System.out.println("OBS:");
                //				((BitNode) Network.get(i))
                // .printInteractions();
                //                System.out.print("OBS: neighbors ");
                //                for (Neighbor neighbor : ((BitTorrent)
                // (Network.get(i)
                //						.getProtocol(pid))).getCache()) {
                //                    if (neighbor != null && neighbor
                // .node!=null) {
                //
                //                    System.out.print(neighbor.node.getID()
                // + " ");
                //                    }
                //                }
                //                System.out.println();
                BitNode node = ((BitNode) (Network.get(i)));
                //                for (Neighbor neighbor : ((BitTorrent)
                // (Network.get(i)
                //                        .getProtocol(pid))).getCache()) {
                //                    if (neighbor != null && neighbor.node
                // != null) {
                //                        node.printInteractions(neighbor
                // .node.getID(),
                //                                Interaction.TYPE.DOWNLOAD);
                //                        neighbor
                //                 .node
                // .printInteractions();
                //                        System
                //                 .out.print(neighbor
                // .node.getID() + " ");
                //                        node.printInteractions
                // (neighbor
                //                 .node.getID(),
                //                                Interaction.TYPE
                // .UPLOAD);

                //                    }
                //                }

                if (node.getID() == 2) {
                    for (Map.Entry<Long, Integer> entry : node
                            .getSortedInteractions(Interaction.TYPE.DOWNLOAD)
                            .entrySet()) {
                        System.out.println(entry.getKey() + ":" + entry
                                .getValue());
                    }
                }


                //                System.out.println();
                //                ((BitNode) (Network.get(i)))
                // .printInteractions();
            } else {
                //System.out.println("[OBS] t " + CommonState.getTime() + "\t
                // pc " + "0" + "\t n " + "0");
            }

        }

        // prints the frequency of 0 (leechers) and 1 (seeders)
        nodeStatusStats.printAll(System.out);

        // prints the average number of neighbors per peer
        System.out.println("Avg number of neighbors per peer: " +
                neighborStats.getAverage());
        //        if (nodeStatusStats.getFreq(0) == 0) {
        //            for (int, i = 0; i < 9000; i++) {
        //                System.out.println();
        //            }
        //        }
        return false;
    }
}