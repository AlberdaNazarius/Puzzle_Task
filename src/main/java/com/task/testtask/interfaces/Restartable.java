package com.task.testtask.interfaces;

/**
 * It's an interface that ensures that a class has a method that returns it to its default state.
 */
@FunctionalInterface
public interface Restartable {
  void toDefault();
}
