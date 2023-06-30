package com.task.testtask.components;

import javafx.scene.layout.Pane;

import java.util.List;

/**
 * It's class of a pane that contains of puzzles.
 */
public class PuzzlePane {

  private static final int STARTED_PUZZLE_POS_X = 5;
  private static final int STARTED_PUZZLE_POS_Y = 5;
  private static final int PUZZLES_AMOUNT_ON_ROW = 4;
  private static final int GAP_LENGTH = 10;
  private static final int COEFFICIENT = 40;

  List<Puzzle> puzzles;

  private final Pane pane;

  public PuzzlePane(Pane pane) {
    this.pane = pane;
  }

  /**
   * This method used to locate puzzles in proper order with right indentations.
   *
   * @param puzzles array of puzzles that would be located on the pane
   */
  public void addPuzzles(List<Puzzle> puzzles) {
    this.puzzles = puzzles;

    int startX = STARTED_PUZZLE_POS_X;
    int startY = STARTED_PUZZLE_POS_Y;

    double puzzleWidth =
            (pane.getPrefWidth() / PUZZLES_AMOUNT_ON_ROW) - (PUZZLES_AMOUNT_ON_ROW + 1) * GAP_LENGTH;
    double puzzleHeight =
            pane.getPrefHeight() / Math.ceil((double)puzzles.size() / PUZZLES_AMOUNT_ON_ROW) - GAP_LENGTH;

    int currentPuzzleAmountOnRow = 0;

      for (var puzzle : puzzles) {
      puzzle.getView().setFitWidth(puzzleWidth + COEFFICIENT);
      puzzle.getView().setFitHeight(puzzleHeight);

      puzzle.changePos(startX, startY);
      startX += puzzleWidth + GAP_LENGTH + COEFFICIENT;
      pane.getChildren().add(puzzle.getView());

      if (PUZZLES_AMOUNT_ON_ROW == ++currentPuzzleAmountOnRow) {
        currentPuzzleAmountOnRow = 0;
        startX = STARTED_PUZZLE_POS_X;
        startY += puzzleHeight + GAP_LENGTH;
      }
    }
  }
}
