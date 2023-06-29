package com.task.testtask.components;

import javafx.scene.image.Image;
import javafx.scene.layout.Pane;

import java.util.ArrayList;
import java.util.List;

public class ImageConstructionPane {

  private static final int DETECTION_ZONE_REDUCTION_FACTOR = 40;

  private final Pane pane;
  public Image image;

  int startX = 0;
  int startY = 0;

  private List<Puzzle> puzzles;

  public ImageConstructionPane(Pane pane) {
    this.pane = pane;
    puzzles = new ArrayList<>();
  }

  public void divideOnBlocks(int rowCount, int colCount) {
    final var blockWidth = pane.getPrefWidth() / colCount;
    final var blockHeight = pane.getPrefHeight() / rowCount;

    for (int i = 0; i < rowCount; i++) {
      for (int j = 0; j < colCount; j++) {
        final var puzzle = new Puzzle(image, startX, startY);

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

  private void checkOverlappingWithSelectedPuzzle(Puzzle currentPuzzle) {
    var curPuzzleCord = currentPuzzle.calculateGlobalCords();
    
    var factor = DETECTION_ZONE_REDUCTION_FACTOR;

    currentPuzzle.checkIfPuzzlesOverlapListener(curPuzzleCord, ((rec1, rec2) -> {
      boolean widthIsPositive = Math.min(rec1[2] - factor, rec2[2] - factor) >
              Math.max(rec1[0] + factor, rec2[0] + factor);

      boolean heightIsPositive = Math.min(rec1[3] - factor, rec2[3] - factor) >
              Math.max(rec1[1] + factor, rec2[1] + factor);

      if(widthIsPositive && heightIsPositive) {
//        final var d = Puzzle.getSelectedPuzzle().getView().getTransforms();
//        currentPuzzle.getView().getTransforms().;
        currentPuzzle.getView().setImage(Puzzle.getSelectedPuzzle().getView().getImage());
      }
    }));
  }
}
