package org.tensorflow.yolo;

import org.apache.commons.math3.analysis.function.Sigmoid;
import org.tensorflow.Operation;
import org.tensorflow.yolo.model.BoundingBox;
import org.tensorflow.yolo.model.BoxPosition;
import org.tensorflow.yolo.model.Recognition;
import org.tensorflow.yolo.util.math.ArgMax;
import org.tensorflow.yolo.util.math.SoftMax;

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
 * https://github.com/szaza/android-yolo-v2
 */
public class YOLOClassifier {
    private final static float OVERLAP_THRESHOLD = 0.5f;
    private final static double anchors[] = {1.08,1.19,  3.42,4.41,  6.63,11.38,  9.42,5.11,  16.62,10.52};
    private final static int SIZE = 13;
    private final static int MAX_RECOGNIZED_CLASSES = 13;
    private final static float THRESHOLD = 0.3f;
    private final static int MAX_RESULTS = 15;
    private final static int NUMBER_OF_BOUNDING_BOX = 5;
    private static YOLOClassifier classifier;

    private YOLOClassifier() {}

    public static YOLOClassifier getInstance() {
        if (classifier == null) {
            classifier = new YOLOClassifier();
        }

        return  classifier;
    }

    /**
     * Gets the number of classes based on the tensor shape
     *
     * @param operation tensorflow operation object
     * @return the number of classes
     */
    public int getOutputSizeByShape(final Operation operation) {
        return (int) (operation.output(0).shape().size(3) * Math.pow(SIZE,2));
    }

    /**
     * It classifies the object/objects on the image
     *
     * @param tensorFlowOutput output from the tensorflow, it is a 13x13x125 tensor
     * 125 = (numClass +  Tx, Ty, Tw, Th, To) * 5 - cause we have 5 boxes per each cell
     * @param labels a string vector with the labels
     * @return a list of recognition objects
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
                        new BoxPosition((float) (boundingBox.getX() - boundingBox.getWidth() / 2),
                                (float) (boundingBox.getY() - boundingBox.getHeight() / 2),
                                (float) boundingBox.getWidth(),
                                (float) boundingBox.getHeight())));
            }
        }
    }

    private List<Recognition> getRecognition(final PriorityQueue<Recognition> priorityQueue) {
        List<Recognition> recognitions = new ArrayList();

        if (priorityQueue.size() > 0) {
            // Best recognition
            Recognition bestRecognition = priorityQueue.poll();
            recognitions.add(bestRecognition);

            for (int i = 0; i < Math.min(priorityQueue.size(), MAX_RESULTS); ++i) {
                Recognition recognition = priorityQueue.poll();
                boolean overlaps = false;
                for (Recognition previousRecognition : recognitions) {
                    overlaps = overlaps || (getIntersectionProportion(previousRecognition.getLocation(),
                            recognition.getLocation()) > OVERLAP_THRESHOLD);
                }

                if (!overlaps) {
                    recognitions.add(recognition);
                }
            }
        }

        return recognitions;
    }

    private float getIntersectionProportion(BoxPosition primaryShape, BoxPosition secondaryShape) {
        if (overlaps(primaryShape, secondaryShape)) {
            float intersectionSurface = Math.max(0, Math.min(primaryShape.getRight(), secondaryShape.getRight()) - Math.max(primaryShape.getLeft(), secondaryShape.getLeft())) *
                    Math.max(0, Math.min(primaryShape.getBottom(), secondaryShape.getBottom()) - Math.max(primaryShape.getTop(), secondaryShape.getTop()));

            float surfacePrimary = Math.abs(primaryShape.getRight() - primaryShape.getLeft()) * Math.abs(primaryShape.getBottom() - primaryShape.getTop());

            return intersectionSurface / surfacePrimary;
        }

        return 0f;

    }

    private boolean overlaps(BoxPosition primary, BoxPosition secondary) {
        return primary.getLeft() < secondary.getRight() && primary.getRight() > secondary.getLeft()
                && primary.getTop() < secondary.getBottom() && primary.getBottom() > secondary.getTop();
    }

    // Intentionally reversed to put high confidence at the head of the queue.
    private class RecognitionComparator implements Comparator<Recognition> {
        @Override
        public int compare(final Recognition recognition1, final Recognition recognition2) {
            return Float.compare(recognition2.getConfidence(), recognition1.getConfidence());
        }
    }
}
