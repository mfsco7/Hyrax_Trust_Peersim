package Hyrax;

import peersim.cdsim.CDState;
import peersim.core.Control;
import peersim.core.Network;
import peersim.core.Node;


/*The observer, being a 'control' component, will run once in every cycle. 
 * It will keep track of the current cycle number as if it was a clock.
 * Using the cycle number, nodes can know when to report their matrix to the infrastructure.
 * 
 * This observer will have an additional task of printing all final reputations of the nodes at the last cycle of the simulation
 * 
 */
public class Observer implements Control {

    private static int reportingInterval; //defines how frequently must the nodes report their matrix to the infrastructure
    //	private static int currentCycle; // a cycle symbolizes a time frame, so this variable symbolizes the clock for the simulation
    private static int maxCycles;   // the total number of cycles for the simulation
    private static int atribpid;    //pid of the atributes's protocol


    public Observer(String prefix) {
        //since we don't need to read any parameters for this component, this is left empty.
    }


    //the initializer calls this method
    public static void init(int maxRep, int maxC, int attpid) {
        System.out.println("Observer was initiated");
        reportingInterval = (maxC - 1) / maxRep + 1;
        maxCycles = maxC;
        atribpid = attpid;
//		currentCycle      = -1;
    }

    //nodes ask the 'observer' if they're supposed to report their matrix to the infrastructure this cycle
    public static boolean isTimeToReport() {
        return ((CDState.getCycle()) % reportingInterval) == 0;
    }


    //this method is called by Peersim once every cycle
    @Override
    public boolean execute() {
//		currentCycle++;
//		System.out.println("OBSERVER: currentCycle " + currentCycle);
//		System.out.println("OBSERVER: cdstatecycle " + CDState.getCycle());
        if (CDState.getCycle() == maxCycles - 1) {
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            for (int i = 0; i < Network.size(); i++) {
                Node n = Network.get(i);
                NodeAttributes atribs = (NodeAttributes) n.getProtocol(atribpid);
                System.out.print("Node" + n.getID() + " is ");
                if (atribs.isVictim()) {
                    System.out.print("Victim ");
                }
                if (atribs.isRandomRater()) {
                    System.out.print("RandomRater ");
                }
                if (atribs.isEvilOverstater()) {
                    System.out.print("Overstater ");
                }
                if (!atribs.isEvilOverstater() && !atribs.isRandomRater() && !atribs.isVictim()) {
                    System.out.print("Normal");
                }
                System.out.println("");

                System.out.println("kindness = " + atribs.getKindness() + ", reputation = " + Infrastructure.askForReputation((int) n.getID()));
                int[] credibility = Infrastructure.askForCredibility((int) n.getID());
                System.out.println("his ratings were accepted " + credibility[0] + " times");
                System.out.println("his ratings were rejected " + credibility[1] + " times");
                System.out.println("---------------------");
            }
        }
        return false;
    }

}
