package org.tensorflow.yolo.model;

/**
 * Model to store the data of a bounding box
 *
 * Created by Zoltan Szabo on 12/17/17.
 * URL: https://github.com/szaza/android-yolo-v2
 */
public class BoundingBox {
    private double x;
    private double y;
    private double width;
    private double height;
    private double confidence;
    private double[] classes;

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getWidth() {
        return width;
    }

    public void setWidth(double width) {
        this.width = width;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public double getConfidence() {
        return confidence;
    }

    public void setConfidence(double confidence) {
        this.confidence = confidence;
    }

    public double[] getClasses() {
        return classes;
    }

    public void setClasses(double[] classes) {
        this.classes = classes;
    }
}
