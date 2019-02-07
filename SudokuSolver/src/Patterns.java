
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
    public boolean oneNote(int notes[][][], int value[][], TextField board[][], Label board2[][]) {
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
                    clearRCB(notes, board2, i, j, value);
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
    public boolean oneOccurrence(int notes[][][], int value[][], TextField board[][], Label board2[][]) {
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
                    clearRCB(notes, board2, row, y, value);
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
                    clearRCB(notes, board2, x, col, value);
                    return true;
                }
            }
        }

        // check by boxes
        for (int yShift = 0; yShift <= 6; yShift += 3) {
            for (int xShift = 0; xShift <= 6; xShift += 3) {
                for (int number = 1; number <= 9; number++) {
                    int count = 0;
                    int x = -1;  // will never use this value
                    int y = -1;  // will never use this value
                    for (int row = 0; row < 3; row++) {
                        for (int col = 0; col < 3; col++) {
                            if (notes[row + yShift][col + xShift][number - 1] == number) {
                                count++;
                                x = row + yShift;
                                y = col + xShift;
                            }
                        }
                    }
                    if (count == 1) {
                        //System.out.println(number + " can only occur in box " + (yShift + (xShift / 3) + 1) + " at (" + (y + 1) + ", " + (x + 1) + ")");
                        value[x][y] = number;
                        notes[x][y] = new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0};
                        board2[x][y].setText("");
                        board[x][y].setText(number + "");
                        clearRCB(notes, board2, x, y, value);
                        return true;
                    }
                }
            }
        }

        return false;
    }

    // clear row column box notes
    public void clearRCB(int notes[][][], Label board2[][], int row, int column, int value[][]) {
        String note;
        // row
        for (int i = 0; i < 9; i++) {
            notes[row][i][value[row][column] - 1] = 0;
            if (value[row][i] == 0) {
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
        // integer division, gives 0 for 0-2, 1 for 3-5, 2 for 6-8, then multiple 3 to determine starting coordinate of that section
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
