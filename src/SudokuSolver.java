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
     * Attempts to solve the sudoku board using the AC3 algorithm with backtracking
     * @return true if the board is solved with a valid solution, false otherwise
     */
    public boolean solveUsingAC3WithBacktracking() {
        

        return false;
    }

    // MARK: - Helper methods for AC3 with backtracking
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




        for (int i = startRow; i < (startRow * 3) + sqrtSize; i++) {
            for (int j = startCol; j < (startCol * 3) + sqrtSize; j++) {
                if (row != i && j != col) {
                    arcList.add(new Arc(row, col, i, j));
                }
            }
        }
        return arcList;

    }    /*
     * Revisethe domain of Xi, comparing with Xj
     * use arc to get these domains, then pass the domains in :) - Melva
     */
    private boolean revise(ArrayList<Integer> Xi, ArrayList<Integer> Xj) {
        boolean revised = false;
        if (Xj.size() == 1) {
            if (Xi.contains(Xj.get(0))) {
                Xi.remove(Xj.get(0));
                revised = true;
            }
        }
        return revised;
    }
    /**
     * Checks if the sudoku board is solved with a valid solution
     * @return true if the board is solved with a valid solution, false otherwise
     */
    private boolean isValidSolution() {
        for (int i = 0; i < this.size; i++) {
            for (int j = 0; j < this.size; j++) {
                if (this.board[i][j] != 0 && !isValidGuess(j, i, this.board[i][j])) {
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
        int[][] completeBoard = {{1,2,3,6,0,8,9,4,5}, {5,8,4,2,3,0,7,6,1}, {9,6,7,1,4,5,3,2,8},
         {3,7,2,4,6,1,5,8,9},{6,9,1,5,8,3,2,7,4},{4, 5,8,7,9,2,6,1,3}, {8,3,6,9,2,4,1,5,7}, {2,1,9,8,5,7,4,3,6},
        {7, 4,5,3,1,6,8,9,2}};
        SudokuSolver test = new SudokuSolver(completeBoard);
        System.out.print(test.toString());
        System.out.println(test.constraints);
    }
}