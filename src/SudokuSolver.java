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
            throw new IllegalArgumentException("Size must be a square");
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

        return null;
    }

    @Override
    public String toString() {
        String outString = "";

        for (int i = 0; i < size + sqrtSize; i++) {
            
        }

        return "";
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
    private static boolean isSolved() {
        return false;
    }

    // MARK: - Helper methods for generateBoard
    private int[][] generateSubMatrix() {
        int[][] subMatrix = new int[this.sqrtSize][this.sqrtSize];


        return subMatrix;
    }
}
