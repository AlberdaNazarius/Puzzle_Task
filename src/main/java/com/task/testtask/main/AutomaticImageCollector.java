package com.task.testtask.main;

import com.task.testtask.Union;
import com.task.testtask.components.Puzzle;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

import java.util.*;

import static com.task.testtask.utils.ColorSimilarityUtils.calculateColorSimilarity;

public class AutomaticImageCollector {
  private static final int BORDER_THICKNESS = 5;
  private static final int ACCURACY_FACTOR = 97;
  private static final int NOT_EXISTING_VALUE = -1;

  private final List<List<Union>> rightOrder;
  private final List<Puzzle> puzzles;
  private final int puzzleColumnsCount;
  private final int puzzleRowsCount;
  private final int height;
  private final int width;

  public AutomaticImageCollector(List<Puzzle> puzzles) {
    this.puzzles = puzzles;
    Image firstImage = puzzles.get(0).getImage();
    width = (int) firstImage.getWidth();
    height = (int) firstImage.getHeight();
    puzzleColumnsCount = (int) Math.ceil(Math.sqrt(puzzles.size()));
    puzzleRowsCount = puzzleColumnsCount;
    rightOrder = Arrays.asList(
            new ArrayList<>(),
            new ArrayList<>(),
            new ArrayList<>(),
            new ArrayList<>()
    );
  }

  /**
   * Logic of this method is following:
   * <p>1. Find similarities for each side of the image and do this for each image</p>
   * <p>2. Find top or bottom unions if possible</p>
   * <p>3. Using the found unions find all other unions</p>
   *
   * @return list that contains right order of puzzle locating
   */
  public List<Integer> automaticallyCollectImage() {
    List<Union> unions = new ArrayList<>();
    List<Double> similarityPercentages;
    Image currentImage;
    double maxValue;

    for (int i = 0; i < puzzles.size(); i++) {
      currentImage = puzzles.get(i).getImage();

      System.out.println("Image_" + i);

      similarityPercentages = checkTopSideSimilarities(currentImage);
      maxValue = Collections.max(similarityPercentages);
      int topIndex = maxValue > ACCURACY_FACTOR ? similarityPercentages.indexOf(maxValue) : NOT_EXISTING_VALUE;
      System.out.println(String.format("Top index: %s, Value: %s", topIndex, maxValue));

      similarityPercentages = checkBottomSideSimilarities(currentImage);
      maxValue = Collections.max(similarityPercentages);
      int bottomIndex = maxValue > ACCURACY_FACTOR ? similarityPercentages.indexOf(maxValue) : NOT_EXISTING_VALUE;
      System.out.println(String.format("Bottom index: %s, Value: %s", bottomIndex, maxValue));

      similarityPercentages = checkRightSideSimilarities(currentImage);
      maxValue = Collections.max(similarityPercentages);
      int rightIndex = maxValue > ACCURACY_FACTOR ? similarityPercentages.indexOf(maxValue) : NOT_EXISTING_VALUE;
      System.out.println(String.format("right index: %s, Value: %s", rightIndex, maxValue));

      similarityPercentages = checkLeftSideSimilarities(currentImage);
      maxValue = Collections.max(similarityPercentages);
      int leftIndex = maxValue > ACCURACY_FACTOR ? similarityPercentages.indexOf(maxValue) : NOT_EXISTING_VALUE;
      System.out.println(String.format("left index: %s, Value: %s\n", leftIndex, maxValue));

      Union union = Union.builder()
              .currentUnionIndex(i)
              .topUnionIndex(topIndex)
              .bottomUnionIndex(bottomIndex)
              .leftUnionIndex(leftIndex)
              .rightUnionIndex(rightIndex)
              .build();

      unions.add(union);
    }

    findTopUnions(unions);
    findBottomUnions(unions);
    if (!rightOrder.get(0).isEmpty()) {
      for (int i = 0; i <= puzzleColumnsCount - 2; i++) {
        findMiddleUnions(unions, i + 1,  i);
      }
    }
    else if (!rightOrder.get(puzzleRowsCount-1).isEmpty()) {
      for (int i = puzzleColumnsCount - 1; i > 0; i--) {
        findMiddleUnions(unions, i - 1,  i);
      }
    }

    return convertRightOrderToIntegerList();
  }

  private List<Integer> convertRightOrderToIntegerList() {
    var newRightOrder = new ArrayList<Integer>();
    for (var list : rightOrder) {
      for (var value : list) {
        newRightOrder.add(value.getCurrentUnionIndex());
      }
    }
    return newRightOrder;
  }


