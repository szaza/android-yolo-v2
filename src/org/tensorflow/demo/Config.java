package org.tensorflow.demo;

/**
 * Created by Zoltan Szabo on 12/17/17.
 */
public interface Config {
    int INPUT_SIZE = 224;   // The input size. A square image of inputSize x inputSize is assumed.
    int IMAGE_MEAN = 128;   // The assumed mean of the image values.
    float IMAGE_STD = 128.0f;   // The assumed std of the image values.
    String INPUT_NAME = "input";    // The label of the image input node.
    String OUTPUT_NAME = "MobilenetV1/Predictions/Softmax"; // The label of the output node.
    String MODEL_FILE = "file:///android_asset/graph.pb";   // The filepath of the model GraphDef protocol buffer.
    String LABEL_FILE = "file:///android_asset/labels.txt"; // The filepath of label file for classes.
}
