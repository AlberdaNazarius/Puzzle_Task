package com.task.testtask;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Application extends javafx.application.Application {

  @Override
  public void start(Stage stage) throws IOException {
    FXMLLoader fxmlLoader = new FXMLLoader(Application.class.getResource("main_view.fxml"));
    Scene scene = new Scene(fxmlLoader.load());
    stage.setTitle("Puzzle construct");
    stage.setMinHeight(880);
    stage.setMinWidth(1335);
    stage.setScene(scene);
    stage.show();

    stage.setOnCloseRequest(windowEvent -> {
      Platform.exit();
      System.exit(0);
    });
  }

  public static void main(String[] args) {
    launch();
  }
}