  private void findTopUnions(List<Union> unions) {
    var topUnions = unions.stream().filter(x -> x.getTopUnionIndex() == NOT_EXISTING_VALUE).toList();

    for (var union : topUnions) {
      Union previousUnion;
      rightOrder.get(0).add(union);
      var currentUnionToCheck = union;

      for (int i = 0; i < puzzleColumnsCount - 1; i++) {
        previousUnion = currentUnionToCheck;
        int rightUnionIndex = currentUnionToCheck.getRightUnionIndex();

        if (rightUnionIndex == NOT_EXISTING_VALUE) {
          break;
        }

        currentUnionToCheck = unions.get(rightUnionIndex);
        if (currentUnionToCheck.getTopUnionIndex() != NOT_EXISTING_VALUE || previousUnion == currentUnionToCheck) {
          rightOrder.get(0).clear();
          break;
        }
        rightOrder.get(0).add(currentUnionToCheck);
      }

      if (rightOrder.get(0).size() == puzzleColumnsCount) {
        break;
      }
    }
  }

  private void findBottomUnions(List<Union> unions) {
    var bottomUnions = unions.stream().filter(x -> x.getBottomUnionIndex() == -1).toList();
    var lastRow = puzzleRowsCount - 1;

    for (var union : bottomUnions) {
      rightOrder.get(lastRow).add(union);
      var currentUnionToCheck = union;

      for (int i = 0; i < puzzleColumnsCount-1; i++) {
        int rightUnionIndex = currentUnionToCheck.getRightUnionIndex();

        if (rightUnionIndex == NOT_EXISTING_VALUE) {
          break;
        }

        currentUnionToCheck = unions.get(rightUnionIndex);
        if (currentUnionToCheck.getBottomUnionIndex() != NOT_EXISTING_VALUE) {
          rightOrder.get(lastRow).clear();
          break;
        }
        rightOrder.get(lastRow).add(currentUnionToCheck);
      }

      if (rightOrder.get(lastRow).size() == puzzleColumnsCount) {
        break;
      }
    }
  }

  private List<Union> findUnionsThatOnTheSameRow(Set<Union> uniqueElements, List<Union> unions) {
    final var middleRowUnions = new ArrayList<Union>();

    for (var uniqueUnion : uniqueElements) {
      var leftUnionIdx =  uniqueUnion.getLeftUnionIndex();
      var rightUnionIdx = uniqueUnion.getRightUnionIndex();

      var leftUnion = leftUnionIdx != NOT_EXISTING_VALUE ?
              unions.get(leftUnionIdx) :
              new Union(NOT_EXISTING_VALUE);
      var rightUnion = rightUnionIdx != NOT_EXISTING_VALUE ?
              unions.get(rightUnionIdx) :
              new Union(NOT_EXISTING_VALUE);
      if (uniqueElements.contains(leftUnion) || uniqueElements.contains(rightUnion)) {
        middleRowUnions.add(uniqueUnion);
      }
    }
    return middleRowUnions;
  }

  private void findRightOrderForMiddleUnions(List<Union> unions,
                                             List<Union> middleRowUnions,
                                             int rowIndex, int knownRow) {
    final var isKnownTopPos = rowIndex > knownRow;
    for (var union : middleRowUnions) {
      Union previousUnion;
      var currentUnionToCheck = union;
      var similaritiesCount = 1;
      rightOrder.get(rowIndex).add(union);

      for (int i = 0; i < puzzleColumnsCount - 1; i++) {
        previousUnion = currentUnionToCheck;
        int rightUnionIndex = currentUnionToCheck.getRightUnionIndex();

        if (rightUnionIndex == NOT_EXISTING_VALUE) {
          if (isKnownTopPos) {
            currentUnionToCheck = unions.get(rightOrder.get(knownRow).get(i+1).getBottomUnionIndex());
          }
          else {
            currentUnionToCheck = unions.get(rightOrder.get(knownRow).get(i+1).getTopUnionIndex());
          }
        }
        else {
          currentUnionToCheck = unions.get(rightUnionIndex);
        }

        rightOrder.get(rowIndex).add(currentUnionToCheck);

        if (middleRowUnions.contains(currentUnionToCheck) && previousUnion != currentUnionToCheck) {
          similaritiesCount++;
        }
      }
      if (similaritiesCount >= middleRowUnions.size()) {
        break;
      }
      rightOrder.get(rowIndex).clear();
    }
  }

