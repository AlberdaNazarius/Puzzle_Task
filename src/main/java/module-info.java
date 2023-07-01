module com.task.testtask {
  requires javafx.controls;
  requires javafx.swing;
  requires javafx.fxml;
  requires java.desktop;
  requires lombok;


  opens com.task.testtask to javafx.fxml;
  exports com.task.testtask;
  exports com.task.testtask.enums;
  opens com.task.testtask.enums to javafx.fxml;
}