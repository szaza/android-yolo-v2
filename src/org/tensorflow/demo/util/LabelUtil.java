package org.tensorflow.demo.util;

import android.content.res.AssetManager;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Vector;

/**
 * Created by Zoltan Szabo on 12/17/17.
 */

public final class LabelUtil {
    private static final String TAG = "YOLOv2";

    // Read the label names into memory.
    // TODO(andrewharp): make this handle non-assets.
    public static Vector<String> readLabels(final AssetManager assetManager, final String labelFilename) {
        Vector<String> labels = new Vector();

        String actualFilename = labelFilename.split("file:///android_asset/")[1];
        Log.i(TAG, "Reading labels from: " + actualFilename);

        try (BufferedReader br = new BufferedReader(new InputStreamReader(assetManager.open(actualFilename)))) {
            br.lines().forEach((line) -> labels.add(line));
        } catch (IOException ex) {
            throw new RuntimeException("Problem reading label file!", ex);
        }

        return labels;
    }
}
