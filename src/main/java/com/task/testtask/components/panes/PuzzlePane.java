package com.task.testtask.components.panes;

import com.task.testtask.components.Puzzle;
import com.task.testtask.interfaces.Restartable;
import javafx.scene.layout.Pane;

import java.util.Collections;
import java.util.List;

/**
 * It's class of a pane that contains of puzzles.
 */
public class PuzzlePane implements Restartable {

  private static final int STARTED_PUZZLE_POS_X = 5;
  private static final int STARTED_PUZZLE_POS_Y = 5;
  private static final int GAP_LENGTH = 10;
  private static final int COEFFICIENT = 40;

  private List<Puzzle> puzzles;

  private final Pane pane;

  public PuzzlePane(Pane pane) {
    this.pane = pane;
  }

  public Pane getPane() {
    return pane;
  }

  /**
   * This method used to locate puzzles in proper order with right indentations.
   *
   * @param puzzles array of puzzles that would be located on the pane
   */
  public void addPuzzles(List<Puzzle> puzzles, boolean shuffle) {
    this.puzzles = puzzles;

    int startX = STARTED_PUZZLE_POS_X;
    int startY = STARTED_PUZZLE_POS_Y;

    int puzzlesAmountOnRow = (int)Math.ceil(Math.sqrt(puzzles.size()));

    double puzzleWidth =
            (pane.getPrefWidth() / puzzlesAmountOnRow) - (puzzlesAmountOnRow + 1) * GAP_LENGTH;
    double puzzleHeight =
            pane.getPrefHeight() / Math.ceil((double)puzzles.size() / puzzlesAmountOnRow) - GAP_LENGTH;

    if (shuffle) {
      Collections.shuffle(puzzles);
    }

    int currentPuzzleAmountOnRow = 0;
    for (var puzzle : puzzles) {
      puzzle.getView().setFitWidth(puzzleWidth + COEFFICIENT);
      puzzle.getView().setFitHeight(puzzleHeight);

      puzzle.changePos(startX, startY);
      startX += puzzleWidth + GAP_LENGTH + COEFFICIENT;
      pane.getChildren().add(puzzle.getView());

      if (puzzlesAmountOnRow == ++currentPuzzleAmountOnRow) {
        currentPuzzleAmountOnRow = 0;
        startX = STARTED_PUZZLE_POS_X;
        startY += puzzleHeight + GAP_LENGTH;
      }
    }
  }

  @Override
  public void toDefault() {
    pane.getChildren().clear();
    puzzles.clear();
  }
}
