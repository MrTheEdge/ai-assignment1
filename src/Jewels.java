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

    private HashMap<String, String[]> stateAdjMap;
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
        stateAdjMap = new HashMap<>();
        visited = new HashSet<>();
        this.startState = startState;
        this.endState = goalState;
    }

    public static void main(String[] args){
        //System.out.println("Hello World!");

        String startState = "DDDDDDDDD";
        String goalState = "DRRDDRDDD";

        Jewels j = new Jewels(startState, goalState);
        j.generate();

        System.out.println( j.depthFirst() );
        // System.out.println( j.bestFirst() );


    }

    public void generate() {
        ArrayDeque<String> openQueue = new ArrayDeque<>();
        openQueue.add(startState);

        while (!openQueue.isEmpty()) {

            String stateToEval = openQueue.remove();
            if (!stateAdjMap.containsKey(stateToEval)) {
                String[] children = generateChildren(stateToEval);
                Collections.addAll(openQueue, children);
                stateAdjMap.put(stateToEval, children);
            }
        }
        System.out.println(stateAdjMap.size());
        System.out.println(Arrays.toString(stateAdjMap.get(startState)));
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

        ArrayDeque<Integer> route = new ArrayDeque<>();
        ArrayDeque<String> stack = new ArrayDeque<>();
        visited.clear();

        String currentState = startState;
        visited.add(currentState);
        while (!currentState.equals(endState)){
            boolean openNodeFound = false;
            String[] children = stateAdjMap.get(currentState);
            for (int i = 0; i < children.length; i++){
                if (!visited.contains(children[i])){
                    route.push(i);
                    stack.push(children[i]);
                    visited.add(children[i]);
                    currentState = children[i];
                    openNodeFound = true;
                    break;
                }
            }
            if (openNodeFound)
                continue;

            if (route.isEmpty())
                return "No solution found.";

            route.pop();
            currentState = stack.pop();
        }
        String soln = "";
        while (!route.isEmpty()){
            soln += route.removeLast() + " ";
        }
        return "Solution found: " + soln;
    }


    private class HeuristicComp implements Comparator<State> {

        @Override
        public int compare(State o1, State o2) {
            return o1.getWeight() - o2.getWeight();
        }
    }

    private class State {

        private String jewels;
        private int weight;
        private State parent = null; //set externally, null by default to not break method calls
        private int pos; //position flipped to get this state

        public State(String jewels, int pos){
            this.jewels = jewels;
            this.pos = pos;

            StringBuilder tmpCurrent = new StringBuilder(jewels);
            StringBuilder tmpGoal = new StringBuilder(endState);
            int tmpweight = 0;
            for (int i=0;i<tmpCurrent.length();i++)
            {
                char c = tmpCurrent.charAt(i);
                switch (c) {
                    case 'D':
                        if (tmpGoal.charAt(i) == 'R')
                            tmpweight +=1;
                        else if (tmpGoal.charAt(i) == 'E')
                            tmpweight +=2;
                        //if D, add nothing
                        break;
                    case 'R':
                        if (tmpGoal.charAt(i) == 'E')
                            tmpweight +=1;
                        else if (tmpGoal.charAt(i) == 'D')
                            tmpweight +=2;
                        //if R, add nothing
                        break;
                    case 'E':
                        if (tmpGoal.charAt(i) == 'D')
                            tmpweight +=1;
                        else if (tmpGoal.charAt(i) == 'R')
                            tmpweight +=2;
                        //if E, add nothing
                        break;
                    default:
                        throw new IllegalArgumentException("Sumtin verwy wong!");
                }
            }
            this.weight = tmpweight; //minimum of 0 when == to goal node, max 18 if all nodes two steps away

        }

        public int getWeight() {
            return weight;
        }

        public State getParent() {
            return parent;
        }

        public void setParent(State parent) { //parent must be set externally!
            this.parent = parent;
        }
    }
}