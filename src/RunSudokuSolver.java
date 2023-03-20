import java.io.File;
import java.io.FileNotFoundException;
import java.util.Random;
import java.util.Scanner;

public class RunSudokuSolver {
    private static final String FILENAME = "sudoku.csv";
    private static int[][] board;
    private static int[][] solution;

    // for some console fun
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";

    private static void createBoardFromCSVDatabase() throws FileNotFoundException {
        board = new int[9][9];
        solution = new int[9][9];

        File file = new File(FILENAME);
        Scanner scanner = new Scanner(file);

        // create random int less than 9000000
        Random rand = new Random();
        int randomInt = rand.nextInt(9000000);

        for (int i = 0; i < randomInt; i++) {
            scanner.nextLine();
        }
        String line = scanner.nextLine();
        String[] lineArray = line.split(",");
        char[] boardString = lineArray[0].toCharArray();
        char[] solutionString = lineArray[1].toCharArray();

        for (int i = 0, strIdx = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                board[i][j] = Character.getNumericValue(boardString[strIdx]);
                solution[i][j] = Character.getNumericValue(solutionString[strIdx]);
                strIdx++;
            }
        }

        scanner.close();
    }

    private static void createBoardFromCSVDatabase(int selection) throws FileNotFoundException {
        board = new int[9][9];
        solution = new int[9][9];

        File file = new File(FILENAME);
        Scanner scanner = new Scanner(file);

        for (int i = 0; i < selection; i++) {
            scanner.nextLine();
        }
        String line = scanner.nextLine();
        String[] lineArray = line.split(",");
        char[] boardString = lineArray[0].toCharArray();
        char[] solutionString = lineArray[1].toCharArray();

        for (int i = 0, strIdx = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                board[i][j] = Character.getNumericValue(boardString[strIdx]);
                solution[i][j] = Character.getNumericValue(solutionString[strIdx]);
                strIdx++;
            }
        }

        scanner.close();
    }

    private static void createBoardFromFilePath(String filePath) throws FileNotFoundException {
        board = new int[9][9];
        solution = new int[9][9];

        File file = new File(filePath);
        Scanner scanner = new Scanner(file);
        
        String line = scanner.nextLine();
        String[] lineArray = line.split(",");
        char[] boardString = lineArray[0].toCharArray();
        char[] solutionString = lineArray[1].toCharArray();

        for (int i = 0, strIdx = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                board[i][j] = Character.getNumericValue(boardString[strIdx]);
                solution[i][j] = Character.getNumericValue(solutionString[strIdx]);
                strIdx++;
            }
        }

        scanner.close();
    }

    public static void main(String[] args) {
        if (args.length == 1) {
            try {
                int selection = Integer.parseInt(args[0]);

                if (selection > 9000000 || selection < 1) {
                    System.out.println(ANSI_RED + "Selection must be an integer between 1 and 9000000." + ANSI_RESET);
                    System.exit(1);
                }

                createBoardFromCSVDatabase(selection);
            } catch (NumberFormatException e) {
                System.out.println(ANSI_RED + "Could not parse argument to an integer. Trying argument as a local file path..." + ANSI_RESET);
                
                try {
                    createBoardFromFilePath(args[0]);
                } catch (FileNotFoundException e2) {
                    System.out.println(ANSI_RED + "Could not find file at path '" + args[0] + "'. Please ensure that the file exists." + ANSI_RESET);
                    System.exit(1);
                }
            } catch (FileNotFoundException e) {
                System.out.println(ANSI_RED + "Could not find database file. Please ensure that the file 'sudoku.csv' is in the same directory as the program." + ANSI_RESET);
                System.exit(1);
            }
        } else if (args.length > 1) {
            System.out.println(ANSI_RED + "Usage: java RunSudokuSolver <int: selection from database> or <String: file path>" + ANSI_RESET);
            System.out.println("See README.md for more information.");
            System.exit(1);
        } else {
            System.out.println(ANSI_YELLOW + "No arguments provided. Using random puzzle from database." + ANSI_RESET);
            try {
                createBoardFromCSVDatabase();
            } catch (Exception e) {
                System.out.println("Could not find database file. Please ensure that the file 'sudoku.csv' is in the same directory as the program.");
                System.exit(1);
            }
        }

        try {
            SudokuSolver solver = new SudokuSolver(board);

            System.out.println("Unsolved Puzzle:");
            System.out.println(solver);

            solver.solve();

            System.out.println("\nSolved Puzzle:");
            System.out.println(solver);

            if (solver.equals(solution)) {
                System.out.println(ANSI_GREEN + "The solved puzzle matches the provided solution." + ANSI_RESET);
            } else if (!solver.equals(solution) && solver.isValidSolution()) {
                System.out.println(ANSI_YELLOW + "The solved puzzle is valid, but does not match the provided solution." + ANSI_RESET);
                System.out.println("Some (not many) sudoku puzzles have multiple valid solutions.");
            } else if (!solver.isValidSolution()) {
                System.out.println(ANSI_RED + "The solved puzzle is not a valid solution." + ANSI_RESET);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }
    }
}
