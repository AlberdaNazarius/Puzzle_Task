package com.task.testtask.components;

import javafx.scene.layout.Pane;
import javafx.scene.transform.Rotate;

import java.util.ArrayList;
import java.util.List;

/**
 * It's a class of a pane that use puzzles to construct an image.
 */
public class ImageConstructionPane {

  private static final int DETECTION_ZONE_REDUCTION_FACTOR = 40;

  private final Pane pane;
  private List<Puzzle> puzzles;

  private int startX = 0;
  private int startY = 0;


  public ImageConstructionPane(Pane pane) {
    this.pane = pane;
    puzzles = new ArrayList<>();
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
   * Changes properties of the puzzle according to the selected puzzle.
   *
   * @param currentPuzzle it's current puzzle
   */
  private void changePropertiesOfOverlappedPuzzle(Puzzle currentPuzzle) {
    final var centerX = currentPuzzle.getCenterX();
    final var centerY = currentPuzzle.getCenterY();
    final var transforms = currentPuzzle.getView().getTransforms();

    transforms.removeAll(transforms);
    currentPuzzle.getView().setImage(Puzzle.getSelectedPuzzle().getView().getImage());
    currentPuzzle.setRotation(Puzzle.getSelectedPuzzle().getRotation());
    transforms.add(new Rotate(currentPuzzle.getRotation(), centerX, centerY));
  }

  /**
   * Checks if two puzzles overlapping namely current puzzle with selected one and also changes property of that puzzle.
   *
   * @param currentPuzzle it's current puzzle
   */
  private void checkOverlappingWithSelectedPuzzle(Puzzle currentPuzzle) {
    var curPuzzleCord = currentPuzzle.calculateGlobalCords();
    var factor = DETECTION_ZONE_REDUCTION_FACTOR;

    currentPuzzle.checkIfPuzzlesOverlapListener(curPuzzleCord, ((rec1, rec2) -> {
      boolean widthIsPositive = Math.min(rec1[2] - factor, rec2[2] - factor) >
              Math.max(rec1[0] + factor, rec2[0] + factor);

      boolean heightIsPositive = Math.min(rec1[3] - factor, rec2[3] - factor) >
              Math.max(rec1[1] + factor, rec2[1] + factor);

      if(widthIsPositive && heightIsPositive) {
        changePropertiesOfOverlappedPuzzle(currentPuzzle);
      }
    }));
  }
}
