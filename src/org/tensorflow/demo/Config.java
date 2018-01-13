/*
 * Copyright 2018 The Android YOLOv2 sample application Authors.
 *
 *     This file is part of Android YOLOv2 sample application.
 * Android YOLOv2 is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Android YOLOv2 is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Android YOLOv2. If not, see <http://www.gnu.org/licenses/>.
 */
package org.tensorflow.demo;

/**
 * Created by Zoltan Szabo on 12/17/17.
 * https://github.com/szaza/android-yolov2
 */
public interface Config {
    int INPUT_SIZE = 416;   // The input size. A square image of inputSize x inputSize is assumed.
    int IMAGE_MEAN = 128;   // The assumed mean of the image values.
    float IMAGE_STD = 128.0f;   // The assumed std of the image values.
    String MODEL_FILE = "tiny_yolo_voc_graph.pb";   // The filepath of the model GraphDef protocol buffer.
    String LABEL_FILE = "tiny-yolo-voc-labels.txt"; // The filepath of label file for classes.
    String INPUT_NAME = "input";    // The label of the image input node.
    String OUTPUT_NAME = "output"; // The label of the output node.

    String LOGGING_TAG = "YOLO";
}
