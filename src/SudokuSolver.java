import java.util.Random;
public class SudokuSolver {
    private int size;
    private int sqrtSize;
    private int[][] board;

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
        return notUsedInRow(row, num) && notUsedInCol(col, num) && notUsedInBox(row - row % this.sqrtSize, col - col % this.sqrtSize, num);
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
        SudokuSolver test = new SudokuSolver();
        System.out.print(test.toString());
    }
}
