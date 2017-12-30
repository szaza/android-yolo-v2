package org.tensorflow.demo.classifier;

import android.graphics.RectF;

import org.tensorflow.Operation;
import org.tensorflow.demo.model.Recognition;
import org.tensorflow.demo.model.Yolov2;
import org.tensorflow.demo.util.Logger;

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
    private final static int SIZE = 7;
    private final static int MAX_RECOGNIZED_CLASSES = 3;
    private final static float THRESHOLD = 0.1f;
    private final static int MAX_RESULTS = 3;
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

        for (int i=0; i<SIZE; i++) {        // SIZE * SIZE cells
            for (int j=0; j<SIZE; j++) {
                for (int b=0; b<NUMBER_OF_BOUNDING_BOX; b++) {   // 5 bounding boxes per each cell
                    boundingBoxPerCell[i][j][b] = getModel(tensorFlowOutput, numClass, i+j+b);
                    getTopPriorities(boundingBoxPerCell[i][j][b], priorityQueue, labels);
                }
            }
        }

        return getRecognition(priorityQueue);
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
            model.addCalculatedProbabilities(tensorFlowOutput[5 + index + j] * tensorFlowOutput[index + 4]);
        }

        return model;
    }

    private void getTopPriorities(final Yolov2 boundingBox, final PriorityQueue<Recognition> priorityQueue,
                                  final Vector<String> labels) {
        for (int i=0; i<boundingBox.getCalculatedProbablilities().size(); i++) {
            LOGGER.i("i: " + i + " - " + boundingBox.getCalculatedProbablilities().get(i));
            if (boundingBox.getCalculatedProbablilities().get(i) > THRESHOLD) {
                priorityQueue.add(new Recognition("" + i, labels.get(i), boundingBox.getCalculatedProbablilities().get(i),
                        new RectF(boundingBox.getTx(), boundingBox.getTy(),
                                boundingBox.getTx() + boundingBox.getTw(),
                                boundingBox.getTy() + boundingBox.getTh())));
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
