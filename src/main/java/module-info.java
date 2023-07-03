module com.task.testtask {
  requires javafx.controls;
  requires javafx.swing;
  requires javafx.fxml;
  requires java.desktop;
  requires lombok;

  exports com.task.testtask.enums;
  exports com.task.testtask.utils;
  exports com.task.testtask.main;
  exports com.task.testtask.saving;
  opens com.task.testtask to javafx.fxml;
  opens com.task.testtask.enums to javafx.fxml;
  opens com.task.testtask.utils to javafx.fxml;
  opens com.task.testtask.main to javafx.fxml;
  opens com.task.testtask.saving to javafx.fxml;
  exports com.task.testtask;
}