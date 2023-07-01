package com.task.testtask;

import com.task.testtask.components.Puzzle;
import com.task.testtask.components.panes.ImageConstructionPane;
import com.task.testtask.components.panes.PuzzlePane;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Pane;
import javafx.scene.transform.Rotate;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import static com.task.testtask.Data.readImageFromFile;
import static com.task.testtask.Data.saveImageToFile;

public class MainController implements Initializable {
  @FXML
  private Pane pane;
  @FXML
  private Pane imageConstructionPane;
  @FXML
  private Pane congratulationPane;
  @FXML
  private Pane shadowPane;
  @FXML
  private Label congratulationLabel;

  private static final int ROW_COUNT = 4;
  private static final int COL_COUNT = 4;
  private static final int ROTATION_ANGLE = 90;
  private static final String PATH = "https://cdn.pixabay.com/photo/2015/04/23/22/00/tree-736885_1280.jpg";
  private static final String DIRECTORY_PATH = "src/main/resources/images";

  private Image image;
  private List<Puzzle> puzzles;
  private PuzzlePane puzzlePane;
  private ImageConstructionPane constructionPane;

  @Override
  public void initialize(URL url, ResourceBundle resourceBundle) {
    image = new Image(PATH);
    puzzlePane = new PuzzlePane(pane);
    constructionPane = new ImageConstructionPane(imageConstructionPane);
    defaultSettings();
  }

  private void savePuzzlesToFile() {
    for (int i = 0; i < puzzles.size(); i++) {
      final var curImage = constructionPane.getRightPuzzlesOrder().get(i).getImage();
      final var fileName = String.format("image_%d.png", i+1);
      final var filePath = DIRECTORY_PATH + File.separator + fileName;
      saveImageToFile(curImage, filePath);
    }
  }

  private List<Puzzle> readPuzzlesFromFile() {
    List<Puzzle> puzzlesList = new ArrayList<>();
    for (int i = 0; i < ROW_COUNT*COL_COUNT; i++) {
      String fileName = String.format("image_%d.png", i+1);
      String filePath = DIRECTORY_PATH + File.separator + fileName;
      Image curImage = readImageFromFile(filePath);
      puzzlesList.add(new Puzzle(curImage));
    }
    return puzzlesList;
  }

  private List<Puzzle> cutPuzzlesFromImage(Image image, int rowCount, int colCount) {
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

  private void defaultSettings() {
    congratulationPane.setVisible(false);
    congratulationLabel.setVisible(false);
    shadowPane.setVisible(false);

    puzzles = cutPuzzlesFromImage(image, ROW_COUNT, COL_COUNT);
    constructionPane.divideOnBlocks(ROW_COUNT, COL_COUNT);
    constructionPane.setRightPuzzlesOrder(puzzles);
    puzzlePane.addPuzzles(puzzles);
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

  @FXML
  protected void submit() {
    final var isImageCorrect = constructionPane.checkConstructedImageCorrectness();
    congratulationPane.setVisible(isImageCorrect);
    congratulationLabel.setVisible(isImageCorrect);
    shadowPane.setVisible(isImageCorrect);
  }

  @FXML
  protected void restart() {
    puzzlePane.toDefault();
    constructionPane.toDefault();
    defaultSettings();
  }
}