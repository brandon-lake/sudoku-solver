
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

/**
 *
 * @author Brandon Lake
 */
public class Patterns {

    /**
     * Checks to see if a given square can only possibly be one number, as it
     * only has one number in its note
     *
     * @param notes The 3D array containing the notes for each square
     * @param value The 2D array of numeric values associated with the board
     * @param board The 2D array of textboxes which the values are written in
     * @param board2 The 2D array of labels which the notes are written in
     * @return true if this method solves a square, false if not
     */
    public boolean oneNote(int[][][] notes, int[][] value, TextField[][] board, Label[][] board2) {
        int counter;
        int number = 0;
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                counter = 0;
                for (int k = 0; k < 9; k++) {
                    if (notes[i][j][k] != 0) {
                        counter++;
                        number = notes[i][j][k];
                    }
                }
                if (counter == 1) {
                    value[i][j] = number;
                    board2[i][j].setText("");
                    board[i][j].setText(number + "");
                    clearRCB(notes, value, board2, i, j);
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Checks to see if a given number only occurs as a note once within a given
     * row, column, or box
     *
     * @param notes The 3D array containing the notes for each square
     * @param value The 2D array of numeric values associated with the board
     * @param board The 2D array of textboxes which the values are written in
     * @param board2 The 2D array of labels which the notes are written in
     * @return true if this method solves a square, false if not
     */
    public boolean oneOccurrence(int[][][] notes, int[][] value, TextField[][] board, Label[][] board2) {
        // check by rows
        for (int row = 0; row < 9; row++) {
            for (int number = 1; number <= 9; number++) {
                int count = 0;
                int y = -1;  // will never use this value
                for (int col = 0; col < 9; col++) {
                    if (notes[row][col][number - 1] == number) {
                        count++;
                        y = col;
                    }
                }
                if (count == 1) {
                    //System.out.println(number + " can only occur in row " + (row + 1) + " at (" + (row + 1) + ", " + (y + 1) + ")");
                    value[row][y] = number;
                    notes[row][y] = new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0};
                    board2[row][y].setText("");
                    board[row][y].setText(number + "");
                    clearRCB(notes, value, board2, row, y);
                    return true;
                }
            }
        }

        // check by columns
        for (int col = 0; col < 9; col++) {
            for (int number = 1; number <= 9; number++) {
                int count = 0;
                int x = -1;  // will never use this value
                for (int row = 0; row < 9; row++) {
                    if (notes[row][col][number - 1] == number) {
                        count++;
                        x = row;
                    }
                }
                if (count == 1) {
                    //System.out.println(number + " can only occur in column " + (col + 1) + " at (" + (x + 1) + ", " + (col + 1) + ")");
                    value[x][col] = number;
                    notes[x][col] = new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0};
                    board2[x][col].setText("");
                    board[x][col].setText(number + "");
                    clearRCB(notes, value, board2, x, col);
                    return true;
                }
            }
        }

        // check by boxes
        int[] vPos;     // contains the vertical position for squares that share notes in a box
        int[] hPos;     // contains the horizontal position for squares that share notes in a box
        for (int yShift = 0; yShift <= 6; yShift += 3) {
            for (int xShift = 0; xShift <= 6; xShift += 3) {
                for (int number = 1; number <= 9; number++) {
                    int count = 0;
                    vPos = new int[9];
                    hPos = new int[9];
                    for (int row = 0; row < 3; row++) {
                        for (int col = 0; col < 3; col++) {
                            if (notes[row + yShift][col + xShift][number - 1] == number) {
                                vPos[count] = row + yShift;
                                hPos[count] = col + xShift;
                                count++;
                            }
                        }
                    }
                    if (count == 1) {
                        //System.out.println(number + " can only occur in box " + (yShift + (xShift / 3) + 1) + " at (" + (y + 1) + ", " + (x + 1) + ")");
                        value[vPos[0]][hPos[0]] = number;
                        notes[vPos[0]][hPos[0]] = new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0};
                        board2[vPos[0]][hPos[0]].setText("");
                        board[vPos[0]][hPos[0]].setText(number + "");
                        clearRCB(notes, value, board2, vPos[0], hPos[0]);
                        return true;
                    }
                    if (count > 1 && count < 4) {
                        boolean rowLine = true;
                        boolean colLine = true;
                        for (int i = 1; i < count; i++) {
                            if (vPos[i] != vPos[i - 1]) {
                                rowLine = false;
                            }
                            if (hPos[i] != hPos[i - 1]) {
                                colLine = false;
                            }
                        }
                        if (rowLine) {
                            int[] targetColumns = new int[6];
                            count = 0;
                            for (int i = 0; i < 9; i++) {
                                if (i < xShift || i > (xShift + 2)) {
                                    if (notes[vPos[0]][i][number - 1] == number) {
                                        targetColumns[count++] = i;
                                    }
                                }
                            }
                            if (count > 0) {
                                candidateRow(notes, board2, number, vPos[0], targetColumns);
                                return true;
                            }
                        } else if (colLine) {
                            int[] targetRows = new int[6];
                            count = 0;
                            for (int i = 0; i < 9; i++) {
                                if (i < yShift || i > (yShift + 2)) {
                                    if (notes[i][hPos[0]][number - 1] == number) {
                                        targetRows[count++] = i;
                                    }
                                }
                            }
                            if (count > 0) {
                                candidateCol(notes, board2, number, hPos[0], targetRows);
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    /**
     * Method to clear the notes for a certain number from a row, except for the box where the squares
     * that contain notes for that number form a line
     * 
     * @param notes The 3D array containing the notes for each square
     * @param board2 The 2D array of labels which the notes are written in
     * @param number The number to clear notes for
     * @param row The row to clear notes from
     * @param columns The columns go clear notes from
     */
    public void candidateRow(int[][][] notes, Label[][] board2, int number, int row, int[] columns) {
        for (int col : columns) {
            String note = "";
            notes[row][col][number - 1] = 0;
            for (int num : notes[row][col]) {
                if (num != 0)
                    note += num + " ";
            }
            board2[row][col].setText(note.trim());
        }
    }
    
    /**
     * Method to clear the notes for a certain number from a column, except for the box where the squares
     * that contain notes for that number form a line
     * 
     * @param notes The 3D array containing the notes for each square
     * @param board2 The 2D array of labels which the notes are written in
     * @param number The number to clear notes for
     * @param col The column to clear the notes of a given number from
     * @param rows The rows to clear notes from
     */
    public void candidateCol(int[][][] notes, Label[][] board2, int number, int col, int[] rows) {
        for (int row : rows) {
            String note = "";
            notes[row][col][number - 1] = 0;
            for (int num : notes[row][col]) {
                if (num != 0)
                    note += num + " ";
            }
            board2[row][col].setText(note.trim());
        }
    }
    
    // clear row column box notes
    public void clearRCB(int[][][] notes, int[][] value, Label[][] board2, int row, int column) {
        String note;
        // row
        for (int i = 0; i < 9; i++) {
            if (value[row][i] == 0) {
                notes[row][i][value[row][column] - 1] = 0;
                note = "";
                for (int n = 1; n <= 9; n++) {
                    if (notes[row][i][n - 1] != 0) {
                        note += n + " ";
                    }
                }
                board2[row][i].setText(note.trim());
            }
        }

        // column
        for (int i = 0; i < 9; i++) {
            notes[i][column][value[row][column] - 1] = 0;
            if (value[i][column] == 0) {
                note = "";
                for (int n = 1; n <= 9; n++) {
                    if (notes[i][column][n - 1] != 0) {
                        note += n + " ";
                    }
                }
                board2[i][column].setText(note.trim());
            }
        }

        // box
        int yShift = (row / 3) * 3;
        int xShift = (column / 3) * 3;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                notes[i + yShift][j + xShift][value[row][column] - 1] = 0;
                if (value[i + yShift][j + xShift] == 0) {
                    note = "";
                    for (int n = 1; n <= 9; n++) {
                        if (notes[i + yShift][j + xShift][n - 1] != 0) {
                            note += n + " ";
                        }
                    }
                    board2[i + yShift][j + xShift].setText(note.trim());
                }
            }
        }
    }
}
