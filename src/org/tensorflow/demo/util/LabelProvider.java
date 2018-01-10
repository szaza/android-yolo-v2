package org.tensorflow.demo.util;

import android.content.res.AssetManager;

import org.tensorflow.demo.Config;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Vector;

/**
 * It is used to read names of the classes from the specified resource.
 *
 * Created by Zoltan Szabo on 12/17/17.
 */

public final class LabelProvider {
    public static Vector<String> readLabels(final AssetManager assetManager) {
        Vector<String> labels = new Vector();

        try (BufferedReader br = new BufferedReader(new InputStreamReader(assetManager.open(Config.LABEL_FILE)))) {
            br.lines().forEach((line) -> labels.add(line));
        } catch (IOException ex) {
            throw new RuntimeException("Problem reading label file!", ex);
        }

        return labels;
    }
}
