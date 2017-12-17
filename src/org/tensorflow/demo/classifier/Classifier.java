package org.tensorflow.demo.classifier;

import org.tensorflow.Operation;
import org.tensorflow.demo.model.Recognition;

import java.util.List;
import java.util.Vector;

/**
 * Created by Zoltan Szabo on 12/17/17.
 */

public interface Classifier {
    /**
     * Gets the number of classes based on the tensor shape
     *
     * @param operation tensorflow operation object
     * @return the number of classes
     */
    int getNumberOfClassesByShape(final Operation operation);

    /**
     * It classifies the object/objects on the image
     *
     * @param tensorFlowOutput output from the tensorflow
     * @param labels a string vector with the labels
     * @return a list of recognition objects
     */
    List<Recognition> classifyImage(float[] tensorFlowOutput, final Vector<String> labels);
}
