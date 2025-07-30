package com.example.mine_sweeper;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.scene.layout.VBox;


import java.util.ArrayList;
import java.util.Objects;


class MyButton extends javafx.scene.control.Button {
    private boolean isBomb;
    private boolean isFlag;
    public MyButton() {
        super();
    }
    public void setFlag(boolean isFlag) {
        this.isFlag = isFlag;
    }
    public boolean getIsFlag() {
        return isFlag;
    }
    public void setIsBomb(boolean var) {
        this.isBomb = var;
    }
    public boolean getIsBomb() {
        return this.isBomb;
    }
}


public class HelloApplication extends Application {
    final int cols = 10;
    final int rows = 8;
    final int btnSize = 50;
    final int WINDOW_WIDTH = btnSize * cols;
    final int WINDOW_HEIGHT = btnSize * rows;
    final int difficulty = 10;
    boolean fillVar = true;
    boolean alreadyPlaced = false;
    int buttonsHeight = 0;
    int buttonsSetToNotVisible = 0;
    int bombCol, bombRow;
    ArrayList<Integer> tempBombs;
    Color colore = Color.DARKGREEN;
    MyButton[][] buttons = new MyButton[rows][cols];
    Label[][] labels = new Label[rows][cols];
    Image flagImage = new Image(Objects.requireNonNull(getClass().getResource("/img/flag.png")).toString());
    Image[] bombs = new Image[8];


