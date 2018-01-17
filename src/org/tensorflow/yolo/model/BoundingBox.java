/* Copyright 2018 The Android YOLOv2 sample application Authors.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
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
