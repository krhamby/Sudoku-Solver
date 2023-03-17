public class SudokuSolver {
    private int size;
    private int[][] board;

    /**
     * Default constructor for the SodukuSolver class that creates a 9x9 board
     */
    public SudokuSolver() {
        this.size = 9;
        this.board = generateBoard();
    }

    /**
     * Constructor for the SodukuSolver class that takes a size and randomly generates a board
     * @param size the size of the board (i.e., 1 - size numbers per row, column, and box)
     */
    public SudokuSolver(int size) {
        this.size = size;
        this.board = generateBoard();
    }

    /**
     * Constructor for the SodukuSolver class that takes a pre-generated board
     * @param board a square 2D array of integers representing the board
     */
    public SudokuSolver(int[][] board) {
        this.board = board;
        this.size = board.length;
    }

    /**
     * Generates a random board with the size provided in the class constructor
     * Note: the board is not guaranteed to be solvable
     * @return a 2D array of integers representing a sudoku board
     */
    private static int[][] generateBoard() {
        return null;
    }

    /**
     * Solves the sudoku board using the AC3 algorithm with backtracking
     * @return a 2D array of integers representing a solved sudoku board; null if no solution exists
     */
    public int[][] solveUsingAC3WithBacktracking() {
        return null;
    }

    // MARK: - Helper methods for AC3 with backtracking

    /**
     * Checks if the sudoku board is solved with a valid solution
     * @return true if the board is solved with a valid solution, false otherwise
     */
    private static boolean isSolved() {
        return false;
    }
}
