package org.tensorflow.demo.classifier;

import org.tensorflow.Operation;
import org.tensorflow.demo.model.Recognition;
import org.tensorflow.demo.model.Yolov2;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * This classifier has been written for Yolov2 model
 *
 * Created by Zoltan Szabo on 12/17/17.
 */

public class YoloClassifierv2 implements Classifier {

    private final static int SIZE = 7;
    private static YoloClassifierv2 classifier;

    private YoloClassifierv2() {}

    public static Classifier getInstance() {
        if (classifier == null) {
            classifier = new YoloClassifierv2();
        }

        return  classifier;
    }

    @Override
    public int getNumberOfClassesByShape(final Operation operation) {
        return (int) operation.output(0).shape().size(3) / 5 - 5;
    }

    @Override
    /**
     * The yolov2 output is size*size*125 in our case x = size
     * 125 = (numClass + coords + 1) * number in our case (20 + 4 +1) * 5
     * For more information please read the article here: https://arxiv.org/pdf/1612.08242.pdf
     */
    public List<Recognition> classifyImage(final float[] tensorFlowOutput, final Vector<String> labels) {
        int numClass = (int) (tensorFlowOutput.length / (Math.pow(SIZE,2) * 5) - 5);
        Yolov2[][][] boundingBoxPerCell = new Yolov2[SIZE][SIZE][5];

        for (int i=0; i<SIZE; i++) {        // SIZE * SIZE cells
            for (int j=0; j<SIZE; j++) {
                for (int b=0; b<5; b++) {   // 5 bounding boxes per each cell
                    boundingBoxPerCell[i][j][b] = getModel(tensorFlowOutput, numClass, i+j+b);
                }
            }
        }

        return getRecognition(boundingBoxPerCell);
    }

    private Yolov2 getModel(final float[] tensorFlowOutput, final int numClass, final int offset) {
        int index =  (5 + numClass) * offset;

        Yolov2 model = new Yolov2();
        model.setTx(tensorFlowOutput[index]);
        model.setTy(tensorFlowOutput[index+1]);
        model.setTw(tensorFlowOutput[index+2]);
        model.setTh(tensorFlowOutput[index+3]);
        model.setTo(tensorFlowOutput[index+4]);

        for (int j=0; j<numClass; j++) {
            model.addProbability(tensorFlowOutput[5 + index + j]);
        }

        return model;
    }

    private List<Recognition> getRecognition(final Yolov2[][][] boundingBoxPerCell) {
        List<Recognition> recognitions = new ArrayList();



        return recognitions;
    }
}
