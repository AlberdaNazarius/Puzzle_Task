package com.task.testtask.components.panes;

import com.task.testtask.components.Puzzle;
import com.task.testtask.interfaces.Restartable;
import javafx.scene.layout.Pane;
import javafx.scene.transform.Rotate;

import java.util.ArrayList;
import java.util.List;

/**
 * It's a class of a pane that use puzzles to construct an image.
 */
public class ImageConstructionPane implements Restartable {

  private static final double DETECTION_ZONE_REDUCTION_FACTOR = 3.3;

  private double reductionFactorX;
  private double reductionFactorY;

  private final Pane pane;
  private final List<Puzzle> rightPuzzlesOrder;
  private final List<Puzzle> puzzles;

  private int startX = 0;
  private int startY = 0;

  public ImageConstructionPane(Pane pane) {
    this.pane = pane;
    puzzles = new ArrayList<>();
    rightPuzzlesOrder = new ArrayList<>();
  }

  public void setRightPuzzlesOrder(List<Puzzle> puzzlesOrder) {
    for (var puzzle : puzzlesOrder) {
      rightPuzzlesOrder.add(new Puzzle(puzzle));
    }
  }

  public List<Puzzle> getRightPuzzlesOrder() {
    return rightPuzzlesOrder;
  }

  public List<Puzzle> getPuzzles() {
    return puzzles;
  }

  public boolean checkConstructedImageCorrectness() {
    for (int i = 0; i < rightPuzzlesOrder.size(); i++) {
      var isRightPicture = rightPuzzlesOrder.get(i).getImage() == puzzles.get(i).getImage();
      var isRightRotation = rightPuzzlesOrder.get(i).getRotation() == puzzles.get(i).getRotation();
      if (!isRightPicture || !isRightRotation) {
        return false;
      }
    }
    return true;
  }

  /**
   * This method divides pane on blocks where each block represent some place for a puzzle.
   *
   * @param rowCount it's row count
   * @param colCount it's column count
   */
  public void divideOnBlocks(int rowCount, int colCount) {
    final var blockWidth = pane.getPrefWidth() / colCount;
    final var blockHeight = pane.getPrefHeight() / rowCount;

    reductionFactorX = blockWidth / DETECTION_ZONE_REDUCTION_FACTOR;
    reductionFactorY = blockHeight / DETECTION_ZONE_REDUCTION_FACTOR;


    for (int i = 0; i < rowCount; i++) {
      for (int j = 0; j < colCount; j++) {
        final var puzzle = new Puzzle(startX, startY);

        puzzle.setWidth(blockWidth);
        puzzle.setHeight(blockHeight);
        pane.getChildren().add(puzzle.getView());

        puzzles.add(puzzle);
        startX += blockWidth;

        checkOverlappingWithSelectedPuzzle(puzzle);
      }
      startX = 0;
      startY += blockHeight;
    }
  }

  /**
   * Changes properties of the puzzles according to the selected puzzle.
   *
   * @param currentPuzzle it's current puzzle
   */
  private void changePropertiesOfOverlappedPuzzles(Puzzle currentPuzzle) {
    final var curCenterX = currentPuzzle.getCenterX();
    final var curCenterY = currentPuzzle.getCenterY();
    final var curTransforms = currentPuzzle.getView().getTransforms();

    final var selectedPuzzle = Puzzle.getSelectedPuzzle();
    final var selectedCenterX = selectedPuzzle.getCenterX();
    final var selectedCenterY = selectedPuzzle.getCenterY();
    final var selectedTransforms = selectedPuzzle.getView().getTransforms();

    var tempImage = currentPuzzle.getImage();
    var tempRotation = currentPuzzle.getRotation();


    curTransforms.removeAll(curTransforms);
    selectedTransforms.removeAll(selectedTransforms);

    currentPuzzle.setImage(selectedPuzzle.getImage());
    selectedPuzzle.setImage(tempImage);

    currentPuzzle.setRotation(selectedPuzzle.getRotation());
    curTransforms.add(new Rotate(currentPuzzle.getRotation(), curCenterX, curCenterY));

    selectedPuzzle.setRotation(tempRotation);
    selectedTransforms.add(new Rotate(selectedPuzzle.getRotation(), selectedCenterX, selectedCenterY));

    currentPuzzle.setActive(true);
    currentPuzzle.makeSelected();
  }

  /**
   * Checks if two puzzles overlapping namely current puzzle with selected one and also changes property of that puzzle.
   *
   * @param currentPuzzle it's current puzzle
   */
  private void checkOverlappingWithSelectedPuzzle(Puzzle currentPuzzle) {
    var curPuzzleCord = currentPuzzle.calculateGlobalCords();

    currentPuzzle.checkIfPuzzlesOverlapListener(curPuzzleCord, ((rec1, rec2) -> {
      double factorX;
      double factorY;
      if (pane == Puzzle.getSelectedPuzzle().getView().getParent()) {
        factorX = reductionFactorX;
        factorY = reductionFactorY;
      }
      else {
        factorX = 0;
        factorY = 0;
      }
      boolean widthIsPositive = Math.min(rec1[2] - factorX, rec2[2] - factorX) >
              Math.max(rec1[0] + factorX, rec2[0] + factorX);

      boolean heightIsPositive = Math.min(rec1[3] - factorY, rec2[3] - factorY) >
              Math.max(rec1[1] + factorY, rec2[1] + factorY);

      if(widthIsPositive && heightIsPositive) {
        changePropertiesOfOverlappedPuzzles(currentPuzzle);
      }
    }));
  }

  @Override
  public void toDefault() {
    startX = 0;
    startY = 0;
    for (var puzzle : puzzles) {
      puzzle.removeCheckOverlapListener();
    }
    pane.getChildren().clear();
    puzzles.clear();
    rightPuzzlesOrder.clear();
  }
}