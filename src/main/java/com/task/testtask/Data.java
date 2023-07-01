package com.task.testtask;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;

public class Data implements Serializable {
  public static void saveImageToFile(Image image, String filePath) {
    File file = new File(filePath);
    try {
      java.awt.image.BufferedImage bufferedImage = SwingFXUtils.fromFXImage(image, null);
      ImageIO.write(bufferedImage, "png", file);
    } catch (IOException e) {
      throw new RuntimeException("Error saving image: " + e.getMessage());
    }
  }

  public static Image readImageFromFile(String filePath) {
    try {
      File file = new File(filePath);
      return new Image(file.toURI().toString());
    } catch (Exception e) {
      throw new RuntimeException("Error saving image: " + e.getMessage());
    }
  }
}
