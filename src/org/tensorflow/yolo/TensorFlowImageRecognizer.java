package org.tensorflow.yolo;

import android.content.res.AssetManager;
import android.graphics.Bitmap;

import org.tensorflow.contrib.android.TensorFlowInferenceInterface;
import org.tensorflow.yolo.model.Recognition;
import org.tensorflow.yolo.util.ClassAttrProvider;

import java.io.IOException;
import java.util.List;
import java.util.Vector;

import static org.tensorflow.yolo.Config.IMAGE_MEAN;
import static org.tensorflow.yolo.Config.IMAGE_STD;
import static org.tensorflow.yolo.Config.INPUT_NAME;
import static org.tensorflow.yolo.Config.INPUT_SIZE;
import static org.tensorflow.yolo.Config.MODEL_FILE;
import static org.tensorflow.yolo.Config.OUTPUT_NAME;

/**
 * A classifier specialized to label images using TensorFlow.
 * Modified by Zoltan Szabo
 */
public class TensorFlowImageRecognizer {
    private int outputSize;
    private Vector<String> labels;
    private TensorFlowInferenceInterface inferenceInterface;

    private TensorFlowImageRecognizer() {
    }

    /**
     * Initializes a native TensorFlow session for classifying images.
     *
     * @param assetManager The asset manager to be used to load assets.
     * @throws IOException
     */
    public static TensorFlowImageRecognizer create(AssetManager assetManager) {
        TensorFlowImageRecognizer recognizer = new TensorFlowImageRecognizer();
        recognizer.labels = ClassAttrProvider.newInstance(assetManager).getLabels();
        recognizer.inferenceInterface = new TensorFlowInferenceInterface(assetManager,
                "file:///android_asset/" + MODEL_FILE);
        recognizer.outputSize = YOLOClassifier.getInstance()
                .getOutputSizeByShape(recognizer.inferenceInterface.graphOperation(OUTPUT_NAME));
        return recognizer;
    }

    public List<Recognition> recognizeImage(final Bitmap bitmap) {
        return YOLOClassifier.getInstance().classifyImage(runTensorFlow(bitmap), labels);
    }

    public String getStatString() {
        return inferenceInterface.getStatString();
    }

    public void close() {
        inferenceInterface.close();
    }

    private float[] runTensorFlow(final Bitmap bitmap) {
        final float[] tfOutput = new float[outputSize];
        // Copy the input data into TensorFlow.
        inferenceInterface.feed(INPUT_NAME, processBitmap(bitmap), 1, INPUT_SIZE, INPUT_SIZE, 3);

        // Run the inference call.
        inferenceInterface.run(new String[]{OUTPUT_NAME});

        // Copy the output Tensor back into the output array.
        inferenceInterface.fetch(OUTPUT_NAME, tfOutput);

        return tfOutput;
    }

    /**
     * Preprocess the image data from 0-255 int to normalized float based
     * on the provided parameters.
     *
     * @param bitmap
     */
    private float[] processBitmap(final Bitmap bitmap) {
        int[] intValues = new int[INPUT_SIZE * INPUT_SIZE];
        float[] floatValues = new float[INPUT_SIZE * INPUT_SIZE * 3];

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
