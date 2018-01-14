/*
 * Copyright 2018 The Android YOLOv2 sample application Authors.
 *
 *     This file is part of Android YOLOv2 sample application.
 * Android YOLOv2 is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Android YOLOv2 is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Android YOLOv2. If not, see <http://www.gnu.org/licenses/>.
 */
package org.tensorflow.yolo.util;

import android.content.res.AssetManager;
import android.graphics.Color;

import org.tensorflow.yolo.Config;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Vector;

/**
 * It is used to read names of the classes from the specified resource.
 * It also specifies a color for each classes.
 *
 * Created by Zoltan Szabo on 12/17/17.
 * URL: https://github.com/szaza/android-yolov2
 */

public final class ClassAttrProvider {
    private final Vector<String> labels = new Vector();
    private final Vector<Integer> colors = new Vector();
    private static ClassAttrProvider instance;

    private ClassAttrProvider(final AssetManager assetManager) {
        init(assetManager);
    }

    public static ClassAttrProvider newInstance(final AssetManager assetManager) {
        if (instance == null) {
            instance = new ClassAttrProvider(assetManager);
        }

        return instance;
    }

    private void init(final AssetManager assetManager) {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(assetManager.open(Config.LABEL_FILE)))) {
            br.lines().forEach((line) -> {
                labels.add(line);
                colors.add(convertClassNameToColor(line));
            });
        } catch (IOException ex) {
            throw new RuntimeException("Problem reading label file!", ex);
        }
    }

    private int convertClassNameToColor(String className) {
        byte[] rgb = new byte[3];
        byte[] name = className.getBytes();

        for (int i=0; i<name.length; i++) {
            rgb[i%3] += name[i];
        }

        //Hue saturation
        for (int i=0; i<rgb.length; i++) {
            if (rgb[i] < 120) {
                rgb[i] += 120;
            }
        }

        return Color.rgb(rgb[0], rgb[1], rgb[2]);
    }

    public Vector<String> getLabels() {
        return labels;
    }

    public Vector<Integer> getColors() {
        return colors;
    }
}
