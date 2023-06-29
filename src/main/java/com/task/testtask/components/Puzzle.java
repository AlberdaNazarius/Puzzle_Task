package com.task.testtask.components;

import com.task.testtask.enums.Direction;
import com.task.testtask.interfaces.CheckOverlapping;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import lombok.Getter;

import java.util.Arrays;

/**
 * This class represents puzzle, that used for constructing final image.
 */
@Getter
public class Puzzle {

  private static final ObjectProperty<Puzzle> selectedPuzzle = new SimpleObjectProperty<>();
  private static final ObjectProperty<Boolean> isReleased = new SimpleObjectProperty<>();

  private static final double DEFAULT_OPACITY = 0.5;

  private final ObjectProperty<Integer> rotation = new SimpleObjectProperty<>();
  private final ImageView view;

  private Direction direction;

  private double xOffset;
  private double yOffset;
  private double originPosX;
  private double originPosY;

  /**
   * This constructor used to create puzzles that located in the PuzzlePanel.
   *
   * @param image the image of the puzzle
   */
  public Puzzle(Image image) {
    view = new ImageView(image);
    view.setOpacity(DEFAULT_OPACITY);
    direction = Direction.TOP;
    rotation.set(0);

    mousePressedListener();
    dragPuzzleListener();
    returnToOriginPosListener();
    checkPuzzleSelection();
    checkChangeInRotation();
  }

  /**
   * It's constructor for puzzles that used in final picture.
   *
   * @param image
   * @param x coordinate of the ImageView
   * @param y coordinate of the ImageView
   */
  public Puzzle(Image image, int x, int y) {
    // TODO remove next line
    view = new ImageView(image);

    view.setX(x);
    view.setY(y);
    direction = Direction.TOP;
    rotation.set(0);
    checkChangeInRotation();
  }

  public static Puzzle getSelectedPuzzle() {
    return selectedPuzzle.get();
  }

  public void setImage(Image image) {
    view.imageProperty().set(image);
  }

  public void changePos(int x, int y) {
    view.setX(x);
    view.setY(y);
  }

  public double getWidth() {
    return view.getFitWidth();
  }

  public double getHeight() {
    return view.getFitHeight();
  }

  public double getX() {
    return view.getX();
  }

  public double getY() {
    return view.getY();
  }

  public void setWidth(double width) {
    view.setFitWidth(width);
  }

  public void setHeight(double height) {
    view.setFitHeight(height);
  }

  /**
   * This method calculates the start and end global coordinates of the current puzzle.
   *
   * @return array with the global coordinates of the puzzle
   */
  public double[] calculateGlobalCords() {
    final var puzzle = this;

    double startX = 0;
    double startY = 0;
    double endX = 0;
    double endY = 0;

    var globalPosition = puzzle.getView().localToScene(
            puzzle.getView().getLayoutBounds().getMinX(),
            puzzle.getView().getLayoutBounds().getMinY());

    if (direction == Direction.TOP) {
      startX = globalPosition.getX();
      startY = globalPosition.getY();

      endX = startX + puzzle.getWidth();
      endY = startY + puzzle.getHeight();
    }

    if (direction == Direction.BOTTOM) {
      startX = globalPosition.getX() - puzzle.getWidth();
      startY = globalPosition.getY() - puzzle.getHeight();

      endX = globalPosition.getX();
      endY = globalPosition.getY();
    }

    if (direction == Direction.LEFT || direction == Direction.RIGHT) {
      startX = globalPosition.getX();
      startY = globalPosition.getY();
    }

    return new double[] {startX, startY, endX, endY};
  }

  public void checkIfPuzzlesOverlapListener(double[] rec1, CheckOverlapping checkOverlapping) {
    isReleased.addListener((observableValue, oldValue, newValue) -> {
      final var rec2 = selectedPuzzle.get().calculateGlobalCords();
      checkOverlapping.checkRectanglesOverlap(rec1, rec2);
      isReleased.set(false);
    });
  }

  private void checkChangeInRotation() {
    rotation.addListener((observableValue, oldValue, newValue) -> {
      if (Math.abs(rotation.get()) >= 360) {
        rotation.set(0);
      }
      updateDirection(rotation.get());
    });
  }

  private void updateDirection(double angle) {
    if (angle == 0) {
      direction = Direction.TOP;
    } else if (angle == 90 || angle == -270) {
      direction = Direction.RIGHT;
    } else if (angle == -180 || angle == 180) {
      direction = Direction.BOTTOM;
    } else if (angle == -90 || angle == 270) {
      direction = Direction.LEFT;
    }
    System.out.println("Current Direction: " + direction);
  }

  private void mousePressedListener() {
    view.setOnMousePressed (e -> {
      xOffset = e.getX() - view.getX();
      yOffset = e.getY() - view.getY();

      originPosX = view.getX();
      originPosY = view.getY();

      view.toFront();
      selectedPuzzle.set(this);
    });
  }

  private void checkPuzzleSelection() {
    selectedPuzzle.addListener((observableValue, oldValue, newValue) -> {
      view.setOpacity(DEFAULT_OPACITY);
      selectedPuzzle.get().getView().setOpacity(1);
    });
  }

  private void dragPuzzleListener() {
    view.setOnMouseDragged(e -> {
      view.setX(e.getX() - xOffset);
      view.setY(e.getY() - yOffset);
    });
  }

  private void returnToOriginPosListener() {
    view.setOnMouseReleased(e -> {
      isReleased.set(true);

      view.setX(originPosX);
      view.setY(originPosY);
    });
  }
}
