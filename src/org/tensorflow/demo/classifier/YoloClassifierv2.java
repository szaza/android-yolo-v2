package org.tensorflow.demo.classifier;

import android.graphics.RectF;

import org.apache.commons.math3.analysis.function.Sigmoid;
import org.tensorflow.Operation;
import org.tensorflow.demo.model.Recognition;
import org.tensorflow.demo.model.Yolov2;
import org.tensorflow.demo.util.Logger;
import org.tensorflow.demo.util.math.ArgMax;
import org.tensorflow.demo.util.math.SoftMax;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Vector;

/**
 * This classifier has been written for Yolov2 model
 *
 * Created by Zoltan Szabo on 12/17/17.
 */

public class YoloClassifierv2 implements Classifier {
    private static final Logger LOGGER = new Logger();
    private final static double anchors[] = {1.08,1.19,  3.42,4.41,  6.63,11.38,  9.42,5.11,  16.62,10.52};
    private final static int SIZE = 13;
    private final static int MAX_RECOGNIZED_CLASSES = 10;
    private final static float THRESHOLD = 0.1f;
    private final static int MAX_RESULTS = 5;
    private final static int NUMBER_OF_BOUNDING_BOX = 5;
    private static YoloClassifierv2 classifier;

    private YoloClassifierv2() {}

    public static Classifier getInstance() {
        if (classifier == null) {
            classifier = new YoloClassifierv2();
        }

        return  classifier;
    }

    @Override
    public int getOutputSizeByShape(final Operation operation) {
        return (int) (operation.output(0).shape().size(3) * Math.pow(SIZE,2));
    }

    @Override
    /**
     * The yolov2 output is size*size*125 in our case x = size
     * 125 = (numClass + coords + 1) * number in our case (20 + 4 +1) * 5
     * For more information please read the article here: https://arxiv.org/pdf/1612.08242.pdf
     */
    public List<Recognition> classifyImage(final float[] tensorFlowOutput, final Vector<String> labels) {
        int numClass = (int) (tensorFlowOutput.length / (Math.pow(SIZE,2) * NUMBER_OF_BOUNDING_BOX) - 5);
        Yolov2[][][] boundingBoxPerCell = new Yolov2[SIZE][SIZE][NUMBER_OF_BOUNDING_BOX];
        PriorityQueue<Recognition> priorityQueue = new PriorityQueue<>(MAX_RECOGNIZED_CLASSES, new RecognitionComparator());

        int offset = 0;
        for (int cx=0; cx<SIZE; cx++) {        // SIZE * SIZE cells
            for (int cy=0; cy<SIZE; cy++) {
                for (int b=0; b<NUMBER_OF_BOUNDING_BOX; b++) {   // 5 bounding boxes per each cell
                    boundingBoxPerCell[cx][cy][b] = getModel(tensorFlowOutput, cx, cy, b, numClass, offset);
                    calculateTopPredictions(boundingBoxPerCell[cx][cy][b], priorityQueue, labels);
                    offset = offset + numClass + 5;
                }
            }
        }

        return getRecognition(priorityQueue);
    }

    private Yolov2 getModel(final float[] tensorFlowOutput, int cx, int cy, int b, int numClass, int offset) {
        Yolov2 model = new Yolov2();
        Sigmoid sigmoid = new Sigmoid();
        model.setX((cx + sigmoid.value(tensorFlowOutput[offset])) * 32);
        model.setY((cy + sigmoid.value(tensorFlowOutput[offset + 1])) * 32);
        model.setW(Math.exp(tensorFlowOutput[offset + 2]) * anchors[2 * b] * 32);
        model.setH(Math.exp(tensorFlowOutput[offset + 3]) * anchors[2 * b + 1] * 32);
        model.setConfidence(sigmoid.value(tensorFlowOutput[offset + 4]));

        model.setClasses(new double[numClass]);

        for (int probIndex=0; probIndex<numClass; probIndex++) {
            model.getClasses()[probIndex] = tensorFlowOutput[probIndex + offset + 5];
        }

        return model;
    }

    private void calculateTopPredictions(final Yolov2 boundingBox, final PriorityQueue<Recognition> predictionQueue,
                                  final Vector<String> labels) {
        for (int i=0; i<boundingBox.getClasses().length; i++) {
            ArgMax.Result argMax = new ArgMax(new SoftMax(boundingBox.getClasses()).getValue()).getResult();
            double confidenceInClass = argMax.getMaxValue() * boundingBox.getConfidence();

            if (confidenceInClass > THRESHOLD) {
                predictionQueue.add(new Recognition("" + argMax.getIndex(), labels.get(argMax.getIndex()), (float) confidenceInClass,
                        new RectF((float) (boundingBox.getX() - boundingBox.getW() / 2),
                                (float) (boundingBox.getY() - boundingBox.getH() / 2),
                                (float) boundingBox.getW(),
                                (float) boundingBox.getH())));
            }
        }
    }

    private List<Recognition> getRecognition(final PriorityQueue<Recognition> priorityQueue) {
        List<Recognition> recognitions = new ArrayList();

        for (int i = 0; i < Math.min(priorityQueue.size(), MAX_RESULTS); ++i) {
            recognitions.add(priorityQueue.poll());
        }

        return recognitions;
    }

    // Intentionally reversed to put high confidence at the head of the queue.
    private class RecognitionComparator implements Comparator<Recognition> {
        @Override
        public int compare(final Recognition lhs, final Recognition rhs) {
            return Float.compare(rhs.getConfidence(), lhs.getConfidence());
        }
    }
}
