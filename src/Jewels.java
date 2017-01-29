import java.util.HashMap;
import java.util.HashSet;

/**
 * Created by EJ on 1/28/2017.
 */
public class Jewels {

    /**
     * Given a start state and end state
     * Diamond -> Ruby -> Emerald -> Diamond
     * D D R     E E E
     * D D D --> E E E
     * D D D     E E E
     *
     * Starting Grid: DDD DDD DDD
     * Position     Result          Modifies
     *      0       RRD RDD DDD     0,1,3
     *      1       RRR DRD DDD     0,1,2,4
     *      2       DRR DDR DDD     1,2,5
     *      3       RDD RRD RDD     0,3,4,6
     *      4       DRD RRR DRD     1,3,4,5,7
     *      5       DDR DRR DDR     2,4,5,8
     *      6       DDD RDD RRD     3,6,7
     *      7       DDD DRD RRR     4,6,7,8
     *      8       DDD DDR DRR     5,7,8
     *
     */

    private HashMap<String, String[]> stateEdgeMap;
    private HashSet<String> visited;

    private String startState;
    private String endState;

    public Jewels(String startState, String goalState) {
    }

    public static void main(String[] args){
        //System.out.println("Hello World!");

        String startState = "";
        String goalState = "";

        Jewels j = new Jewels(startState, goalState);

        System.out.println( j.depthFirst() );
        System.out.println( j.bestFirst() );

    }

    private String bestFirst() {
        String solution = "";

        // Do stuff here...

        return solution;
    }

    private String depthFirst() {
        String solution = "";

        // Some more stuff here...

        return solution;
    }

}
