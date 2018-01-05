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
==============================================================================*/

package org.tensorflow.demo.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import org.tensorflow.demo.Config;
import org.tensorflow.demo.model.Recognition;

import java.util.LinkedList;
import java.util.List;

/**
 * A simple View providing a render callback to other classes.
 */
public class OverlayView extends View {
    private final Paint paint;
    private final List<DrawCallback> callbacks = new LinkedList<DrawCallback>();
    private List<Recognition> results;

    public OverlayView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.GREEN);
    }

    public void addCallback(final DrawCallback callback) {
        callbacks.add(callback);
    }

    @Override
    public synchronized void draw(final Canvas canvas) {
        for (final DrawCallback callback : callbacks) {
            callback.drawCallback(canvas);
        }

        if (Config.CLASSIFIER.startsWith("Yolo") && results != null) {
            for (final Recognition recog : results) {
//                canvas.drawRect(reCalcSize(recog.getLocation()), paint);
                canvas.drawRect(recog.getLocation(), paint);
            }
        }
    }

    public void setResults(final List<Recognition> results) {
        this.results = results;
        postInvalidate();
    }

    /**
     * Interface defining the callback for client classes.
     */
    public interface DrawCallback {
        public void drawCallback(final Canvas canvas);
    }

    private RectF reCalcSize(RectF rect) {
        float size_multiplier_x = this.getWidth() / Config.INPUT_SIZE;
        float size_multiplier_y = size_multiplier_x;
        float offset_x = 0;
        float offset_y = (this.getHeight() - Config.INPUT_SIZE * size_multiplier_y) / 2;

        float width = rect.right * size_multiplier_x;
        float height = rect.bottom * size_multiplier_y;

        float left = size_multiplier_x * rect.left + offset_x - width;
        float top = size_multiplier_y * rect.top + offset_y - height;

        float right = left + 2 * width;
        float bottom = top + 2 * height;

        return new RectF(left,top,right,bottom);
    }
}
