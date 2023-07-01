package com.task.testtask.interfaces;

/**
 * It's a functional interface to give custom logic to check overlapping.
 *
 */
@FunctionalInterface
public interface CheckOverlapping {
  void checkRectanglesOverlap(double[] rec1, double[] rec2);
}
