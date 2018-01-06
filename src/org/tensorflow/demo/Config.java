package org.tensorflow.demo;

/**
 * Created by Zoltan Szabo on 12/17/17.
 */
public interface Config {
    String CLASSIFIER = "YoloV2"; // Possible values are MobileNet | Yolo | YoloV2

    // Configuration for the YoloV2
    int INPUT_SIZE = 416;   // The input size. A square image of inputSize x inputSize is assumed.
    int IMAGE_MEAN = 128;   // The assumed mean of the image values.
    float IMAGE_STD = 128.0f;   // The assumed std of the image values.
    String MODEL_FILE = "file:///android_asset/rounded_graph.pb";   // The filepath of the model GraphDef protocol buffer.
    String LABEL_FILE = "file:///android_asset/tiny-yolo-voc-labels.txt"; // The filepath of label file for classes.
    String INPUT_NAME = "input";    // The label of the image input node.
    String OUTPUT_NAME = "output"; // The label of the output node.
}
