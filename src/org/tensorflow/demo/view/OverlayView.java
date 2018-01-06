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
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import org.tensorflow.demo.Config;
import org.tensorflow.demo.R;
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
    private float resultsViewHeight;

    public OverlayView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.GREEN);
        resultsViewHeight = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                112, getResources().getDisplayMetrics());
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
                canvas.drawRect(reCalcSize(recog.getLocation()), paint);
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
        float sizeMultiplierX = (float) this.getWidth() / (float) Config.INPUT_SIZE;
        float sizeMultiplierY = (float) (this.getHeight() - resultsViewHeight) / (float) Config.INPUT_SIZE;

        float width = rect.right * sizeMultiplierX;
        float height = rect.bottom * sizeMultiplierY;

        float left = sizeMultiplierX * rect.left;
        float top = sizeMultiplierY * rect.top + resultsViewHeight;

        float right = left + width;
        float bottom = top + height;

        return new RectF(left,top,right,bottom);
    }
}
