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

    private static void createBoardFromCSVDatabase() {
        board = new int[9][9];
        solution = new int[9][9];

        try {
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
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    private static void createBoardFromCSVDatabase(int selection) {
        board = new int[9][9];
        solution = new int[9][9];

        try {
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
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static void createBoardFromFilePath(String filePath) {
        board = new int[9][9];
        solution = new int[9][9];

        try {
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
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        if (args.length == 1) {
            try {
                int selection = Integer.parseInt(args[0]);
                createBoardFromCSVDatabase(selection);
            } catch (Exception e) {
                System.out.println("Could not parse argument to an integer. Trying argument as a local file path.");
                createBoardFromFilePath(args[0]);
            }
        } else if (args.length > 1) {
            System.out.println("Usage: java RunSudokuSolver <int: selection from database> or <String: file path>");
            System.out.println("See README.md for more information.");
        } else {
            System.out.println("No arguments provided. Using random puzzle from database.");
            createBoardFromCSVDatabase();
        }

        SudokuSolver solver = new SudokuSolver(board);
        solver.solve();
        System.out.println(solver);
        String result = (solver.equals(solution)) ? ANSI_GREEN + "Yes" + ANSI_RESET : ANSI_RED + "No" + ANSI_RESET;
        System.out.println("Does the solved puzzle match the provided solution? " + result);
    }
}
