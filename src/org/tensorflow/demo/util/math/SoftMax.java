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
package org.tensorflow.demo.util.math;

/**
 * Implementation of the SoftMax function.
 * For more information please read this article:
 * https://en.wikipedia.org/wiki/Softmax_function
 *
 * Created by Zoltan Szabo on 1/5/18.
 * URL: https://github.com/szaza/android-yolov2
 */

public class SoftMax {
    private final double[] params;

    public SoftMax(double[] params) {
        this.params = params;
    }

    public double[] getValue() {
        double sum = 0;

        for (int i=0; i<params.length; i++) {
            params[i] = Math.exp(params[i]);
            sum += params[i];
        }

        if (Double.isNaN(sum) || sum < 0) {
            for (int i=0; i<params.length; i++) {
                params[i] = 1.0 / params.length;
            }
        } else {
            for (int i=0; i<params.length; i++) {
                params[i] = params[i] / sum;
            }
        }

        return params;
    }
}
