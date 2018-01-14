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
package org.tensorflow.yolo.util.math;

/**
 * ArgMax function to select the higher value and its index from the array.
 *
 * Created by Zoltan Szabo on 1/5/18.
 * URL: https://github.com/szaza/android-yolov2
 */
public class ArgMax {

    private double[] params;

    public ArgMax(double[] params) {
        this.params = params;
    }

    public Result getResult() {
        int maxIndex = 0;
        for (int i=0; i<params.length; i++) {
            if (params[maxIndex] < params[i]) {
                maxIndex = i;
            }
        }

        return new Result(maxIndex, params[maxIndex]);
    }

    public class Result {
        private int index;
        private double maxValue;

        public Result(int index, double maxValue) {
            this.index = index;
            this.maxValue = maxValue;
        }

        public int getIndex() {
            return index;
        }

        public double getMaxValue() {
            return maxValue;
        }
    }
}
