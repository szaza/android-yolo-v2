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
package org.tensorflow.yolo;

/**
 * Created by Zoltan Szabo on 12/17/17.
 * https://github.com/szaza/android-yolo-v2
 */
public interface Config {
    int INPUT_SIZE = 416;   // The input size. A square image of inputSize x inputSize is assumed.
    int IMAGE_MEAN = 128;   // The assumed mean of the image values.
    float IMAGE_STD = 128.0f;   // The assumed std of the image values.
    String MODEL_FILE = "tiny-yolo-voc-graph.pb";   // The filepath of the model GraphDef protocol buffer.
    String LABEL_FILE = "tiny-yolo-voc-labels.txt"; // The filepath of label file for classes.
    String INPUT_NAME = "input";    // The label of the image input node.
    String OUTPUT_NAME = "output"; // The label of the output node.

    String LOGGING_TAG = "YOLO";
}
