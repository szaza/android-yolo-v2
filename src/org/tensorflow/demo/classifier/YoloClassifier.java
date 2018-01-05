package org.tensorflow.demo.classifier;

import android.graphics.RectF;
import android.util.Log;

import org.tensorflow.Operation;
import org.tensorflow.demo.Config;
import org.tensorflow.demo.model.Recognition;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * Created by Zoltan Szabo on 1/2/18.
 */

public class YoloClassifier implements Classifier {
    private final static int SIZE = 7;
    private final static int NUMBER_OF_BOUNDING_BOX = 2;
    private static YoloClassifier classifier;

    private YoloClassifier() {}

    public static Classifier getInstance() {
        if (classifier == null) {
            classifier = new YoloClassifier();
        }

        return  classifier;
    }

    @Override
    public int getOutputSizeByShape(Operation operation) {
        return (int) (operation.output(0).shape().size(1) * Math.pow(SIZE,2));
    }

    @Override
    public List<Recognition> classifyImage(float[] tensorFlowOutput, Vector<String> labels) {
        final ArrayList<Recognition> recognitions = new ArrayList<Recognition>();
        int numClass = 20;
        float[][][] class_probs = new float[SIZE][SIZE][numClass];
        float[][][] scales = new float[SIZE][SIZE][NUMBER_OF_BOUNDING_BOX];
        float[][][][] boxes = new float[SIZE][SIZE][NUMBER_OF_BOUNDING_BOX][4];

        // Convert the linear array to the output tensor (7x7x30)
        int counter = 0;
        // Corresponds to class probs
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                for (int k = 0; k < numClass; k++) {
                    class_probs[i][j][k] = tensorFlowOutput[counter];
                    counter++;
                }
            }
        }
        // Corresponds to scales
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                for (int k = 0; k < NUMBER_OF_BOUNDING_BOX; k++) {
                    scales[i][j][k] = tensorFlowOutput[counter];
                    counter++;
                }
            }
        }
        // Corresponds to boxes
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                for (int k = 0; k < NUMBER_OF_BOUNDING_BOX; k++) {
                    for (int l = 0; l < 4; l++) {
                        boxes[i][j][k][l] = tensorFlowOutput[counter];
                        counter++;
                    }
                }
            }
        }

        // Add offset.
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                for (int k = 0; k < NUMBER_OF_BOUNDING_BOX; k++) {
                    boxes[i][j][k][0] += j;
                    boxes[i][j][k][0] = boxes[i][j][k][0] * Config.INPUT_SIZE / SIZE;
                }
            }
        }
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                for (int k = 0; k < NUMBER_OF_BOUNDING_BOX; k++) {
                    boxes[i][j][k][1] += i;
                    boxes[i][j][k][1] = boxes[i][j][k][1] * Config.INPUT_SIZE / SIZE;
                }
            }
        }
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                for (int k = 0; k < NUMBER_OF_BOUNDING_BOX; k++) {
                    boxes[i][j][k][2] = boxes[i][j][k][2] * boxes[i][j][k][2] * Config.INPUT_SIZE;
                    boxes[i][j][k][3] = boxes[i][j][k][3] * boxes[i][j][k][3] * Config.INPUT_SIZE;
                }
            }
        }

        float[][][][] probs = new float[SIZE][SIZE][NUMBER_OF_BOUNDING_BOX][numClass];
        // Combine conditional class probabilities and objectness probability.
        for (int i = 0; i < NUMBER_OF_BOUNDING_BOX; i++) {
            for (int j = 0; j < numClass; j++) {
                for (int l = 0; l < SIZE; l++) {
                    for (int m = 0; m < SIZE; m++) {
                        probs[l][m][i][j] = class_probs[l][m][j] * scales[l][m][i];
                    }
                }
            }
        }

        // I will try to output the best bounding box and class.
        // First get best probability
        float highest_prob = 0;
        int hp_i = 0;
        int hp_j = 0;
        int hp_l = 0;
        int hp_m = 0;
        for (int i = 0; i < NUMBER_OF_BOUNDING_BOX; i++) {
            for (int j = 0; j < numClass; j++) {
                for (int l = 0; l < SIZE; l++) {
                    for (int m = 0; m < SIZE; m++) {
                        if (probs[l][m][i][j] >= highest_prob) {
                            highest_prob = probs[l][m][i][j];
                            hp_i = i;
                            hp_j = j;
                            hp_l = l;
                            hp_m = m;
                        }
                    }
                }
            }
        }

        // Get x, y, width, height. These will be processed and drawn in BoundingBoxView.
        float bounding_x = boxes[hp_l][hp_m][hp_i][0];
        float bounding_y = boxes[hp_l][hp_m][hp_i][1];
        float box_width = boxes[hp_l][hp_m][hp_i][2] / 2;
        float box_height = boxes[hp_l][hp_m][hp_i][3] / 2;

        // Now get the class number.
        int predicted_class = hp_j;

        // Now log this prediction.
        String prediction_string = Integer.toString(predicted_class) + " | x1: " + Float.toString(bounding_x) +
                " y1: " + Float.toString(bounding_y) + " width: " + Float.toString(box_width) +
                " height: " + Float.toString(box_height);
        Log.i("Java prediction --- ", prediction_string);

        // Add recognition to recognition list.
        final RectF boundingBox = new RectF(bounding_x, bounding_y, box_width, box_height);
        recognitions.add(new Recognition("Prediction ", labels.get(predicted_class), highest_prob, boundingBox));
        Log.i("YOLO", predicted_class + "");
        return recognitions;
    }
}
