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
        this.board = generateBoard();
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
            this.board = generateBoard();
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

        if (Math.sqrt(size) != (int) Math.sqrt(size)) {
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
    private int[][] generateBoard() {
        int[][] board = new int[this.size][this.size];

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

        return null;
    }

    @Override
    public String toString() {
        String outString = "";

        for (int i = 0; i < size; i++) {
            if (i % size == 0) {
                for (int j = 0; j <= 2 * (size + sqrtSize); j++) {
                    outString += "-";
                }
                outString += "\n";
            }
            for (int j = 0; j < size; j++) {
                    if (j % size == 0) {
                        outString += "| ";
                    }
                    outString += board[i][j] + " ";
            }
            outString += "|\n";
        }
        for (int j = 0; j <= 2 * (size + sqrtSize); j++) {
            outString += "-";
        }

        return outString;
    }

    // public static String toStringTest(int[] list) {
    //     String outString = "";
    //     int size = 16;
    //     int sqrtSize = 4;

    //     for (int i = 0; i < size; i++) {
    //         if (i % sqrtSize == 0) {
    //             for (int j = 0; j <= 2 * (size + sqrtSize); j++) {
    //                 outString += "-";
    //             }
    //             outString += "\n";
    //         }
    //         for (int j = 0; j < size; j++) {
    //                 if (j % sqrtSize == 0) {
    //                     outString += "| ";
    //                 }
    //                 outString += list[j] + " ";
    //         }
    //         outString += "|\n";
    //     }
    //     for (int j = 0; j <= 2 * (size + sqrtSize); j++) {
    //         outString += "-";
    //     }

    //     return outString;
    // }

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
    private static boolean isSolved() {
        return false;
    }

    // MARK: - Helper methods for generateBoard
    private void fillSubMatrix(int rowStart, int colStart) {
        int num = 1;
        for (int i = rowStart; i < this.sqrtSize; i++) {
            for (int j = colStart; j < this.sqrtSize; j++) {
                this.board[i][j] = num;
                num++;
            }
        }
    }

    /**
     * A modified version of the Fisher-Yates shuffle algorithm that shuffles a 2D array
     * https://stackoverflow.com/questions/20190110/2d-int-array-shuffle
     * @param a
     */
    private int[][] shuffle(int[][] a) {
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

    public static void main(String[] args) {
        // int[] testList = {1,2,3,4,5,6,7,8,9,1,2,3,4,5,6,7};
        // System.out.print(toStringTest(testList));

    }
}
