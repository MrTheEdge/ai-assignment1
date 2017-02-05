import java.util.*;

/**
 * E.J. Schroeder
 * Matt Moellman
 * CSC 425 - Programming Assignment 1
 *
 * * Given a start state and end state
 * Diamond -> Ruby -> Emerald -> Diamond
 * D D D     E E E
 * D D D ==> E E E
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
 */
public class Jewels {

    private String startState;
    private String endState;

    private int totalVisits = 0;

    // An array of which positions change when a grid position is chosen
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
        this.startState = startState;
        this.endState = goalState;
    }

    public static void main(String[] args){

        /*
            Test Case 1:
                D D D     E E R
                D D D ==> E R E
                D D D     R E R
         */
        String startState = "DDDDDDDDD";
        String goalState = "EERERERER";

        Jewels j = new Jewels(startState, goalState);

        int[] dfSolution = j.depthFirst();
        System.out.println("DFS Solution: " + Arrays.toString(dfSolution));
        //simulate(startState, goalState, dfSolution);
        System.out.println("Total DFS Visits: " + j.totalVisits);

        int[] bfSolution = j.bestFirst();
        System.out.println("BestFS Solution: " + Arrays.toString(bfSolution));
        //simulate(startState, goalState, bfSolution);
        System.out.println("Total BestFS Visits: " + j.totalVisits);
        System.out.println();

        /*
            Test Case 2:
                D D D     R R E
                D D D ==> R E R
                D D D     E R R
         */

        startState = "DDDDDDDDD";
        goalState =  "RRERERERR";

        j = new Jewels(startState, goalState);

        dfSolution = j.depthFirst();
        System.out.println("DFS Solution: " + Arrays.toString(dfSolution));
        //simulate(startState, goalState, dfSolution);
        System.out.println("Total DFS Visits: " + j.totalVisits);

        bfSolution = j.bestFirst();
        System.out.println("BestFS Solution: " + Arrays.toString(bfSolution));
        //simulate(startState, goalState, bfSolution);
        System.out.println("Total BestFS Visits: " + j.totalVisits);
        System.out.println();

        /*
            Test Case 3:
                D D D     R D R
                D D D ==> D R D
                D D D     R D R
         */

        startState = "DDDDDDDDD";
        goalState =  "RDRDRDRDR";

        j = new Jewels(startState, goalState);

        dfSolution = j.depthFirst();
        System.out.println("DFS Solution: " + Arrays.toString(dfSolution));
        //simulate(startState, goalState, dfSolution);
        System.out.println("Total DFS Visits: " + j.totalVisits);

        bfSolution = j.bestFirst();
        System.out.println("BestFS Solution: " + Arrays.toString(bfSolution));
        //simulate(startState, goalState, bfSolution);
        System.out.println("Total BestFS Visits: " + j.totalVisits);
        System.out.println();

    }

    // Used as a test to see if the result from DFS and BestFS actually solve the puzzle.
    public static void simulate(String startState, String goalState, int[] solution){
        // Simulating the found path to test if the result is correct.
        String currentState = startState;
        for (int i = 0; i < solution.length; i++){
            currentState = apply(currentState, solution[i]);
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

    // Returns the character that corresponds with the next jewel in the sequence
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
    private int[] bestFirst() {
        // Priority queue will order its contents based on a heuristic value
        PriorityQueue<State> open = new PriorityQueue<>(new HeuristicComp());
        HashSet<State> closed = new HashSet<>();
        HashMap<State, Integer> distances = new HashMap<>(); // Store current total distances to nodes
        this.totalVisits = 0; //set total number of nodes visited counter to 0

        State currentState = new State(startState, -1);
        open.add(currentState);
        distances.put(currentState, 0);

        while(!open.isEmpty()){

            currentState = open.poll();
            this.totalVisits +=1; //increment counter of visited nodes
            int currentDistance = distances.get(currentState);
            // If the goal state is found, generate the path from start to goal
            if (currentState.equals(endState)) return findPath(currentState);

            String[] children = generateChildren(currentState.jewels);

            for (int i = 0; i < children.length; i++){
                // Generate a state for each child to store its heuristic value, parent, and grid position
                State childState = new State(children[i], i);
                childState.setParent(currentState);
                boolean onOpen = open.contains(childState);
                boolean onClosed = closed.contains(childState);
                if (!onOpen && !onClosed){
                    open.add(childState);
                    distances.put(childState, currentDistance+1); // Set the distance of the child based on the parent
                } else if (onOpen) {
                    if (distances.get(childState) > currentDistance + 1){
                        open.remove(childState); // .equals() doesn't care about info other than string
                        open.add(childState); // Changes to state with parent that has shorter distance (and pos value)
                    }
                } else { // onClosed == true
                    if (distances.get(childState) > currentDistance + 1){
                        // We've found a shorter route to this state, re-add to open
                        closed.remove(childState);
                        open.add(childState);
                    }
                }
            }
            closed.add(currentState);
        }
        return new int[]{-1}; // -1 means no solution found

    }

    // Takes the current state from best first search and returns a string representation of the path
    // from the start state to the current state.
    private int[] findPath(State currentState) {
        // Starting from the goal node, positions will be pushed onto the stack
        ArrayDeque<Integer> path = new ArrayDeque<>();

        while (currentState.getParent() != null){ // Prevents printing -1 from the start node
            path.push(currentState.pos);
            currentState = currentState.getParent();
        }
        int[] positionPath = new int[path.size()];
        for (int i = 0; i < positionPath.length; i++){
            // Popping positions off the stack will give the path in the correct order.
            positionPath[i] = path.pop();
        }

        return positionPath;
    }

    // Finds a path to the goal node via a depth first search.
    private int[] depthFirst() {

        ArrayDeque<Integer> route = new ArrayDeque<>(); // Stack to store the current position route.
        ArrayDeque<String> stack = new ArrayDeque<>(); // Stack to keep track of the states
        Set<String> visited = new HashSet<>();

        this.totalVisits = 0; //set total number of nodes visited counter to 0

        String currentState = startState;
        visited.add(currentState);
        this.totalVisits += 1;

        while (!currentState.equals(endState)){

            boolean openNodeFound = false; // Initially, no children are found
            String[] children = generateChildren(currentState);
            for (int i = 0; i < children.length; i++){
                if (!visited.contains(children[i])){ // Checking the current state for unvisited children
                    route.push(i); // The loop counter is the location on the grid that is being "pressed"
                    stack.push(children[i]); // Need to store the state as well, not just the grid location

                    visited.add(children[i]); // Mark child as visited and set it to be evaluated next loop iteration
                    currentState = children[i];

                    openNodeFound = true;
                    this.totalVisits += 1; //increment counter
                    break;
                }
            }
            if (openNodeFound) // If an open child is found, start another loop iteration
                continue;

            if (route.isEmpty())
                return new int[]{-1};

            route.pop(); // No child found, so back up
            currentState = stack.pop();
        }

        int[] soln = new int[route.size()];
        for (int i = 0; i < soln.length; i++){
            soln[i] =  route.removeLast();
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

            calculateHeuristic();
        }

        // Calculates a heuristic value for a state based on how close each letter is to the goal letter.
        // The heuristic is a sum of the scores of all 9 jewels.
        // If the jewel is equal to the jewel in that position in the goal state, add 0;
        // If the jewel is 1 step away from the jewel in the goal state, add 1;
        // If the jewel is 2 steps away from the jewel in the goal state, add 2;
        // This amounts to the lowest possible heuristic being 0 (as in the goal state),
        //  or 18 if every jewel is 2 steps away from the final state.
        private void calculateHeuristic(){
            int tmpweight = 0;
            for (int i=0;i<jewels.length();i++) {
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
                // Want to be able to compare string representations to State objects in a priority queue
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