// import java.util.Random;
import java.util.ArrayList;
import java.util.LinkedList;

public class SudokuSolver {
    private int size;
    private int sqrtSize;
    private int[][] board;
    private ArrayList<ArrayList<ArrayList<Integer>>> constraints;
    private LinkedList<Arc> queue;

    /**
     * Default constructor for the SodukuSolver class that creates a 9x9 board
     */
    // public SudokuSolver() {
    //     this.size = 9;
    //     this.sqrtSize = 3;
    //     generateBoard();
    // }

    /**
     * Constructor for the SodukuSolver class that takes a size and randomly generates a board
     * @param size the size of the board (i.e., 1 - size numbers per row, column, and box)
     * @throws IllegalArgumentException if the size is not a square
     */
    // public SudokuSolver(int size) {
    //     if (Math.sqrt(size) != (int) Math.sqrt(size)) {
    //         throw new IllegalArgumentException("Size must be a square");
    //     } else {
    //         this.size = size;
    //         this.sqrtSize = (int) Math.sqrt(size);
    //         generateBoard();
    //     }
    // }

    /**
     * Constructor for the SodukuSolver class that takes a pre-generated board
     * @param board a square 2D array of integers representing the board
     * @throws IllegalArgumentException if the board's length is not a square
     */
    public SudokuSolver(int[][] board) throws IllegalArgumentException {
        this.board = new int[board.length][board.length];
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board.length; j++) {
                this.board[i][j] = board[i][j];
            }
        }

        this.size = board.length;

        if (Math.sqrt(size) != (int) Math.sqrt(size) || board[0].length != size) {
            throw new IllegalArgumentException("Size of the board must be a square");
        } else {
            this.sqrtSize = (int) Math.sqrt(size);
        }

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
                    }
                }
            }
        }
    }

    /**
     * Gets the neighboring arcs of every cell 
     * @param i the row of the cell
     * @param j the column of the cell
     * @return an ArrayList of Arcs
     */
    private void initializeQueue() {
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
    // private void generateBoard() {
    //     this.board = new int[this.size][this.size];

    //     // initialize the board to 0
    //     for (int i = 0; i < this.size; i++) {
    //         for (int j = 0; j < this.size; j++) {
    //             board[i][j] = 0;
    //         }
    //     }

    //     // fill the indepent diagonal sub-matrices
    //     for (int i = 0; i < this.size; i += this.sqrtSize) {
    //         fillSubMatrix(i, i);
    //     }
    // }

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
     * Compares the board to a given (ideally solution) board
     * @param solutionBoard
     * @return true if every cell is the same, false otherwise
     */
    public boolean equals(int[][] solutionBoard) {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (board[i][j] != solutionBoard[i][j]) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Attempts to solve the sudoku board using the AC3 algorithm
     * @return true if the queue is empty, false otherwise
     */
    public boolean runAC3() {
        initializeQueue();
        while (!queue.isEmpty()) {
            Arc arc = queue.remove();
            int[] xiLocation = arc.getXiLocation();
            int[] xjLocation = arc.getXjLocation();

            ArrayList<Integer> xiDomList = constraints.get(xiLocation[0]).get(xiLocation[1]);
            ArrayList<Integer> xjDomList = constraints.get(xjLocation[0]).get(xjLocation[1]);

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

    /**
     * Attempts to solve the sudoku board using a backtracking algorithm that utilizes AC-3 to make inferences
     * @return true if the board is solved, false otherwise
     */
    public boolean runBacktrackingWithAC3() {
        if (isValidSolution()) {
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
        int domainSize = domain.size();
        for (int i = 0; i < domainSize; i++) {
            domain = constraints.get(var[0]).get(var[1]); // update domain since we changed memory location
            int value = domain.get(i);
            if (isValidGuess(row, col, value)) {
                board[row][col] = value;
                removeOtherValuesFromDomain(row, col, value);
                boolean inference = runAC3();

                if (inference) {
                    boolean result = runBacktrackingWithAC3();
                    if (result) {
                        return true;
                    }
                }
            }

            // remove changes made to board and constraints on failure
            this.board = new int[size][size];
            for (int j = 0; j < size; j++) {
                for (int k = 0; k < size; k++) {
                    this.board[j][k] = boardCopy[j][k];
                }
            }

            this.constraints = new ArrayList<ArrayList<ArrayList<Integer>>>(size);
            for (int j = 0; j < size; j++) {
                this.constraints.add(new ArrayList<ArrayList<Integer>>(size));
                for (int k = 0; k < size; k++) {
                    this.constraints.get(j).add(new ArrayList<Integer>());
                    for (int l = 0; l < constraintsCopy.get(j).get(k).size(); l++) {
                        this.constraints.get(j).get(k).add(constraintsCopy.get(j).get(k).get(l));
                    }
                }
            }
        }
        return false;
    }

    /**
     * Attempts to solve the sudoku board using a backtracking algorithm that does not utilize AC-3 to make inferences
     * @return true if the board is solved, false otherwise
     */
    public boolean runBacktrackingWithoutAC3() {
        if (isValidSolution()) {
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
        int domainSize = domain.size();
        for (int i = 0; i < domainSize; i++) {
            domain = constraints.get(var[0]).get(var[1]); // update domain since we changed memory location
            int value = domain.get(i);
            if (isValidGuess(row, col, value)) {
                board[row][col] = value;
                removeOtherValuesFromDomain(row, col, value);

                boolean result = runBacktrackingWithoutAC3();
                if (result) {
                    return true;
                }
            }

            // remove changes made to board and constraints on failure
            this.board = new int[size][size];
            for (int j = 0; j < size; j++) {
                for (int k = 0; k < size; k++) {
                    this.board[j][k] = boardCopy[j][k];
                }
            }

            this.constraints = new ArrayList<ArrayList<ArrayList<Integer>>>(size);
            for (int j = 0; j < size; j++) {
                this.constraints.add(new ArrayList<ArrayList<Integer>>(size));
                for (int k = 0; k < size; k++) {
                    this.constraints.get(j).add(new ArrayList<Integer>());
                    for (int l = 0; l < constraintsCopy.get(j).get(k).size(); l++) {
                        this.constraints.get(j).get(k).add(constraintsCopy.get(j).get(k).get(l));
                    }
                }
            }
        }
        return false;
    }

    /**
     * Attempts to solve the sudoku board using a backtracking algorithm that utilizes AC-3 to make inferences.
     * This method will save time by checking if the provided board is valid before attempting to solve it.
     * It also assigns the remaining variables after a successful backtracking run and double checks that the solution is valid.
     * @return true if the board is solved, false otherwise
     */
    public boolean solve() throws Exception {
        if (!isValidStartingBoard()) {
            throw new Exception("Invalid starting board");
        } else {
            boolean result = runBacktrackingWithAC3();
            if (result) {
                assignRemainingVariables();
                if (isValidSolution()) {
                    return true;
                } else {
                    return false;
                }
            } else {
                return false;
            }
        }
    }

    // MARK: - Helper method for solve
    private void assignRemainingVariables() {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (board[i][j] == 0) {
                    ArrayList<Integer> domain = constraints.get(i).get(j);
                    if (domain.size() == 1) {
                        board[i][j] = domain.get(0);
                    }
                }
            }
        }
    }

    // MARK: - Helper methods for AC3

    /**
     * Gets all arcs in the row, column, and box of the given cell
     * @param row
     * @param col
     * @return an ArrayList of Arcs
     */
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
        int startRow = ((int) (row / sqrtSize)) * sqrtSize;
        int startCol = ((int) (col / sqrtSize)) * sqrtSize;

        for (int i = startRow; i < startRow + sqrtSize; i++) {
            for (int j = startCol; j < startCol + sqrtSize; j++) {
                if (row != i && j != col) {
                    arcList.add(new Arc(row, col, i, j));
                }
            }
        }
        return arcList;
    }   
    
    /**
     * Gets all arcs in the row, column, and box of the given cell, but reversed
     * @param row
     * @param col
     * @return an ArrayList of Arcs
     */
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
     * use arc to get these domains, then pass the domains in
     */
    private boolean revise(ArrayList<Integer> Xi, ArrayList<Integer> Xj) {
        boolean revised = false;
        if (Xj.size() == 1) {
            if (Xi.contains(Xj.get(0))) {
                Xi.remove(Xi.indexOf(Xj.get(0)));
                revised = true;
            }
        }
        return revised;
    }

    // MARK: - Helper methods for backtracking

    /**
     * Selects the next unassigned variable with the least number of possible values
     * @return an array of size 2, where the first element is the row and the second element is the column
     */
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

    /**
     * When a guess is made, this method removes all other possible values from the domain of the cell
     * @param row
     * @param col
     * @param value
     */
    private void removeOtherValuesFromDomain(int row, int col, int value) {
        constraints.get(row).get(col).clear();
        constraints.get(row).get(col).add(value);
    }

    /**
     * Checks if the provided starting board is consistent
     * @return
     */
    private boolean isValidStartingBoard() {
        for (int i = 0; i < this.size; i++) {
            for (int j = 0; j < this.size; j++) {
                if (this.board[i][j] != 0) {
                    boolean valid = isValidGuess(i, j, this.board[i][j]);
                    if (!valid) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * Checks if the sudoku board is solved with a valid solution
     * @return true if the board is solved with a valid solution, false otherwise
     */
    protected boolean isValidSolution() {
        for (int i = 0; i < this.size; i++) {
            for (int j = 0; j < this.size; j++) {
                boolean valid = isValidGuess(i, j, this.board[i][j]);
                if (!isComplete() && !valid) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Checks that every variable is assigned a value
     * @return true if every variable is assigned a value (i.e. not 0), false otherwise
     */
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

    private boolean isValidGuess(int row, int col, int num) {
        return (notUsedInRow(row, col, num) && notUsedInCol(row, col, num) && notUsedInBox(row - row % this.sqrtSize, col - col % this.sqrtSize, row, col, num)); //| this.board[row][col] == num;
    }

    private boolean notUsedInRow(int row, int col, int num) {
        for (int i = 0; i < this.size; i++) {
            if (this.board[row][i] == num && i != col) {
                return false;
            }
        }
        return true;
    }

    private boolean notUsedInCol(int row, int col, int num) {
        for (int i = 0; i < this.size; i++) {
            if (this.board[i][col] == num && i != row) {
                return false;
            }
        }
        return true;
    }

    private boolean notUsedInBox(int startRow, int startCol, int rowLoc, int colLoc, int num) {
        for (int i = startRow; i < this.sqrtSize; i++) {
            for (int j = startCol; j < this.sqrtSize; j++) {
                if (this.board[i][j] == num && i != rowLoc && j != colLoc) {
                    return false;
                }
            }
        }
        return true;
    }

    // MARK: - Helper methods for generateBoard

    /**
     * Generates a 3 x 3 submatrix and fills it with the numbers 1-9, then shuffles the numbers
     * @param rowStart
     * @param colStart
     */
    // private void fillSubMatrix(int rowStart, int colStart) {
    //     int[][] subMatrix = new int[this.sqrtSize][this.sqrtSize];

    //     int num = 1;
    //     for (int i = 0; i < this.sqrtSize; i++) {
    //         for (int j = 0; j < this.sqrtSize; j++) {
    //             subMatrix[i][j] = num;
    //             num++;
    //         }
    //     }

    //     subMatrix = shuffleSubMatrix(subMatrix);

    //     for (int i = rowStart, k = 0; k < this.sqrtSize; i++, k++) {
    //         for (int j = colStart, l = 0; l < this.sqrtSize; j++, l++) {
    //             this.board[i][j] = subMatrix[k][l];
    //         }
    //     }
    // }

    /**
     * A modified version of the Fisher-Yates shuffle algorithm that shuffles a 2D array
     * https://stackoverflow.com/questions/20190110/2d-int-array-shuffle
     * @param a a 2D array of integers representing a sub-matrix of a sudoku board
     */
    // private int[][] shuffleSubMatrix(int[][] a) {
    //     Random random = new Random();
    
    //     for (int i = a.length - 1; i > 0; i--) {
    //         for (int j = a[i].length - 1; j > 0; j--) {
    //             int m = random.nextInt(i + 1);
    //             int n = random.nextInt(j + 1);
    
    //             int temp = a[i][j];
    //             a[i][j] = a[m][n];
    //             a[m][n] = temp;
    //         }
    //     }

    //     return a;
    // }

    // MARK: - Main method for testing
    /**
     * Main method for testing
     * Note: The trials in this WILL appear fail because of late changes to the code. The algorithm is still correct, and the results are still valid.
     */
    // public static void main(String[] args) {

    //     // put this into a board: 070000043 040009610 800634900 094052000 358460020
    //     // 000800530 080070091 902100005 007040802
    //     int[][] board1 = { { 0, 7, 0, 0, 0, 0, 0, 4, 3 }, { 0, 4, 0, 0, 0, 9, 6, 1, 0 },
    //             { 8, 0, 0, 6, 3, 4, 9, 0, 0 },
    //             { 0, 9, 4, 0, 5, 2, 0, 0, 0 }, { 3, 5, 8, 4, 6, 0, 0, 2, 0 }, { 0, 0, 0, 8, 0, 0, 5, 3, 0 },
    //             { 0, 8, 0, 0, 7, 0, 0, 9, 1 }, { 9, 0, 2, 1, 0, 0, 0, 0, 5 },
    //             { 0, 0, 7, 0, 4, 0, 8, 0, 2 } };

    //     int[][] board2 = { { 0, 7, 0, 0, 0, 0, 0, 4, 3 }, { 0, 4, 0, 0, 0, 9, 6, 1, 0 },
    //             { 8, 0, 0, 6, 3, 4, 9, 0, 0 },
    //             { 0, 9, 4, 0, 5, 2, 0, 0, 0 }, { 3, 5, 8, 4, 6, 0, 0, 2, 0 }, { 0, 0, 0, 8, 0, 0, 5, 3, 0 },
    //             { 0, 8, 0, 0, 7, 0, 0, 9, 1 }, { 9, 0, 2, 1, 0, 0, 0, 0, 5 },
    //             { 0, 0, 7, 0, 4, 0, 8, 0, 2 } };

    //     // 048301560360008090910670003020000935509010200670020010004002107090100008150834029
    //     int[][] board3 = { { 0, 4, 8, 3, 0, 1, 5, 6, 0 }, { 3, 6, 0, 0, 0, 8, 0, 9, 0 },
    //             { 9, 1, 0, 6, 7, 0, 0, 0, 3 },
    //             { 0, 2, 0, 0, 0, 0, 9, 3, 5 }, { 5, 0, 9, 0, 1, 0, 2, 0, 0 }, { 6, 7, 0, 0, 2, 0, 0, 1, 0 },
    //             { 0, 0, 4, 0, 0, 2, 1, 0, 7 }, { 0, 9, 0, 1, 0, 0, 0, 0, 8 },
    //             { 1, 5, 0, 8, 3, 4, 0, 2, 9 } };

    //     // 008317000 004205109 000040070 327160904 901450000 045700800 030001060 872604000 416070080
    //     int[][] board4 = { { 0, 0, 8, 3, 1, 7, 0, 0, 0 }, { 0, 0, 4, 2, 0, 5, 1, 0, 9 },
    //             { 0, 0, 0, 0, 4, 0, 0, 7, 0 },
    //             { 3, 2, 7, 1, 6, 0, 9, 0, 4 }, { 9, 0, 1, 4, 5, 0, 0, 0, 0 }, { 0, 4, 5, 7, 0, 0, 8, 0, 0 },
    //             { 0, 3, 0, 0, 0, 1, 0, 6, 0 }, { 8, 7, 2, 6, 0, 4, 0, 0, 0 },
    //             { 4, 1, 6, 0, 7, 0, 0, 8, 0 } }; 

    //     // 040890630000136820800740519000467052450020700267010000520003400010280970004050063
    //     int[][] board5 = { { 0, 4, 0, 8, 9, 0, 6, 3, 0 }, { 0, 0, 0, 1, 3, 6, 8, 2, 0 },
    //             { 8, 0, 0, 7, 4, 0, 5, 1, 9 },
    //             { 0, 0, 0, 4, 6, 7, 0, 5, 2 }, { 4, 5, 0, 0, 2, 0, 7, 0, 0 }, { 2, 6, 7, 0, 1, 0, 0, 0, 0 },
    //             { 5, 2, 0, 0, 0, 3, 4, 0, 0 }, { 0, 1, 0, 2, 8, 0, 9, 7, 0 },
    //             { 0, 0, 4, 0, 5, 0, 0, 6, 3 } };
       
    //     long totalAC3Time = 0;
    //     long totalNoAC3Time = 0;
    //     for (int i = 0; i < 100; i++) {
    //         SudokuSolver solver1 = new SudokuSolver(board1);
    //         SudokuSolver solver2 = new SudokuSolver(board1);

    //         long startTime = System.nanoTime();
    //         solver1.runBacktrackingWithAC3();
    //         long endTime = System.nanoTime();
    //         totalAC3Time += (endTime - startTime);

    //         startTime = System.nanoTime();
    //         solver2.runBacktrackingWithoutAC3();
    //         endTime = System.nanoTime();
    //         totalNoAC3Time += (endTime - startTime);
    //     }

    //     System.out.println("Avg backtracking with AC3 time: " + (totalAC3Time / 100) / 1000000.0);
    //     System.out.println("Avg backtracking without AC3 time: " + (totalNoAC3Time / 100) / 1000000.0);

    //     // repeat above with board2
    //     totalAC3Time = 0;
    //     totalNoAC3Time = 0;
    //     for (int i = 0; i < 100; i++) {
    //         SudokuSolver solver1 = new SudokuSolver(board2);
    //         SudokuSolver solver2 = new SudokuSolver(board2);

    //         long startTime = System.nanoTime();
    //         solver1.runBacktrackingWithAC3();
    //         long endTime = System.nanoTime();
    //         totalAC3Time += (endTime - startTime);

    //         startTime = System.nanoTime();
    //         solver2.runBacktrackingWithoutAC3();
    //         endTime = System.nanoTime();
    //         totalNoAC3Time += (endTime - startTime);
    //     }

    //     System.out.println("Avg backtracking with AC3 time: " + (totalAC3Time / 100) / 1000000.0);
    //     System.out.println("Avg backtracking without AC3 time: " + (totalNoAC3Time / 100) / 1000000.0);

    //     // repeat above with board3
    //     totalAC3Time = 0;
    //     totalNoAC3Time = 0;
    //     for (int i = 0; i < 100; i++) {
    //         SudokuSolver solver1 = new SudokuSolver(board3);
    //         SudokuSolver solver2 = new SudokuSolver(board3);

    //         long startTime = System.nanoTime();
    //         solver1.runBacktrackingWithAC3();
    //         long endTime = System.nanoTime();
    //         totalAC3Time += (endTime - startTime);

    //         startTime = System.nanoTime();
    //         solver2.runBacktrackingWithoutAC3();
    //         endTime = System.nanoTime();
    //         totalNoAC3Time += (endTime - startTime);
    //     }

    //     System.out.println("Avg backtracking with AC3 time: " + (totalAC3Time / 100) / 1000000.0);
    //     System.out.println("Avg backtracking without AC3 time: " + (totalNoAC3Time / 100) / 1000000.0);

    //     // repeat above with board4
    //     totalAC3Time = 0;
    //     totalNoAC3Time = 0; 
    //     for (int i = 0; i < 100; i++) {
    //         SudokuSolver solver1 = new SudokuSolver(board4);
    //         SudokuSolver solver2 = new SudokuSolver(board4);

    //         long startTime = System.nanoTime();
    //         solver1.runBacktrackingWithAC3();
    //         long endTime = System.nanoTime();
    //         totalAC3Time += (endTime - startTime);

    //         startTime = System.nanoTime();
    //         solver2.runBacktrackingWithoutAC3();
    //         endTime = System.nanoTime();
    //         totalNoAC3Time += (endTime - startTime);
    //     }

    //     System.out.println("Avg backtracking with AC3 time: " + (totalAC3Time / 100) / 1000000.0);
    //     System.out.println("Avg backtracking without AC3 time: " + (totalNoAC3Time / 100) / 1000000.0);

    //     // repeat above with board5
    //     totalAC3Time = 0;
    //     totalNoAC3Time = 0;
    //     for (int i = 0; i < 100; i++) {
    //         SudokuSolver solver1 = new SudokuSolver(board5);
    //         SudokuSolver solver2 = new SudokuSolver(board5);

    //         long startTime = System.nanoTime();
    //         solver1.runBacktrackingWithAC3();
    //         long endTime = System.nanoTime();
    //         totalAC3Time += (endTime - startTime);

    //         startTime = System.nanoTime();
    //         solver2.runBacktrackingWithoutAC3();
    //         endTime = System.nanoTime();
    //         totalNoAC3Time += (endTime - startTime);
    //     }

    //     System.out.println("Avg backtracking with AC3 time: " + (totalAC3Time / 100) / 1000000.0);
    //     System.out.println("Avg backtracking without AC3 time: " + (totalNoAC3Time / 100) / 1000000.0);
    // }
}