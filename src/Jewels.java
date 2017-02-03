import java.util.*;

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

    private static final int[][] jewelChanges = new int[][]{
        {0,1,3},
        {0,1,2,4},
        {1,2,5},
        {0,3,4,6},
        {1,3,4,5,7},
        {2,4,5,8},
        {3,6,7},
        {4,6,7,8},
        {5,7,8},
    };

    public Jewels(String startState, String goalState) {
        stateEdgeMap = new HashMap<>();
        this.startState = startState;
        this.endState = goalState;
    }

    public static void main(String[] args){
        //System.out.println("Hello World!");

        String startState = "DDDDDDDDD";
        String goalState = "";

        Jewels j = new Jewels(startState, goalState);

        System.out.println( j.depthFirst() );
        System.out.println( j.bestFirst() );

        j.generate();
    }

    public void generate() {
        ArrayDeque<String> openQueue = new ArrayDeque<>();
        openQueue.add(startState);

        while (!openQueue.isEmpty()) {

            String stateToEval = openQueue.remove();
            if (!stateEdgeMap.containsKey(stateToEval)) {
                String[] children = generateChildren(stateToEval);
                Collections.addAll(openQueue, children);
                stateEdgeMap.put(stateToEval, children);
            }
        }
        System.out.println(stateEdgeMap.size());
        System.out.println(Arrays.toString(stateEdgeMap.get(startState)));
    }

    private String[] generateChildren(String parentState) {
        StringBuilder charSeq;
        String[] children = new String[9];
        for (int i = 0; i < 9; i++){
            charSeq = new StringBuilder(parentState);

            for (int j = 0; j < jewelChanges[i].length; j++){
                char c = charSeq.charAt(jewelChanges[i][j]);
                switch (c) {
                    case 'D':
                        c = 'R';
                        break;
                    case 'R':
                        c = 'E';
                        break;
                    case 'E':
                        c = 'D';
                        break;
                    default:
                        throw new IllegalArgumentException("Uhh... sumtin rong");
                }
                charSeq.setCharAt(jewelChanges[i][j], c);
            }

            children[i] = charSeq.toString();
        }
        return children;
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
