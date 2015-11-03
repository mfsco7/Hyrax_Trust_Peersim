package Hyrax;

import peersim.core.Control;
import peersim.core.Network;
import peersim.core.Node;

/**
 * The observer, being a 'control' component, will run once in every cycle,
 * except if configure otherwise in the config file.
 * It will print reputations of the nodes every time it runs.
 */
public class Observer implements Control {

    /**
     * PID of the atributes's protocol
     */
    private static int atribpid;

    public Observer(String prefix) {
        //since we don't need to read any parameters for this component, this
        // is left empty.
    }

    /**
     * the initializer calls this method
     */
    public static void init(int attpid) {
        System.out.println("Observer was initiated");
        atribpid = attpid;
    }

    //this method is called by Peersim once every cycle
    @Override
    public boolean execute() {
        for (int i = 0; i < Network.size(); i++) {
            Node n = Network.get(i);
            NodeAttributes atribs = (NodeAttributes) n.getProtocol(atribpid);
            System.out.print("Node" + n.getIndex() + " is ");
            if (atribs.isVictim()) {
                System.out.print("Victim ");
            }
            if (atribs.isRandomRater()) {
                System.out.print("RandomRater ");
            }
            if (atribs.isEvilOverstater()) {
                System.out.print("Overstater ");
            }
            if (!atribs.isEvilOverstater() &&
                    !atribs.isRandomRater() &&
                    !atribs.isVictim()) {
                System.out.print("Normal");
            }
            System.out.println("");

            System.out.println("kindness = " + atribs.getKindness() + ", " +
                    "avg reputation = " + Infrastructure.askForAvgReputation(
                    (int) n.getIndex()));
            int[] credibility = Infrastructure.askForCredibility((int) n
                    .getIndex());
            System.out.println("his ratings were accepted " +
                    credibility[0] + " times");
            System.out.println("his ratings were rejected " +
                    credibility[1] + " times");
            System.out.println("---------------------");
        }
        return false;
    }
}
