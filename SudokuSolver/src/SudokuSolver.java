
import java.util.Arrays;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

/**
 *
 * @author Brandon Lake
 */
public class SudokuSolver extends Application {

    // TODO: Instance Variables for View Components and Model
    // TODO: Private Event Handlers and Helper Methods
    /**
     * This is where you create your components and the model and add event
     * handlers.
     *
     * @param stage The main stage
     * @throws Exception
     */
    private TextField[][] board = new TextField[9][9];      // board of textfields
    private int[][] value = new int[9][9];                  // 2D array of values.  A value of 0 means an empty square
    private Label[][] boardNotes = new Label[9][9];         // board of labels which hold the notes for each square
    private int[][][] notes = new int[9][9][9];             // 3D array to hold the notes for each square.  A value of 0 means that number cannot occur in that square
    private Patterns solver = new Patterns();               // patterns object to call the methods

    @Override
    public void start(Stage stage) throws Exception {
        Pane root = new Pane();
        Canvas canvas = new Canvas(525, 525);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        Scene scene = new Scene(root, 525, 700); // set the size here
        stage.setTitle("Sudoku Solver!"); // set the window title here
        stage.setScene(scene);
        // TODO: Add your GUI-building code here

        // Create the GUI components
        // create board and add to root in same loop
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                board[i][j] = new TextField("");
                boardNotes[i][j] = new Label("");
                root.getChildren().addAll(boardNotes[i][j], board[i][j]);

                board[i][j].setPrefWidth(55);
                board[i][j].setPrefHeight(55);
                board[i][j].setFont(new Font("Arial", 30));

                boardNotes[i][j].setPrefWidth(55);
                boardNotes[i][j].setPrefHeight(55);
                boardNotes[i][j].setFont(new Font("Arial", 15));
                boardNotes[i][j].setStyle("-fx-wrap-text: true; -fx-alignment:  bottom-left");
            }
        }
        Button addNotes = new Button("Fill notes!");
        Button inputNumbers = new Button("Input Numbers!");
        Button nextStep = new Button("Next step...");
        Button quickFill = new Button("Fill the board with\nnumbers for me!");
        
        Button fullSolve = new Button("Solve!");
        Label optional = new Label("Optional buttons:");
        Label directions = new Label("Fill in numbers, click 'Input Numbers!', then click 'Solve!'");

        // Add components to the root
        root.getChildren().addAll(canvas, inputNumbers, addNotes, nextStep, fullSolve, optional, directions, quickFill);

        // Configure the components
        canvas.toBack();
        gc.setFill(Color.WHITE);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        gc.setStroke(Color.BLACK);
        gc.setLineWidth(7);
        gc.strokeLine(175, 9, 175, 519);
        gc.strokeLine(350, 9, 350, 519);
        gc.strokeLine(9, 176, 516, 176);
        gc.strokeLine(9, 352, 516, 352);

        inputNumbers.setPrefWidth(525);
        inputNumbers.setFont(new Font("Times New Roman", 30));
        inputNumbers.relocate(0, 530);

        directions.setPrefWidth(525);
        directions.setFont(new Font("Times New Roman", 20));
        directions.setStyle("-fx-background-color: palegoldenrod; -fx-alignment: center");
        directions.relocate(0, 590);
        optional.relocate(10, 620);
        addNotes.relocate(10, 640);
        nextStep.relocate(10, 670);
        quickFill.relocate(130, 640);
        fullSolve.relocate(280, 630);
        fullSolve.setPrefWidth(200);
        fullSolve.setPrefHeight(50);
        fullSolve.setFont(new Font("Times New Roman", 30));

        // set location of all squares
        int x;
        int y = 5;
        for (int i = 0; i < 9; i++) {
            x = 5;
            for (int j = 0; j < 9; j++) {
                board[i][j].relocate(x, y);
                boardNotes[i][j].relocate(x, y);
                x += 55;
                if ((j + 1) % 3 == 0) {
                    x += 10;
                }
            }
            y += 55;
            if ((i + 1) % 3 == 0) {
                y += 11;
            }
        }

        // Add Event Handlers and do final setup
        inputNumbers.setOnAction(this::inputValues);
        addNotes.setOnAction(this::notes);
        nextStep.setOnAction(this::oneStep);
        quickFill.setOnAction(this::fillBoard);
        fullSolve.setOnAction(this::solvePuzzle);

        inputNumbers.requestFocus();

        stage.show();
    }

    /**
     * Submits the numbers in the textboxes to memory, fills the 2D array of values so that solving can begin
     * 
     * @param ae 
     */
    private void inputValues(ActionEvent ae) {
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                // trims all but the first character
                if (board[i][j].getLength() > 1) {
                    int length = board[0][0].getLength();
                    System.out.println(length);
                    board[i][j].setText(board[i][j].getText(length - 1, length));
                }
                boardNotes[i][j].toFront();
                try {
                    value[i][j] = Integer.parseInt(board[i][j].getText());
                    board[i][j].setStyle("-fx-background-color: lightgray");
                    //board2[i][j].setStyle("-fx-opacity: 0");
                } catch (NumberFormatException e) {
                    board[i][j].setText("");
                    value[i][j] = 0;
                }
            }
        }
    }

    /**
     * Fill in all Sudoku notes based on possible solutions for each square
     * 
     * @param ae 
     */
    private void notes(ActionEvent ae) {
        // 1-9 notes for all empty boxes
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (value[i][j] == 0) {
                    for (int k = 0; k < 9; k++) {
                        notes[i][j][k] = k + 1;
                    }
                }
            }
        }

        // trim notes based on known boxes
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (value[i][j] != 0) {
                    solver.clearRCB(notes, boardNotes, i, j, value[i][j], value);
                }
            }
        }

        // show notes
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (value[i][j] == 0) {
                    String note = "";
                    for (int n = 1; n <= 9; n++) {
                        if (notes[i][j][n - 1] != 0) {
                            note += n + " ";
                        }
                    }
                    boardNotes[i][j].setText(note.trim());
                }
            }
        }
    }

    /**
     * Check all methods in the patterns class for the next step in solving the
     * puzzle
     *
     * @param ae
     * @return true if a move was able to be made, false if no possible moves
     */
    private boolean oneStep(ActionEvent ae) {
        if (solver.oneNote(notes, value, board, boardNotes)) {
            return true;
        }

        //System.out.println("Couldn't solve anything");
        return false;
    }

    /**
     * Fills the board with a stock Sudoku puzzle, to save the user time when testing
     * 
     * @param ae 
     */
    private void fillBoard(ActionEvent ae) {
        int[] numbers = {4, 3, 5, 5, 2, 6, 9, 4, 7, 1, 2, 4, 3, 5, 3, 7, 4, 5, 6, 7, 8, 6, 5, 8, 1, 3, 6, 6, 5, 9, 4, 1, 8, 8, 7, 9};
        int[] places = {1, 2, 5, 11, 12, 14, 15, 16, 18, 19, 22, 24, 25, 28, 30, 31, 39, 40, 42, 43, 51, 52, 54, 57, 58, 60, 63, 64, 66, 67, 68, 70, 71, 77, 80, 81};
        int counter = 0;
        
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (((i * 9) + (j + 1)) == places[counter]) {
                    board[i][j].setText(numbers[counter++] + "");
                }
            }
        }
        inputValues(ae);
    }

    /**
     * Solves the puzzle in one click, rather than step by step
     * 
     * @param ae
     * @return True if solved, false if cannot solve
     */
    private boolean solvePuzzle(ActionEvent ae) {
        notes(ae);
        while (oneStep(ae)) {
        }

        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (value[i][j] == 0) {
                    System.out.println("Couldn't solve the puzzle");
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Make no changes here.
     *
     * @param args unused
     */
    public static void main(String[] args) {
        launch(args);
    }
}
