package com.task.testtask;

import com.task.testtask.components.ImageConstructionPane;
import com.task.testtask.components.Puzzle;
import com.task.testtask.components.PuzzlePane;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Pane;
import javafx.scene.transform.Rotate;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class MainController implements Initializable {
  @FXML
  private Pane pane;
  @FXML
  private Pane imageConstructionPane;

  private static final int ROW_COUNT = 4;
  private static final int COL_COUNT = 4;
  private static final int ROTATION_ANGLE = 90;
  private static final String PATH = "https://static.vecteezy.com/system/resources/previews/009/273/280/" +
          "non_2x/concept-of-loneliness-and-disappointment-in-love-sad-man-sitting-element-of-the-pictur" +
          "e-is-decorated-by-nasa-free-photo.jpg";

  @Override
  public void initialize(URL url, ResourceBundle resourceBundle) {
    final var image = new Image(PATH);
    final var puzzles = cutPuzzlesFromImage(image, ROW_COUNT, COL_COUNT);
    final var puzzlePane = new PuzzlePane(pane);
    final var constructionPane = new ImageConstructionPane(imageConstructionPane);

    puzzlePane.addPuzzles(puzzles);
    constructionPane.divideOnBlocks(ROW_COUNT, COL_COUNT);
  }

  public List<Puzzle> cutPuzzlesFromImage(Image image, int rowCount, int colCount) {
    List<Puzzle> tiles = new ArrayList<>();
    PixelReader pixelReader = image.getPixelReader();

    final var width = (int) image.getWidth();
    final var height = (int) image.getHeight();

    final var tileSizeX = width / colCount;
    final var tileSizeY = height / rowCount;

    for (int y = 0; y < rowCount; y++) {
      for (int x = 0; x < colCount; x++) {
        int startX = x * tileSizeX;
        int startY = y * tileSizeY;
        tiles.add(new Puzzle(new WritableImage(pixelReader, startX, startY, tileSizeX, tileSizeY)));
      }
    }
    return tiles;
  }

  @FXML
  protected void rotatePuzzleToTheLeft() {
    final var selectedPuzzle = Puzzle.getSelectedPuzzle();
    final var selectedPuzzleRotation = selectedPuzzle.getView().getTransforms();

    final var centerX = selectedPuzzle.getCenterX();
    final var centerY = selectedPuzzle.getCenterY();

    selectedPuzzle.setRotation(selectedPuzzle.getRotation() - ROTATION_ANGLE);
    selectedPuzzleRotation.add(new Rotate(-ROTATION_ANGLE, centerX, centerY));
  }

  @FXML
  protected void rotatePuzzleToTheRight() {
    final var selectedPuzzle = Puzzle.getSelectedPuzzle();
    final var selectedPuzzleRotation = selectedPuzzle.getView().getTransforms();

    final var centerX = selectedPuzzle.getCenterX();
    final var centerY = selectedPuzzle.getCenterY();

    selectedPuzzle.setRotation(selectedPuzzle.getRotation() + ROTATION_ANGLE);
    selectedPuzzleRotation.add(new Rotate(ROTATION_ANGLE, centerX, centerY));
  }
}