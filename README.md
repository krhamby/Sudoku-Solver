# Sudoku Solver

Uses AC-3 with backtracking to solve 9x9 Sudoku puzzles.

## Compilation

If not using the provided JAR file, use javac RunSudokuSolver.java

## Usage

java RunSudokuSolver <int: selection from database> or <String: file path>

or, if using the JAR file,

java -jar SudokuSolver.jar <int: selection from database> or <String: file path>

Note: Neither methods require arguments. If none are provided, it will randomly pick a puzzle from the database.

## File Format

A .csv file with two strings of 81 characters, where each character is a digit from 1-9 or a 0 for an empty cell. The first 9 characters are the first row, the next 9 characters are the second row, and so on. The first string is the unsolved board, and the second is the solved board.

An example is as follows:
070000043040009610800634900094052000358460020000800530080070091902100005007040802,679518243543729618821634957794352186358461729216897534485276391962183475137945862

See test.csv for an example if desired
