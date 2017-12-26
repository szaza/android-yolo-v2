package org.tensorflow.demo.classifier;

/**
 * Created by Zoltan Szabo on 12/17/17.
 */

public abstract class ClassifierFactory {
    public static Classifier getInstance() {
        return YoloClassifierv2.getInstance();
    }
}
