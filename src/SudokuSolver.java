import java.util.Random;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public class SudokuSolver {
    private int size;
    private int sqrtSize;
    private int[][] board;
    private ArrayList<ArrayList<ArrayList<Integer>>> constraints;
    private LinkedList<Arc> queue;

    /**
     * Default constructor for the SodukuSolver class that creates a 9x9 board
     */
    public SudokuSolver() {
        this.size = 9;
        this.sqrtSize = 3;
        generateBoard();
    }

    /**
     * Constructor for the SodukuSolver class that takes a size and randomly generates a board
     * @param size the size of the board (i.e., 1 - size numbers per row, column, and box)
     * @throws IllegalArgumentException if the size is not a square
     */
    public SudokuSolver(int size) {
        if (Math.sqrt(size) != (int) Math.sqrt(size)) {
            throw new IllegalArgumentException("Size must be a square");
        } else {
            this.size = size;
            this.sqrtSize = (int) Math.sqrt(size);
            generateBoard();
        }
    }

    /**
     * Constructor for the SodukuSolver class that takes a pre-generated board
     * @param board a square 2D array of integers representing the board
     * @throws IllegalArgumentException if the board's length is not a square
     */
    public SudokuSolver(int[][] board) {
        this.board = board;
        this.size = board.length;

        if (Math.sqrt(size) != (int) Math.sqrt(size) || board[0].length != size) {
            throw new IllegalArgumentException("Size of the board must be a square");
        } else {
            this.sqrtSize = (int) Math.sqrt(size);
        }

        // TODO: perhaps extract this into a method
        // initialize the constraints array
        this.constraints = new ArrayList<ArrayList<ArrayList<Integer>>>(this.size);

        for (int i = 0; i < this.size; i++) {
            this.constraints.add(new ArrayList<ArrayList<Integer>>(this.size));
            for (int j = 0; j < this.size; j++) {
                this.constraints.get(i).add(new ArrayList<Integer>());
            }
        }

        for (int i = 0; i < this.size; i++) {
            for (int j = 0; j < this.size; j++) {
                if (this.board[i][j] != 0) {
                    this.constraints.get(i).get(j).add(this.board[i][j]);
                } else {
                    for (int k = 1; k <= this.size; k++) {
                        this.constraints.get(i).get(j).add(k);


                        // if (isValidGuess(i, j, k)) {
                        //     this.constraints.get(i).get(j).add(k);
                        // }
                    }
                }
            }
        }
        
        // TODO: perhaps extract this into a method
        // initialize the queue
        this.queue = new LinkedList<Arc>();
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                ArrayList<Arc> arcs = getAdjacentArcs(i, j);
                queue.addAll(arcs);
            }
        }
    }

    /**
     * Generates a random board with the size provided in the class constructor
     * blanks are stored as 0
     * Note: the board is not guaranteed to be solvable
     * @return a 2D array of integers representing a sudoku board
     */
    private void generateBoard() {
        this.board = new int[this.size][this.size];

        // initialize the board to 0
        for (int i = 0; i < this.size; i++) {
            for (int j = 0; j < this.size; j++) {
                board[i][j] = 0;
            }
        }

        // fill the indepent diagonal sub-matrices
        for (int i = 0; i < this.size; i += this.sqrtSize) {
            fillSubMatrix(i, i);
        }
    }

    @Override
    public String toString() {
        String outString = "";

        for (int i = 0; i < size; i++) {
            if (i % sqrtSize == 0) {
                for (int j = 0; j <= 2 * (size + sqrtSize); j++) {
                    outString += "-";
                }
                outString += "\n";
            }
            for (int j = 0; j < size; j++) {
                    if (j % sqrtSize == 0) {
                        outString += "| ";
                    }
                    outString += (board[i][j] == 0 ? " " : board[i][j]) + " ";
            }
            outString += "|\n";
        }
        for (int j = 0; j <= 2 * (size + sqrtSize); j++) {
            outString += "-";
        }

        return outString;
    }

    /**
     * Attempts to solve the sudoku board using the AC3 algorithm
     * @return true if the queue is empty, false otherwise
     */
    public boolean runAC3() {
        int count = 0;
        while (!queue.isEmpty()) {
            // System.out.println("Iteration: " + count);
            Arc arc = queue.remove();
            int[] xiLocation = arc.getXiLocation();
            int[] xjLocation = arc.getXjLocation();

            ArrayList<Integer> xiDomList = constraints.get(xiLocation[0]).get(xiLocation[1]);
            ArrayList<Integer> xjDomList = constraints.get(xjLocation[0]).get(xjLocation[1]);

            count++;
            if (revise(xiDomList, xjDomList)) {
                if (xiDomList.isEmpty()) {
                    return false;
                }
                ArrayList<Arc> arcs = getReversedAdjacentArcs(xiLocation[0], xiLocation[1]);
                queue.addAll(arcs);
            }
        }

        return true;
    }

    public boolean runBacktrack() {
        if (isValidSolution() && isComplete()) {
            return true;
        }

        // make copies of the board and constraints to revert to on failure
        int[][] boardCopy = new int[size][size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                boardCopy[i][j] = board[i][j];
            }
        }

        ArrayList<ArrayList<ArrayList<Integer>>> constraintsCopy = new ArrayList<ArrayList<ArrayList<Integer>>>(size);
        for (int i = 0; i < size; i++) {
            constraintsCopy.add(new ArrayList<ArrayList<Integer>>(size));
            for (int j = 0; j < size; j++) {
                constraintsCopy.get(i).add(new ArrayList<Integer>());
                for (int k = 0; k < constraints.get(i).get(j).size(); k++) {
                    constraintsCopy.get(i).get(j).add(constraints.get(i).get(j).get(k));
                }
            }
        }

        int[] var = selectUnassignedVariable();
        int row = var[0];
        int col = var[1];

        ArrayList<Integer> domain = constraints.get(var[0]).get(var[1]);
        for (int i = 0; i < domain.size(); i++) {
            int value = domain.get(i);
            if (isValidGuess(row, col, value)) {
                board[row][col] = value;
                removeOtherValuesFromDomain(row, col, value);
                boolean inference = runAC3();
                // System.out.println("Inference: " + inference);
                // System.out.println(this.constraints);
                if (inference) {
                    boolean result = runBacktrack();
                    if (result) {
                        return true;
                    }
                }
                // boolean result = runBacktrack();
                // if (result) {
                //     return true;
                // }
            }
            // remove changes made to board and constraints on failure
            this.board = boardCopy;
            this.constraints = constraintsCopy;
        }
        return false;
    }

    // this can probably be refined some -- do later in testing
    public boolean solve() {
        // boolean ac3Succeeded = runAC3();
        // if (ac3Succeeded && backtrackingNeeded()) {
        //     return runBacktracking();
        // } else if (ac3Succeeded) { // if ac3 succeeded and backtracking is not needed
        //     assignRemainingValues();
        //     if (isValidSolution()) {
        //         return true;
        //     } else {
        //         return false;
        //     }
        // } else { // if ac3 failed
        //     return ac3Succeeded;
        // }

        return runBacktrack();
    }

    // MARK: - Helper methods for AC3
    private ArrayList<Arc> getAdjacentArcs(int row, int col) {
        ArrayList<Arc> arcList = new ArrayList<Arc>();
        for (int i = 0; i < size; i++ ) {
            if (i != row) {
                arcList.add(new Arc(row, col, i, col));
            }
            if (i != col) {
                arcList.add(new Arc(row, col, row, i));
            }
        }
        int startRow = ((int) (row / 3)) * 3;
        int startCol = ((int) (col / 3)) * 3;

        for (int i = startRow; i < startRow + sqrtSize; i++) {
            for (int j = startCol; j < startCol + sqrtSize; j++) {
                if (row != i && j != col) {
                    arcList.add(new Arc(row, col, i, j));
                }
            }
        }
        return arcList;
    }   
    
    private ArrayList<Arc> getReversedAdjacentArcs(int row, int col) {
        ArrayList<Arc> arcList = new ArrayList<Arc>();
        for (int i = 0; i < size; i++ ) {
            if (i != row) {
                arcList.add(new Arc(i, col, row, col));
            }
            if (i != col) {
                arcList.add(new Arc(row, i, row, col));
            }
        }
        int startRow = ((int) (row / 3)) * 3;
        int startCol = ((int) (col / 3)) * 3;

        for (int i = startRow; i < startRow + sqrtSize; i++) {
            for (int j = startCol; j < startCol + sqrtSize; j++) {
                if (row != i && j != col) {
                    arcList.add(new Arc(i, j, row, col)); // this is what's different
                }
            }
        }
        return arcList;
    }
    
    /*
     * Revise the domain of Xi, comparing with Xj
     * use arc to get these domains, then pass the domains in :) - Melva
     */
    private boolean revise(ArrayList<Integer> Xi, ArrayList<Integer> Xj) {
        // boolean revised = false;
        // for (int i = 0; i < Xi.size(); i++) {
        //     if (Xj.contains(Xi.get(i)) && Xi.size() > 1) {
        //         Xi.remove(i);
        //         revised = true;
        //     }
        // }
        // return revised;

        boolean revised = false;
        if (Xj.size() == 1) {
            if (Xi.contains(Xj.get(0))) {
                Xi.remove(Xi.indexOf(Xj.get(0)));
                revised = true;
            }
        }
        return revised;
    }

    private boolean backtrackingNeeded() {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (constraints.get(i).get(j).size() > 1) {
                    return true;
                }
            }
        }
        return false;
    }

    private void assignRemainingValues() {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (board[i][j] == 0) {
                    board[i][j] = constraints.get(i).get(j).get(0);
                }
            }
        }
    }

    // MARK: - Helper methods for backtracking
    private int[] selectUnassignedVariable() {
        // find the cell with the least number of possible values
        int[] location = new int[2];
        int min = 10;
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (board[i][j] == 0 && constraints.get(i).get(j).size() < min) {
                    min = constraints.get(i).get(j).size();
                    location[0] = i;
                    location[1] = j;
                }
            }
        }
        return location;
    }

    private void removeOtherValuesFromDomain(int row, int col, int value) {
        for (int i = 0; i < constraints.get(row).get(col).size(); i++) {
            if (constraints.get(row).get(col).get(i) != value) {
                constraints.get(row).get(col).remove(i);
            }
        }
    }

    /**
     * Checks if the sudoku board is solved with a valid solution
     * @return true if the board is solved with a valid solution, false otherwise
     */
    private boolean isValidSolution() {
        for (int i = 0; i < this.size; i++) {
            for (int j = 0; j < this.size; j++) {
                if (this.board[i][j] != 0 && !isValidGuess(i, j, this.board[i][j])) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean isComplete() {
        for (int i = 0; i < this.size; i++) {
            for (int j = 0; j < this.size; j++) {
                if (this.board[i][j] == 0) {
                    return false;
                }
            }
        }
        return true;
    }

    // there may be an error here with the notUsedInBox method
    private boolean isValidGuess(int row, int col, int num) {
        return (notUsedInRow(row, num) && notUsedInCol(col, num) && notUsedInBox(row - row % this.sqrtSize, col - col % this.sqrtSize, num)) | this.board[row][col] == num;
    }

    private boolean notUsedInRow(int row, int num) {
        for (int i = 0; i < this.size; i++) {
            if (this.board[row][i] == num) {
                return false;
            }
        }
        return true;
    }

    private boolean notUsedInCol(int col, int num) {
        for (int i = 0; i < this.size; i++) {
            if (this.board[i][col] == num) {
                return false;
            }
        }
        return true;
    }

    private boolean notUsedInBox(int startRow, int startCol, int num) {
        for (int i = startRow; i < this.sqrtSize; i++) {
            for (int j = startCol; j < this.sqrtSize; j++) {
                if (this.board[i][j] == num) {
                    return false;
                }
            }
        }
        return true;
    }

    // MARK: - Helper methods for generateBoard
    private void fillSubMatrix(int rowStart, int colStart) {
        int[][] subMatrix = new int[this.sqrtSize][this.sqrtSize];

        int num = 1;
        for (int i = 0; i < this.sqrtSize; i++) {
            for (int j = 0; j < this.sqrtSize; j++) {
                subMatrix[i][j] = num;
                num++;
            }
        }

        subMatrix = shuffleSubMatrix(subMatrix);

        for (int i = rowStart, k = 0; k < this.sqrtSize; i++, k++) {
            for (int j = colStart, l = 0; l < this.sqrtSize; j++, l++) {
                this.board[i][j] = subMatrix[k][l];
            }
        }
    }

    /**
     * A modified version of the Fisher-Yates shuffle algorithm that shuffles a 2D array
     * https://stackoverflow.com/questions/20190110/2d-int-array-shuffle
     * @param a a 2D array of integers representing a sub-matrix of a sudoku board
     */
    private int[][] shuffleSubMatrix(int[][] a) {
        Random random = new Random();
    
        for (int i = a.length - 1; i > 0; i--) {
            for (int j = a[i].length - 1; j > 0; j--) {
                int m = random.nextInt(i + 1);
                int n = random.nextInt(j + 1);
    
                int temp = a[i][j];
                a[i][j] = a[m][n];
                a[m][n] = temp;
            }
        }

        return a;
    }

    // MARK: - Main method for testing
    public static void main(String[] args) {
        // int[] testList = {1,2,3,4,5,6,7,8,9,1,2,3,4,5,6,7};
        // System.out.print(toStringTest(testList));
        int[][] completeBoard = {{1,2,3,6,7,8,9,4,5}, {5,8,4,2,3,9,7,6,1}, {9,6,7,1,4,5,3,2,8},
         {3,7,2,4,6,1,5,8,9},{6,9,1,5,8,3,2,7,4},{4, 5,8,7,9,2,6,1,3}, {8,3,6,9,2,4,1,5,7}, {2,1,9,8,5,7,4,3,6},
        {7, 4,5,3,1,6,8,9,2}};
        int[][] incompleteBoard = {{1,0,3,6,0,8,0,0, 5}, {0,0,4,2,0,9,0,6,1}, {9,6,7,1,4,5,3,2,8},
         {3,7,2,4,0,1,0,8,0},{6,0,0,5,8,0,0,7,4},{4,5,8,7,0,0,0,1,0}, {8,3,6,0,2,0,1,5,7}, {2,0,9,8,0,7,4,3,6},
        {7, 4,5,0,1,0,8,0,0}};
        SudokuSolver complete = new SudokuSolver(completeBoard);
        SudokuSolver test = new SudokuSolver(incompleteBoard);
        System.out.println(test.solve());
        // System.out.println(test.runAC3());
        // System.out.println(test.constraints);
        System.out.println(test);

        // compare the two boards
        for (int i = 0; i < test.size; i++) {
            for (int j = 0; j < test.size; j++) {
                if (test.board[i][j] != completeBoard[i][j]) {
                    System.out.println("Error at row " + i + " and column " + j);
                }
            }
        }
    }
}