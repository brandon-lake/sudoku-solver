
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

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

/**
 *
 * @author Brandon Lake
 */
public class SudokuSolver extends Application {

    private TextField[][] board = new TextField[9][9];      // board of textfields
    private int[][] value = new int[9][9];                  // 2D array of values.  A value of 0 means an empty square
    private Label[][] boardNotes = new Label[9][9];         // board of labels which hold the notes for each square
    private int[][][] notes = new int[9][9][9];             // 3D array to hold the notes for each square.  A value of 0 means that number cannot occur in that square
    private Patterns solver = new Patterns();               // patterns object to call the methods
    private WebDriver d;

    @Override
    public void start(Stage stage) throws Exception {
        Pane root = new Pane();
        Canvas canvas = new Canvas(525, 525);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        Scene scene = new Scene(root, 525, 700); // set the size here
        stage.setTitle("Sudoku Solver!"); // set the window title here
        stage.setScene(scene);

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
        Button createBoard = new Button("Give me a board!");
        Button inputNumbers = new Button("Input Numbers!");
        Button nextStep = new Button("Next step...");
        Button findOnline = new Button("Find me a puzzle\nfrom the internet!");
        Button solveOnline = new Button("Solve web puzzle");

        Button fullSolve = new Button("Solve!");
        Label optional = new Label("Optional buttons:");
        Label directions = new Label("Fill in numbers, click 'Input Numbers!', then click 'Solve!'");

        // Add components to the root
        root.getChildren().addAll(canvas, inputNumbers, createBoard, nextStep, fullSolve, optional, directions, findOnline, solveOnline);

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
        createBoard.relocate(10, 640);
        nextStep.relocate(10, 670);
        findOnline.relocate(135, 620);
        solveOnline.relocate(135, 665);
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
                // add a space between each 3
                if ((j + 1) % 3 == 0) {
                    x += 10;
                }
            }
            y += 55;
            // add a space between each 3
            if ((i + 1) % 3 == 0) {
                y += 11;
            }
        }

        // Add Event Handlers and do final setup
        inputNumbers.setOnAction(this::inputValues);
        createBoard.setOnAction(this::fillBoard);
        nextStep.setOnAction(this::oneStep);
        findOnline.setOnAction(this::webPuzzle);
        fullSolve.setOnAction(this::solvePuzzle);
        solveOnline.setOnAction(this::solveWebPuzzle);

        inputNumbers.requestFocus();

        stage.show();
    }

    /**
     * Opens a chrome window and navigates you to websudoku.com
     *
     * @param ae
     */
    private void webPuzzle(ActionEvent ae) {
        System.setProperty("webdriver.chrome.driver", "resources/chromedriver.exe");
        ChromeOptions options = new ChromeOptions();
        options.addArguments("start-maximized");
        options.addArguments("disable-infobars");
        d = new ChromeDriver();
        d.get("https://www.websudoku.com/?level=2");
        
    }

    /**
     * Pulls the puzzle from websudoku.com, solves it, and submits it back to
     * the web page
     *
     * @param ae
     */
    private void solveWebPuzzle(ActionEvent ae) {
        d.switchTo().frame(0);
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                String number = d.findElement(By.id("f" + j + i)).getAttribute("value");
                if (!number.equals("")) {
                    board[i][j].setText(number);
                }
            }
        }
        
        inputValues(ae);
        solvePuzzle(ae);
        
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                WebElement node = d.findElement(By.id("f" + j + i));
                if (node.getAttribute("value").equals("")) {
                    if (value[i][j] != 0) {
                        node.sendKeys("" + value[i][j]);
                    }
                }
            }
        }
    }

    /**
     * Submits the numbers in the textboxes to memory, fills the 2D array of
     * values so that solving can begin
     *
     * @param ae
     */
    private void inputValues(ActionEvent ae) {
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                // trims all but the first character
                if (board[i][j].getLength() > 1) {
                    board[i][j].setText(board[i][j].getText(0, 1));
                }

                // set the squares as readonly once numbers have been submitted, by bringing the labels to the front
                boardNotes[i][j].toFront();
                try {
                    value[i][j] = Integer.parseInt(board[i][j].getText());
                    board[i][j].setStyle("-fx-background-color: lightgray");
                } catch (NumberFormatException e) {
                    board[i][j].setText("");
                    value[i][j] = 0;
                }
            }
        }
        notes(ae);
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
                    solver.clearRCB(notes, value, boardNotes, i, j);
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
        // no need for if statement on last coded method
        return solver.oneOccurrence(notes, value, board, boardNotes);
    }

    /**
     * Fills the board with a stock Sudoku puzzle, to save the user time when
     * testing
     *
     * @param ae
     */
    private void fillBoard(ActionEvent ae) {
        //int[] numbers = {4, 3, 5, 5, 2, 6, 9, 4, 7, 1, 2, 4, 3, 5, 3, 7, 4, 5, 6, 7, 8, 6, 5, 8, 1, 3, 6, 6, 5, 9, 4, 1, 8, 8, 7, 9};
        //int[] places = {1, 2, 5, 11, 12, 14, 15, 16, 18, 19, 22, 24, 25, 28, 30, 31, 39, 40, 42, 43, 51, 52, 54, 57, 58, 60, 63, 64, 66, 67, 68, 70, 71, 77, 80, 81};

        int[] numbers = {4, 8, 1, 6, 7, 5, 1, 5, 3, 4, 9, 8, 1, 2, 8, 9, 4, 1, 2, 5, 9, 3, 2, 8, 8, 7, 1, 2, 6, 7, 3};
        int[] places = {2, 3, 6, 10, 12, 14, 17, 20, 22, 23, 26, 28, 29, 32, 40, 41, 42, 50, 53, 54, 56, 59, 60, 62, 65, 68, 70, 72, 76, 79, 80};

        for (int i = 0; i < places.length; i++) {
            board[(places[i] - 1) / 9][(places[i] - 1) % 9].setText(numbers[i] + "");
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
        while (oneStep(ae)) {
        }

        // IMPROVE THIS CHECK SOLVED ALGORITHM LATER
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
