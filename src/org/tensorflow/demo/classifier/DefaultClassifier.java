package org.tensorflow.demo.classifier;

import org.tensorflow.Operation;
import org.tensorflow.demo.model.Recognition;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Vector;

/**
 * This classifier works for the MobileNet and Inception v3 models.
 * It is a modified version of the classifier what you can find in the example code from
 * https://codelabs.developers.google.com/codelabs/tensorflow-for-poets-2/#0
 *
 * Don't forget to change the values in the Config file when you change the model.
 *
 * Modified by Zoltan Szabo on 12/17/17.
 */

public class DefaultClassifier implements Classifier {
    // Only return this many results with at least this confidence.
    private static final int MAX_RESULTS = 3;
    private static final float THRESHOLD = 0.1f;

    private static Classifier classifier;
    private DefaultClassifier() {}

    public static Classifier getInstance() {
        if (classifier == null) {
            classifier = new DefaultClassifier();
        }

        return  classifier;
    }

    public List<Recognition> classifyImage(final float[] tensorFlowOutput, final Vector<String> labels) {
        PriorityQueue<Recognition> priorityQueue = new PriorityQueue(3, new RecognitionComparator());

        for (int i = 0; i < tensorFlowOutput.length; ++i) {
            if (tensorFlowOutput[i] > THRESHOLD) {
                priorityQueue.add(new Recognition("" + i,
                        labels.size() > i ? labels.get(i) : "unknown",
                        tensorFlowOutput[i], null));
            }
        }

        final List<Recognition> recognitions = new ArrayList<Recognition>();
        for (int i = 0; i < Math.min(priorityQueue.size(), MAX_RESULTS); ++i) {
            recognitions.add(priorityQueue.poll());
        }

        return recognitions;
    }

    // The shape of the output is [N, NUM_CLASSES], where N is the batch size.
    public int getNumberOfClassesByShape(final Operation operation) {
        return (int) operation.output(0).shape().size(1);
    }

    // Intentionally reversed to put high confidence at the head of the queue.
    private class RecognitionComparator implements Comparator<Recognition> {
        @Override
        public int compare(final Recognition lhs, final Recognition rhs) {
            return Float.compare(rhs.getConfidence(), lhs.getConfidence());
        }
    }
}
