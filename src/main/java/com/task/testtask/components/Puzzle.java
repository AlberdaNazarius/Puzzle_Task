package com.task.testtask.components;

import com.task.testtask.enums.Direction;
import com.task.testtask.interfaces.CheckOverlapping;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import lombok.Getter;

/**
 * This class represents puzzle, that used for constructing final image.
 */
@Getter
public class Puzzle implements Cloneable {

  private static final ObjectProperty<Puzzle> selectedPuzzle = new SimpleObjectProperty<>();
  private static final ObjectProperty<Boolean> isReleased = new SimpleObjectProperty<>();
  private static final double DEFAULT_OPACITY = 0.5;

  private final ObjectProperty<Integer> rotation;
  private final ObjectProperty<Boolean> isActive;
  private final ImageView view;

  private double xOffset;
  private double yOffset;
  private double originPosX;
  private double originPosY;
  private Direction direction;


  /**
   * This constructor used to create puzzles that located in the PuzzlePanel.
   *
   * @param image the image of the puzzle
   */
  public Puzzle(Image image) {
    rotation = new SimpleObjectProperty<>();
    isActive = new SimpleObjectProperty<>();
    view = new ImageView(image);

    view.setOpacity(DEFAULT_OPACITY);
    direction = Direction.TOP;
    rotation.set(0);
    isActive.set(true);

    addAllListeners();
  }

  /**
   * It's constructor for puzzles that used in final picture.
   *
   * @param x coordinate of the ImageView
   * @param y coordinate of the ImageView
   */
  public Puzzle(int x, int y) {
    rotation = new SimpleObjectProperty<>();
    isActive = new SimpleObjectProperty<>();
    view = new ImageView();

    view.setX(x);
    view.setY(y);
    direction = Direction.TOP;
    rotation.set(0);
    isActive.set(false);

    addAllListeners();
  }

  /**
   * It's copy constructor
   * @param puzzle {@link Puzzle} which values we want to copy
   */
  public Puzzle(Puzzle puzzle) {
    this.isActive = new SimpleObjectProperty<>();
    this.rotation = new SimpleObjectProperty<>(puzzle.rotation.get());
    this.view = new ImageView(puzzle.view.getImage());
    this.xOffset = puzzle.xOffset;
    this.yOffset = puzzle.yOffset;
    this.originPosX = puzzle.originPosX;
    this.originPosY = puzzle.originPosY;
    this.direction = puzzle.direction;
  }

  public static Puzzle getSelectedPuzzle() {
    return selectedPuzzle.get();
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

  public double getCenterX() {
    if (this == selectedPuzzle.get()) {
      return this.originPosX + this.getWidth() / 2;
    }
    return this.getX() + this.getWidth() / 2;
  }

  public double getCenterY() {
    if (this == selectedPuzzle.get()) {
      return this.originPosY + this.getHeight() / 2;
    }
    return this.getY() + this.getHeight() / 2;
  }

  public int getRotation() {
    return rotation.get();
  }

  public double getX() {
    return view.getX();
  }

  public double getY() {
    return view.getY();
  }

  public Image getImage() {
    return view.getImage();
  }

  public void setWidth(double width) {
    view.setFitWidth(width);
  }

  public void setHeight(double height) {
    view.setFitHeight(height);
  }

  public void makeSelected() {
    selectedPuzzle.set(this);
    this.originPosX = this.getX();
    this.originPosY = this.getY();
  }

  public void setRotation(int value) {
    rotation.set(value);
  }

  public void setActive(boolean value) {
    isActive.set(value);
  }

  public void setImage(Image image) {
    view.imageProperty().set(image);
  }

  /**
   * This method calculates the start and end global coordinates of the current puzzle taking into account direction of
   * that puzzle.
   *
   * @return array with the global coordinates of the puzzle
   */
  public double[] calculateGlobalCords() {
    final var puzzle = this;

    double startX = 0;
    double startY = 0;
    double endX = 0;
    double endY = 0;
    
    double width = puzzle.getWidth();
    double height = puzzle.getHeight();

    var globalPosition = puzzle.getView().localToScene(
            puzzle.getView().getLayoutBounds().getMinX(),
            puzzle.getView().getLayoutBounds().getMinY());

    if (direction == Direction.TOP) {
      startX = globalPosition.getX();
      startY = globalPosition.getY();

      endX = startX + width;
      endY = startY + height;
    }

    if (direction == Direction.BOTTOM) {
      startX = globalPosition.getX() - width;
      startY = globalPosition.getY() - height;

      endX = globalPosition.getX();
      endY = globalPosition.getY();
    }

    if (direction == Direction.LEFT) {
      startX = globalPosition.getX();
      startY = globalPosition.getY() - height;

      endX = globalPosition.getX() + width;
      endY = globalPosition.getY();
    }

    if (direction == Direction.RIGHT) {
      startX = globalPosition.getX() - width;
      startY = globalPosition.getY();

      endX = globalPosition.getX();
      endY = globalPosition.getY() + height;
    }

    return new double[] {startX, startY, endX, endY};
  }

  public void addAllListeners() {
    mousePressedListener();
    dragPuzzleListener();
    returnToOriginPosListener();
    checkPuzzleSelection();
    checkChangeInRotation();
    checkIsActive();
  }

  public void checkIfPuzzlesOverlapListener(double[] rec1, CheckOverlapping checkOverlapping) {
    isReleased.addListener((observableValue, oldValue, newValue) -> {
      if (selectedPuzzle.get() != this) {
        final var rec2 = selectedPuzzle.get().calculateGlobalCords();
        checkOverlapping.checkRectanglesOverlap(rec1, rec2);
        isReleased.set(false);
      }
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
  }

  private void mousePressedListener() {
    view.setOnMousePressed (e -> {
      if (isActive.get()) {
        xOffset = e.getX() - view.getX();
        yOffset = e.getY() - view.getY();

        originPosX = view.getX();
        originPosY = view.getY();

        view.toFront();
        selectedPuzzle.set(this);
      }
    });
  }

  private void checkPuzzleSelection() {
    selectedPuzzle.addListener((observableValue, oldValue, newValue) -> {
      if (isActive.get()) {
        view.setOpacity(DEFAULT_OPACITY);
        selectedPuzzle.get().getView().setOpacity(1);
      }
    });
  }

  private void dragPuzzleListener() {
    view.setOnMouseDragged(e -> {
      if (isActive.get()) {
        view.setX(e.getX() - xOffset);
        view.setY(e.getY() - yOffset);
      }
    });
  }

  private void returnToOriginPosListener() {
    view.setOnMouseReleased(e -> {
      if (isActive.get()) {
        isReleased.set(true);

        view.setX(originPosX);
        view.setY(originPosY);
      }
    });
  }

  private void checkIsActive() {
    isActive.addListener((observableValue, oldValue, newValue) -> {
      if (Boolean.FALSE.equals(newValue)) {
        selectedPuzzle.get().getView().setVisible(false);
      }
      else {
        this.getView().setVisible(true);
      }
    });
  }
}