package org.tensorflow.demo.classifier;

import android.graphics.RectF;

import org.apache.commons.math3.analysis.function.Sigmoid;
import org.tensorflow.Operation;
import org.tensorflow.demo.model.BoundingBox;
import org.tensorflow.demo.model.Recognition;
import org.tensorflow.demo.util.math.ArgMax;
import org.tensorflow.demo.util.math.SoftMax;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Vector;

/**
 * Implementation of YOLOv2 classifier based on the article:
 * https://arxiv.org/pdf/1612.08242.pdf
 *
 * Created by Zoltan Szabo on 12/17/17.
 */
public class YOLOClassifier implements Classifier {
    private final static double anchors[] = {1.08,1.19,  3.42,4.41,  6.63,11.38,  9.42,5.11,  16.62,10.52};
    private final static int SIZE = 13;
    private final static int MAX_RECOGNIZED_CLASSES = 13;
    private final static float THRESHOLD = 0.3f;
    private final static int MAX_RESULTS = 10;
    private final static int NUMBER_OF_BOUNDING_BOX = 5;
    private static YOLOClassifier classifier;

    private YOLOClassifier() {}

    public static Classifier getInstance() {
        if (classifier == null) {
            classifier = new YOLOClassifier();
        }

        return  classifier;
    }

    @Override
    public int getOutputSizeByShape(final Operation operation) {
        return (int) (operation.output(0).shape().size(3) * Math.pow(SIZE,2));
    }

    @Override
    /**
     * The output is 13x13x125
     * 125 = (numClass +  Tx, Ty, Tw, Th, To) * 5 - cause we have 5 boxes per each cell
     */
    public List<Recognition> classifyImage(final float[] tensorFlowOutput, final Vector<String> labels) {
        int numClass = (int) (tensorFlowOutput.length / (Math.pow(SIZE,2) * NUMBER_OF_BOUNDING_BOX) - 5);
        BoundingBox[][][] boundingBoxPerCell = new BoundingBox[SIZE][SIZE][NUMBER_OF_BOUNDING_BOX];
        PriorityQueue<Recognition> priorityQueue = new PriorityQueue<>(MAX_RECOGNIZED_CLASSES, new RecognitionComparator());

        int offset = 0;
        for (int cy=0; cy<SIZE; cy++) {        // SIZE * SIZE cells
            for (int cx=0; cx<SIZE; cx++) {
                for (int b=0; b<NUMBER_OF_BOUNDING_BOX; b++) {   // 5 bounding boxes per each cell
                    boundingBoxPerCell[cx][cy][b] = getModel(tensorFlowOutput, cx, cy, b, numClass, offset);
                    calculateTopPredictions(boundingBoxPerCell[cx][cy][b], priorityQueue, labels);
                    offset = offset + numClass + 5;
                }
            }
        }

        return getRecognition(priorityQueue);
    }

    private BoundingBox getModel(final float[] tensorFlowOutput, int cx, int cy, int b, int numClass, int offset) {
        BoundingBox model = new BoundingBox();
        Sigmoid sigmoid = new Sigmoid();
        model.setX((cx + sigmoid.value(tensorFlowOutput[offset])) * 32);
        model.setY((cy + sigmoid.value(tensorFlowOutput[offset + 1])) * 32);
        model.setWidth(Math.exp(tensorFlowOutput[offset + 2]) * anchors[2 * b] * 32);
        model.setHeight(Math.exp(tensorFlowOutput[offset + 3]) * anchors[2 * b + 1] * 32);
        model.setConfidence(sigmoid.value(tensorFlowOutput[offset + 4]));

        model.setClasses(new double[numClass]);

        for (int probIndex=0; probIndex<numClass; probIndex++) {
            model.getClasses()[probIndex] = tensorFlowOutput[probIndex + offset + 5];
        }

        return model;
    }

    private void calculateTopPredictions(final BoundingBox boundingBox, final PriorityQueue<Recognition> predictionQueue,
                                         final Vector<String> labels) {
        for (int i=0; i<boundingBox.getClasses().length; i++) {
            ArgMax.Result argMax = new ArgMax(new SoftMax(boundingBox.getClasses()).getValue()).getResult();
            double confidenceInClass = argMax.getMaxValue() * boundingBox.getConfidence();

            if (confidenceInClass > THRESHOLD) {
                predictionQueue.add(new Recognition(argMax.getIndex(), labels.get(argMax.getIndex()), (float) confidenceInClass,
                        new RectF((float) (boundingBox.getX() - boundingBox.getWidth() / 2),
                                (float) (boundingBox.getY() - boundingBox.getHeight() / 2),
                                (float) boundingBox.getWidth(),
                                (float) boundingBox.getHeight())));
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
        public int compare(final Recognition recognition1, final Recognition recognition2) {
            return Float.compare(recognition1.getConfidence(), recognition2.getConfidence());
        }
    }
}
