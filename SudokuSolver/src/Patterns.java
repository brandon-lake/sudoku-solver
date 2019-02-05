
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Brandon Lake
 */
public class Patterns {

    public boolean oneNote(int notes[][][], int value[][], TextField board[][], Label board2[][]) {
        int counter = 0;
        int row = 0;
        int column = 0;
        int number = 0;
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                counter = 0;
                for (int k = 0; k < 9; k++) {
                    if (notes[i][j][k] != 0) {
                        counter++;
                        row = i;
                        column = j;
                        number = notes[i][j][k];
                    }
                }
                if (counter == 1) {
                    board2[i][j].setText("");
                    board[i][j].setText(number + "");
                    value[i][j] = number;
                    //notes[i][j][number - 1] = 0;
                    clearRCB(notes, board2, i, j, number, value);
                    return true;
                }
            }
        }
        return false;
    }

    // clear row column box notes
    public void clearRCB(int notes[][][], Label board2[][], int row, int column, int number, int value[][]) {
        String note;
        // row note
        for (int i = 0; i < 9; i++) {
            notes[row][i][number - 1] = 0;
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

        // column note
        for (int i = 0; i < 9; i++) {
            notes[i][column][number - 1] = 0;
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

        // box note
        int yShift = (row / 3) * 3;
        int xShift = (column / 3) * 3;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                notes[i + yShift][j + xShift][number - 1] = 0;
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
