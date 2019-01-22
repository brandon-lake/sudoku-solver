
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
    private TextField[][] board = new TextField[9][9];
    private Label[][] board2 = new Label[9][9];
    private int[][] value = new int[9][9];
    private int[][][] notes = new int[9][9][9];
    private Patterns solver = new Patterns();

    @Override
    public void start(Stage stage) throws Exception {
        Pane root = new Pane();
        Canvas canvas = new Canvas(525, 525);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        Scene scene = new Scene(root, 525, 700); // set the size here
        stage.setTitle("Sudoku Solver!"); // set the window title here
        stage.setScene(scene);
        // TODO: Add your GUI-building code here

        // 1. Create the model
        // 2. Create the GUI components
        // create board and add to root in same loop
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                board[i][j] = new TextField("");
                board2[i][j] = new Label("");
                root.getChildren().addAll(board2[i][j], board[i][j]);

                board[i][j].setPrefWidth(55);
                board[i][j].setPrefHeight(55);
                board[i][j].setFont(new Font("Arial", 30));

                board2[i][j].setPrefWidth(55);
                board2[i][j].setPrefHeight(55);
                board2[i][j].setFont(new Font("Arial", 15));
                board2[i][j].setStyle("-fx-wrap-text: true; -fx-alignment:  bottom-left");
            }
        }
        Button fill_notes = new Button("Fill notes!");
        Button input_nums = new Button("Input Numbers!");
        Button next_step = new Button("Next step...");
        Button full_solve = new Button("Solve!");
        
        // 3. Add components to the root
        root.getChildren().addAll(canvas, input_nums, fill_notes, next_step, full_solve);

        // 4. Configure the components (colors, fonts, size, location)
        canvas.toBack();
        gc.setFill(Color.WHITE);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        gc.setStroke(Color.BLACK);
        gc.setLineWidth(8);
        gc.strokeLine(175, 9, 175, 517);
        gc.strokeLine(350, 9, 350, 517);
        gc.strokeLine(10, 175, 515, 175);
        gc.strokeLine(10, 350, 515, 350);

        input_nums.setPrefWidth(525);
        input_nums.setFont(new Font("Times New Roman", 30));
        input_nums.relocate(0, 535);

        fill_notes.relocate(0, 650);
        
        next_step.relocate(200, 650);
        
        full_solve.relocate(400, 650);

        // set location of all squares
        int x;
        int y = 5;
        for (int i = 0; i < 9; i++) {
            x = 5;
            for (int j = 0; j < 9; j++) {
                board[i][j].relocate(x, y);
                board2[i][j].relocate(x, y);
                x += 55;
                if ((j + 1) % 3 == 0) {
                    x += 10;
                }
            }
            y += 55;
            if ((i + 1) % 3 == 0) {
                y += 10;
            }
        }
        // 5. Add Event Handlers and do final setup

        input_nums.setOnAction(this::get_values);
        fill_notes.setOnAction(this::notes);
        next_step.setOnAction(this::one_step);
        full_solve.setOnAction(this::solve_puzzle);
        
        // 6. Show the stage
        stage.show();
    }
    
    // check solution - dont move forwards unless solvable
    // hide input button, show new buttons (full solve or go step by step)
    // create new 2d array values2, solve again step by step if needed

    // eventually get values + notes will be the same button
    private void get_values(ActionEvent ae) {
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                // trims all but the first character
                if (board[i][j].getLength() > 1) {
                    board[i][j].setText(board[i][j].getText(0, 1));
                }
                board2[i][j].toFront();
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

    private void notes(ActionEvent ae) {
        // 1-9 notes for all empty boxes
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (value[i][j] == 0) {
                    for(int k = 0; k < 9; k++) {
                        notes[i][j][k] = k + 1;
                    }
                }
            }
        }

        // trim notes based on known boxes
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (value[i][j] != 0) {
                    solver.clear_rcb(notes, board2, i, j, value[i][j], value);
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
                    board2[i][j].setText(note.trim());
                }
            }
        }
    }

    private void one_step(ActionEvent ae) {
        if(solver.one_note(notes, value, board, board2)) {
            return;
        }
        System.out.println("Couldn't solve anything");
    }

    private void solve_puzzle(ActionEvent ae) {
        boolean solved = false;
        // will add functionality later
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