  private void findMiddleUnions(List<Union> unions, int rowIndex, int knownRow) {
    Set<Union> uniqueElements = new HashSet<>();
    var lastRow = puzzleRowsCount - 1;
    rightOrder.get(rowIndex).clear();

    if (knownRow == 0 || rowIndex > knownRow) {
      for (int i = 0; i < puzzleRowsCount; i++) {
        uniqueElements.add(unions.get(rightOrder.get(knownRow).get(i).getBottomUnionIndex()));
      }
    }

    else if (knownRow == lastRow || rowIndex < knownRow) {
      for (int i = 0; i < puzzleRowsCount; i++) {
        uniqueElements.add(unions.get(rightOrder.get(knownRow).get(i).getTopUnionIndex()));
      }
    }

    var middleRowUnions = findUnionsThatOnTheSameRow(uniqueElements, unions);
    findRightOrderForMiddleUnions(unions, middleRowUnions, rowIndex, knownRow);
  }

  /**
   * This method defines logic about how to check two images on similarity.
   *
   * @param currentImage it's an image that currently checked to find its adjacent sides
   * @param currentPuzzleBorders it's an array that contains values of borders defining how many pixels should be taken.
   *                             And it defines borders for current puzzle.
   *                             <p>This array should be initialized like this:</p>
   *                             <p>int[] {xStartBorder, xEndBorder, yStartBorder, yEndBorder}</p>
   * @param otherPuzzleBorders it's an array that contains values of border of all other puzzles except current one
   * @return list that contains all similarity percentages of all puzzles to the current one on a particular side
   */
  private List<Double> toCheck(Image currentImage, int[] currentPuzzleBorders, int[] otherPuzzleBorders) {
    final var similarityPercentages = new ArrayList<Double>();

    double redSum = 0;
    double greenSum = 0;
    double blueSum = 0;

    double redMeanCur;
    double greenMeanCur;
    double blueMeanCur;

    double redMeanToCheck;
    double greenMeanToCheck;
    double blueMeanToCheck;


    for (int y = currentPuzzleBorders[2]; y < currentPuzzleBorders[3]; y++) {
      for (int x = currentPuzzleBorders[0]; x < currentPuzzleBorders[1]; x++) {
        final var curColor = currentImage.getPixelReader().getColor(x, y);
        redSum += curColor.getRed();
        greenSum += curColor.getGreen();
        blueSum += curColor.getBlue();
      }
    }
    redMeanCur = redSum / (height * width);
    greenMeanCur = greenSum / (height * width);
    blueMeanCur = blueSum / (height * width);

    for (var puzzle : puzzles) {
      redSum = 0;
      greenSum = 0;
      blueSum = 0;

      for (int y = otherPuzzleBorders[2]; y < otherPuzzleBorders[3]; y++) {
        for (int x = otherPuzzleBorders[0]; x < otherPuzzleBorders[1]; x++) {
          final var pixelColor = puzzle.getImage().getPixelReader().getColor(x, y);
          redSum += pixelColor.getRed();
          greenSum += pixelColor.getBlue();
          blueSum += pixelColor.getGreen();
        }
      }

      redMeanToCheck = redSum / (height * width);
      greenMeanToCheck = greenSum / (height * width);
      blueMeanToCheck = blueSum / (height * width);

      final var similarityPercentage = calculateColorSimilarity(
              new Color(redMeanCur, blueMeanCur, greenMeanCur, 1),
              new Color(redMeanToCheck, greenMeanToCheck, blueMeanToCheck, 1));

      similarityPercentages.add(similarityPercentage);
    }
    return similarityPercentages;
  }

  private List<Double> checkRightSideSimilarities(Image currentImage) {
    int[] currentPuzzleBorders = {width - BORDER_THICKNESS, width, 0, height};
    int[] otherPuzzleBorders = {0, BORDER_THICKNESS, 0, height};
    return toCheck(currentImage, currentPuzzleBorders, otherPuzzleBorders);
  }
  private List<Double> checkLeftSideSimilarities(Image currentImage) {
    int[] currentPuzzleBorders = {0, BORDER_THICKNESS, 0, height};
    int[] otherPuzzleBorders = {width - BORDER_THICKNESS, width, 0, height};
    return toCheck(currentImage, currentPuzzleBorders, otherPuzzleBorders);
  }
  private List<Double> checkTopSideSimilarities(Image currentImage) {
    int[] currentPuzzleBorders = {0, width, 0, BORDER_THICKNESS};
    int[] otherPuzzleBorders = {0, width, height - BORDER_THICKNESS, height};
    return toCheck(currentImage, currentPuzzleBorders, otherPuzzleBorders);
  }
  private List<Double> checkBottomSideSimilarities(Image currentImage) {
    int[] currentPuzzleBorders = {0, width, height - BORDER_THICKNESS, height};
    int[] otherPuzzleBorders = {0, width, 0, BORDER_THICKNESS};
    return toCheck(currentImage, currentPuzzleBorders, otherPuzzleBorders);
  }
}