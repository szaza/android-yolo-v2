package org.tensorflow.demo.classifier;

import org.tensorflow.Operation;
import org.tensorflow.demo.model.Recognition;

import java.util.List;
import java.util.Vector;

/**
 * This classifier has been written for Yolov2 model
 *
 * Created by Zoltan Szabo on 12/17/17.
 */

public class YoloClassifierv2 implements Classifier {
    @Override
    public int getNumberOfClassesByShape(final Operation operation) {
        return 0;
    }

    @Override
    public List<Recognition> classifyImage(final float[] tensorFlowOutput, final Vector<String> labels) {
        return null;
    }
}
