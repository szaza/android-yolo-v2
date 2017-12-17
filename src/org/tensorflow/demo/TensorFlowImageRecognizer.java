/* Copyright 2016 The TensorFlow Authors. All Rights Reserved.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
==============================================================================
Modified by Zoltan Szabo
2017 12 17
*/

package org.tensorflow.demo;

import android.content.res.AssetManager;
import android.graphics.Bitmap;

import org.tensorflow.contrib.android.TensorFlowInferenceInterface;
import org.tensorflow.demo.classifier.ClassifierFactory;
import org.tensorflow.demo.model.Recognition;
import org.tensorflow.demo.util.LabelUtil;

import java.io.IOException;
import java.util.List;
import java.util.Vector;

import static org.tensorflow.demo.Config.IMAGE_MEAN;
import static org.tensorflow.demo.Config.IMAGE_STD;
import static org.tensorflow.demo.Config.INPUT_NAME;
import static org.tensorflow.demo.Config.INPUT_SIZE;
import static org.tensorflow.demo.Config.LABEL_FILE;
import static org.tensorflow.demo.Config.MODEL_FILE;
import static org.tensorflow.demo.Config.OUTPUT_NAME;

/**
 * A classifier specialized to label images using TensorFlow.
 */
public class TensorFlowImageRecognizer implements Recognizer {
    private int numClasses;
    private Vector<String> labels;
    private boolean logStats = false;
    private TensorFlowInferenceInterface inferenceInterface;

    private TensorFlowImageRecognizer() {}

    /**
     * Initializes a native TensorFlow session for classifying images.
     *
     * @param assetManager  The asset manager to be used to load assets.
     * @throws IOException
     */
    public static Recognizer create(AssetManager assetManager) {
        TensorFlowImageRecognizer recognizer = new TensorFlowImageRecognizer();
        recognizer.labels = LabelUtil.readLabels(assetManager, LABEL_FILE);
        recognizer.inferenceInterface = new TensorFlowInferenceInterface(assetManager, MODEL_FILE);
        recognizer.numClasses = ClassifierFactory.getInstance()
                .getNumberOfClassesByShape(recognizer.inferenceInterface.graphOperation(OUTPUT_NAME));
        return recognizer;
    }

    @Override
    public List<Recognition> recognizeImage(final Bitmap bitmap) {
        return ClassifierFactory.getInstance().classifyImage(runTensorFlow(bitmap), labels);
    }

    @Override
    public void enableStatLogging(boolean logStats) {
        this.logStats = logStats;
    }

    @Override
    public String getStatString() {
        return inferenceInterface.getStatString();
    }

    @Override
    public void close() {
        inferenceInterface.close();
    }

    private float[] runTensorFlow(final Bitmap bitmap) {
        final float[] tfOutput = new float[numClasses];
        // Copy the input data into TensorFlow.
        inferenceInterface.feed(INPUT_NAME, processBitmap(bitmap), 1, INPUT_SIZE, INPUT_SIZE, 3);

        // Run the inference call.
        inferenceInterface.run(new String[]{OUTPUT_NAME}, logStats);

        // Copy the output Tensor back into the output array.
        inferenceInterface.fetch(OUTPUT_NAME, tfOutput);

        return tfOutput;
    }

    /**
     * Preprocess the image data from 0-255 int to normalized float based
     * on the provided parameters.
     * @param bitmap
     */
    private float[] processBitmap(final Bitmap bitmap) {
        int[] intValues = new int[INPUT_SIZE * INPUT_SIZE];
        float[] floatValues = new float[INPUT_SIZE * INPUT_SIZE * 3];;

        bitmap.getPixels(intValues, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
        for (int i = 0; i < intValues.length; ++i) {
            final int val = intValues[i];
            floatValues[i * 3 + 0] = (((val >> 16) & 0xFF) - IMAGE_MEAN) / IMAGE_STD;
            floatValues[i * 3 + 1] = (((val >> 8) & 0xFF) - IMAGE_MEAN) / IMAGE_STD;
            floatValues[i * 3 + 2] = ((val & 0xFF) - IMAGE_MEAN) / IMAGE_STD;
        }
        return floatValues;
    }
}