    @Override
    public void start(Stage stage) {
        GridPane gridPane = new GridPane();
        gridPane.setAlignment(Pos.CENTER);
        gridPane.setBackground(new Background(new BackgroundFill(Color.SANDYBROWN, CornerRadii.EMPTY, Insets.EMPTY)));
        init_bombs();
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                MyButton button = new MyButton();
                Label label = new Label();
                button.setMinSize(btnSize, btnSize);
                button.setAlignment(Pos.CENTER);
                button.setIsBomb(false);
                button.setFlag(false);
                if (j != 0) {
                    if (fillVar) {
                        colore = Color.GREEN;
                    } else {
                        colore = Color.DARKGREEN;
                    }
                    fillVar = !fillVar;
                }
                button.setBackground(new Background(new BackgroundFill(colore, new CornerRadii(0), Insets.EMPTY)));
                buttonsHeight += btnSize;
                int r = i;
                int c = j;
                button.addEventFilter(MouseEvent.MOUSE_CLICKED,
                        event -> {
                    if (event.getButton() == MouseButton.PRIMARY) {
                        if (!((MyButton)event.getSource()).getIsFlag()) {
                            if (((MyButton) event.getSource()).getIsBomb()) {
                                for (int i1 = 0; i1 < rows; i1++) {
                                    for (int j1 = 0; j1 < cols; j1++) {
                                        buttons[i1][j1].setMouseTransparent(true);
                                    }
                                }
                                revealBombs(r, c);
                            } else {
                                if (!alreadyPlaced) {
                                    placeBombs(buttons, r, c);
                                    alreadyPlaced = true;
                                }
                                reveal(r, c);
                                if (buttonsSetToNotVisible == rows * cols - difficulty) {
                                    win();
                                }
                            }
                        }
                    } else if (event.getButton() == MouseButton.SECONDARY) {
                        placeFlag(r, c);
                    }
                });
                buttons[i][j] = button;
                labels[i][j] = label;
                gridPane.add(button, j, i);
                gridPane.add(label, j, i);
            }
        }

        Scene scene = new Scene(gridPane, WINDOW_WIDTH, WINDOW_HEIGHT);
        stage.setScene(scene);
        stage.setTitle("Campo Minato");
        stage.show();
    }

    private void placeBombs(MyButton[][] buttons, int clickedRow, int clickedCol) {
        int[][] safeSpots = getNeighbours(clickedRow, clickedCol);
        tempBombs = new ArrayList<>();
        for (int i = 0; i < difficulty; i++) {
            bombCol = (int) (Math.random() * cols);
            bombRow = (int) (Math.random() * rows);
            for (int s = 0; s < safeSpots.length; s++) {
                if (safeSpots[s][0] == bombRow && safeSpots[s][1] == bombCol) {
                    bombCol = (int) (Math.random() * cols);
                    bombRow = (int) (Math.random() * rows);
                    s = 0;
                }
            }
            for (int j = 0; j < tempBombs.size() - 1; j += 2) {
                if (bombCol == tempBombs.get(j) && bombRow == tempBombs.get(j + 1)) {
                    bombCol = (int) (Math.random() * cols);
                    bombRow = (int) (Math.random() * rows);
                    j = 0;
                }
            }
            tempBombs.add(bombCol);
            tempBombs.add(bombRow);
            buttons[bombRow][bombCol].setIsBomb(true);
        }
    }

    private void placeFlag(int clickedRow, int clickedCol) {
        MyButton clickedButton = buttons[clickedRow][clickedCol];
        if (clickedButton.getIsFlag()) {
            clickedButton.setGraphic(null);
        } else {
            ImageView flag = new ImageView(flagImage);
            flag.setFitWidth(btnSize);
            flag.setFitHeight(btnSize);
            clickedButton.setGraphic(flag);
        }
        clickedButton.setFlag(!clickedButton.getIsFlag());
    }

    private void revealBombs(int clickedRow, int clickedCol) {
        ArrayList<MyButton> bombButtons = new ArrayList<>();
        int rand = (int)(Math.random() * 8);
        final int bombSize = 50;
        ImageView bomb = new ImageView(bombs[rand]);
        bomb.setFitWidth(bombSize);
        bomb.setFitHeight(bombSize);
        VBox content = new VBox();
        content.setAlignment(Pos.CENTER);
        content.getChildren().addAll(bomb);

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (buttons[i][j].getIsBomb() && !buttons[i][j].getIsFlag()) {
                    bombButtons.add(buttons[i][j]);
                }
            }
        }
        buttons[clickedRow][clickedCol].setGraphic(content);
        bombButtons.remove(buttons[clickedRow][clickedCol]);
        Timeline timeline = new Timeline();
        for (int i = 0; i < bombButtons.size(); i++) {
            MyButton btn = bombButtons.get(i);
            rand = (int)(Math.random() * 8);
            int finalRand = rand;
            bomb = new ImageView(bombs[finalRand]);
            bomb.setFitWidth(bombSize);
            bomb.setFitHeight(bombSize);
            ImageView finalBomb = bomb;
            content = new VBox();
            content.setAlignment(Pos.CENTER);
            content.getChildren().addAll(finalBomb);
            VBox finalContent = content;
            timeline.getKeyFrames().add(new KeyFrame(Duration.millis(500 * (i + 1)), ae -> btn.setGraphic(finalContent)));
        }
        timeline.getKeyFrames().add(new KeyFrame(Duration.millis(500 * (bombButtons.size() + 2)), ae -> {}));
        timeline.play();
    }

    private int[][] getNeighbours(int row, int col) {
        int[][] neighbours = new int[9][2];
        int[][] directions = {
                {-1, -1}, {-1, 0}, {-1, 1},
                {0, -1},  {0, 0},  {0, 1},
                {1, -1}, {1, 0}, {1, 1}
        };
        int index = 0;
        for (int[] dir : directions) {
            int newRow = row + dir[0];
            int newCol = col + dir[1];
            //Controllo di non eseguire controlli FUORI dalla matrice di bottoni
            if (newRow >= 0 && newRow < rows && newCol >= 0 && newCol < cols) {
                neighbours[index] = new int[] {newRow, newCol};
                index++;
            }
        }
        return neighbours;
    }

    private int checkNeighbours(int row, int col) {
        int nBombs = 0;
        // Direzioni attorno al pulsante cliccato
        int[][] directions = {
                {-1, -1}, {-1, 0}, {-1, 1},
                {0, -1},          {0, 1},
                {1, -1}, {1, 0}, {1, 1}
        };
        for (int[] dir : directions) {
            int newRow = row + dir[0];
            int newCol = col + dir[1];
            //Controllo di non eseguire controlli FUORI dalla matrice di bottoni
            if (newRow >= 0 && newRow < rows && newCol >= 0 && newCol < cols) {
                if (buttons[newRow][newCol].getIsBomb()) {
                    nBombs++;
                }
            }
        }
        return nBombs;
    }

    private void reveal(int row, int col) {
        Color[] colors = {Color.BLACK, Color.PINK, Color.RED, Color.GREEN, Color.BLUE, Color.PURPLE, Color.ORANGE, Color.BROWN};
        if (row >= 0 && row < rows && col >= 0 && col < cols && buttons[row][col].isVisible()) {
            buttons[row][col].setVisible(false);
            if (checkNeighbours(row, col) == 0) {
                for (int dx = -1; dx <= 1; dx++) {
                    for (int dy = -1; dy <= 1; dy++) {
                        if (dx != 0 || dy != 0) {
                            reveal(row + dx, col + dy);
                        }
                    }
                }
            }
            else {
                buttons[row][col].setVisible(false);
                labels[row][col].setText(String.valueOf(checkNeighbours(row, col)));
                for (int i = 0; i < colors.length; i++) {
                    if (checkNeighbours(row, col) == i + 1) {
                        labels[row][col].setTextFill(colors[i]);
                    }
                }
                labels[row][col].setFont(new Font("Times New Roman", 35));
                GridPane.setHalignment(labels[row][col], HPos.CENTER);
                GridPane.setValignment(labels[row][col], VPos.CENTER);
            }
            buttonsSetToNotVisible++;
        }
    }

    private void win() {
        revealBombs(0, 0);
    }

    private void init_bombs() {
        for (int i = 0; i < bombs.length; i++) {
            bombs[i] = new Image(Objects.requireNonNull(getClass().getResource("/img/" + i + ".png")).toString());
        }
    }

    public static void main(String[] args) {
        Application.launch(args);
    }
}
