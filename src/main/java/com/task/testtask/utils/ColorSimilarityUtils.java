package com.task.testtask.utils;

import javafx.scene.paint.Color;

public class ColorSimilarityUtils {
  private ColorSimilarityUtils() {

  }

  public static double calculateColorSimilarity(Color color1, Color color2) {
    double[] lab1 = toLABColor(color1);
    double[] lab2 = toLABColor(color2);

    double deltaL = lab2[0] - lab1[0];
    double deltaA = lab2[1] - lab1[1];
    double deltaB = lab2[2] - lab1[2];

    double deltaE = Math.sqrt(deltaL * deltaL + deltaA * deltaA + deltaB * deltaB);

    // Calculate the color similarity as a percentage (100% - deltaE)
    double colorSimilarity = (1.0 - deltaE) * 100.0;

    return colorSimilarity;
  }

  private static double[] toLABColor(Color color) {
    double red = color.getRed() * 255.0;
    double green = color.getGreen() * 255.0;
    double blue = color.getBlue() * 255.0;

    // Convert sRGB to XYZ color space
    double x = 0.4124564 * red + 0.3575761 * green + 0.1804375 * blue;
    double y = 0.2126729 * red + 0.7151522 * green + 0.0721750 * blue;
    double z = 0.0193339 * red + 0.1191920 * green + 0.9503041 * blue;

    // Convert XYZ to Lab color space
    double xRef = 95.047;
    double yRef = 100.000;
    double zRef = 108.883;

    double xRatio = x / xRef;
    double yRatio = y / yRef;
    double zRatio = z / zRef;

    double fx = f(xRatio);
    double fy = f(yRatio);
    double fz = f(zRatio);

    double l = 116.0 * fy - 16.0;
    double a = 500.0 * (fx - fy);
    double b = 200.0 * (fy - fz);

    return new double[]{l, a, b};
  }

  private static double f(double t) {
    double threshold = 6.0 / 29.0;
    return t > threshold ? t * t * t : (t - 4.0 / 29.0) * 108.0 / 841.0;
  }
}
