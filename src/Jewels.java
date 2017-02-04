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
        String goalState = "RRERERERR";

        Jewels j = new Jewels(startState, goalState);
        j.generate();

        // System.out.println( j.depthFirst() );

        String solution = j.bestFirst();

        // Simulating the found path to test if the result is correct.
        String currentState = startState;
        String[] pathPos = solution.split(" ");
        for (int i = 0; i < pathPos.length; i++){
            int pos = Integer.parseInt(pathPos[i]);
            currentState = apply(currentState, pos);
        }
        System.out.println("Simulated: " + currentState + " Expected: " + goalState + " Equal?: " + currentState.equals(goalState));
    }

    // Takes a state as a string and applies a move corresponding to the given position.
    public static String apply(String state, int pos){
        StringBuilder sb = new StringBuilder(state);

        for (int i = 0; i < jewelChanges[pos].length; i++){
            int charPos = jewelChanges[pos][i];
            char c = getNextChar( sb.charAt(charPos) );

            sb.setCharAt(jewelChanges[pos][i], c);
        }

        return sb.toString();
    }

    // Generates all possible states
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
        //System.out.println(stateAdjMap.size());
        //System.out.println(Arrays.toString(stateAdjMap.get(startState)));
    }

    // Returns the character that the given character will change to with one move
    public static char getNextChar(char c){
        switch(c) {
            case 'D':
                return 'R';
            case 'R':
                return 'E';
            case 'E':
                return 'D';
            default:
                throw new IllegalArgumentException("Uhh... sumtin rong");
        }
    }

    // Generates the child states of a given state representation.
    private String[] generateChildren(String parentState) {
        StringBuilder charSeq;
        String[] children = new String[9];
        for (int i = 0; i < 9; i++){
            charSeq = new StringBuilder(parentState);

            for (int j = 0; j < jewelChanges[i].length; j++){
                char c = getNextChar( charSeq.charAt(jewelChanges[i][j]) );
                charSeq.setCharAt(jewelChanges[i][j], c);
            }

            children[i] = charSeq.toString();
        }
        return children;
    }

    // Finds a solution and returns the path based on a heuristic. Factors in distance
    // so that if a state is revisited, it will choose the one with a shorter distance.
    private String bestFirst() {
        PriorityQueue<State> open = new PriorityQueue<>(new HeuristicComp());
        HashSet<State> closed = new HashSet<>();
        HashMap<State, Integer> distances = new HashMap<>();

        State currentState = new State(startState, -1);
        open.add(currentState);
        distances.put(currentState, 0);

        while(!open.isEmpty()){

            currentState = open.poll();
            int currentDistance = distances.get(currentState);
            if (currentState.equals(endState)) return findPath(currentState);

            String[] children = stateAdjMap.get(currentState.jewels);

            for (int i = 0; i < children.length; i++){
                State childState = new State(children[i], i);
                childState.setParent(currentState);
                boolean onOpen = open.contains(childState);
                boolean onClosed = closed.contains(childState);
                if (!onOpen && !onClosed){
                    open.add(childState);
                    distances.put(childState, currentDistance+1);
                } else if (onOpen) {
                    if (distances.get(childState) > currentDistance + 1){
                        open.remove(childState); // .equals() doesn't care about info other than string
                        open.add(childState); // Changes to state with parent that has shorter distance (and pos value)
                    }
                } else { // onClosed == true
                    if (distances.get(childState) > currentDistance + 1){
                        closed.remove(childState);
                        open.add(childState);
                    }
                }
            }
            closed.add(currentState);
        }
        return "Best-First Solution not found.";

    }

    // Takes the current state from best first search and returns a string representation of the path
    // from the start state to the current state.
    private String findPath(State currentState) {
        ArrayDeque<Integer> path = new ArrayDeque<>();

        while (currentState.getParent() != null){ // Prevents printing -1 from the start node
            path.push(currentState.pos);
            currentState = currentState.getParent();
        }
        String positionPath = "";
        while (!path.isEmpty()){
            positionPath += path.pop() + " ";
        }

        return positionPath;
    }

    // Finds a path to the goal node via a depth first search.
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
                return "No Dept-First solution found.";

            route.pop();
            currentState = stack.pop();
        }
        String soln = "";
        while (!route.isEmpty()){
            soln += route.removeLast() + " ";
        }
        return soln;
    }

    // Comparator for use in a priority queue to order smaller priorities first.
    private class HeuristicComp implements Comparator<State> {

        @Override
        public int compare(State o1, State o2) {
            return o1.getWeight() - o2.getWeight();
        }
    }

    // Currently only used in the best first search. Stores the heuristic weight, along with the states
    //  parent state and which position was chosen to generate the state.
    private class State {

        private String jewels;
        private int weight;
        private State parent = null; //set externally, null by default to not break method calls
        private int pos; //position flipped to get this state

        public State(String jewels, int pos){
            this.jewels = jewels;
            this.pos = pos;

            calcHeuristic();

        }

        // Calculates a heuristic value for a state based on how close each letter is to the goal letter.
        // The heuristic is a sum of the scores of all 9 jewels.
        // If the jewel is equal to the jewel in that position in the goal state, add 0;
        // If the jewel is 1 step away from the jewel in the goal state, add 1;
        // If the jewel is 2 steps away from the jewel in the goal state, add 2;
        // This amounts to the lowest possible heuristic being 0 (as in the goal state),
        //  or 18 if every jewel is 2 steps away from the final state.
        private void calcHeuristic(){
            int tmpweight = 0;
            for (int i=0;i<jewels.length();i++)
            {
                char c = jewels.charAt(i);
                switch (c) {
                    case 'D':
                        if (endState.charAt(i) == 'R')
                            tmpweight +=1;
                        else if (endState.charAt(i) == 'E')
                            tmpweight +=2;
                        //if D, add nothing
                        break;
                    case 'R':
                        if (endState.charAt(i) == 'E')
                            tmpweight +=1;
                        else if (endState.charAt(i) == 'D')
                            tmpweight +=2;
                        //if R, add nothing
                        break;
                    case 'E':
                        if (endState.charAt(i) == 'D')
                            tmpweight +=1;
                        else if (endState.charAt(i) == 'R')
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

        // For use in the priority queue when searching for a state. The only attribute that we care about
        // is the string representation of the state.
        @Override
        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            } else if (obj instanceof String) {
                String s = (String) obj;
                return jewels.equals(s);
            } else if (obj instanceof State){
                State s = (State) obj;
                // Compare the data members and return accordingly
                return jewels.equals(s.jewels);
            } else {
                return false;
            }
        }

        // For use in data structures like a HashSet or HashMap. The only attribute that matters is the
        // string representation of the state.
        @Override
        public int hashCode() {
            return jewels.hashCode();
        }
    }
}