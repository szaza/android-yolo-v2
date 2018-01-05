package org.tensorflow.demo.classifier;

/**
 * Created by Zoltan Szabo on 12/17/17.
 */

public abstract class ClassifierFactory {
    public static Classifier getInstance(final String classifier) {
        switch (classifier) {
            case "Yolo":
                return YoloClassifier.getInstance();
            case "YoloV2":
                return YoloClassifierv2.getInstance();
            default:
                return DefaultClassifier.getInstance();
        }
    }
}
