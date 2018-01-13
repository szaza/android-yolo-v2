/* Copyright 2016 The TensorFlow Authors. All Rights Reserved.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package org.tensorflow.demo.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import org.tensorflow.demo.Config;
import org.tensorflow.demo.model.Recognition;

import java.util.LinkedList;
import java.util.List;

/**
 * A simple View providing a render callback to other classes.
 * Modified by Zoltan Szabo
 */
public class OverlayView extends View {
    private final static float OVERLAP_THRESHOLD = 0.5F;
    private final Paint paint;
    private final List<DrawCallback> callbacks = new LinkedList();
    private List<Recognition> results;
    private float resultsViewHeight;

    public OverlayView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        paint = new Paint();
        paint.setColor(Color.GREEN);
        paint.setStyle(Paint.Style.STROKE);
        paint.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                15, getResources().getDisplayMetrics()));
        resultsViewHeight = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                112, getResources().getDisplayMetrics());
    }

    public void addCallback(final DrawCallback callback) {
        callbacks.add(callback);
    }

    @Override
    public synchronized void onDraw(final Canvas canvas) {
        for (final DrawCallback callback : callbacks) {
            callback.drawCallback(canvas);
        }

        if (results != null && results.size() > 0) {
            RectF bestBox = reCalcSize(results.get(0).getLocation());
            drawBoundingBox(canvas, bestBox, "0:" + results.get(0).getTitle() + ":"
                    + String.format("%.2f", results.get(0).getConfidence()));

            if (results.size() > 1) {
                for (int i = 1; i < results.size(); i++) {
                    RectF box = reCalcSize(results.get(i).getLocation());
                    if (getIntersectionProportion(bestBox, box) < OVERLAP_THRESHOLD) {
                        drawBoundingBox(canvas, box, i + ":" +results.get(i).getTitle() + ":"
                                + String.format("%.2f", results.get(0).getConfidence()));
                    }
                }
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
        void drawCallback(final Canvas canvas);
    }

    private void drawBoundingBox(final Canvas canvas, RectF box, String title) {
        canvas.drawRect(box, paint);
        canvas.drawText(title, box.left, box.top, paint);
    }

    private float getIntersectionProportion(RectF primaryShape, RectF secondaryShape) {
        if (overlaps(primaryShape, secondaryShape)) {
            float intersectionSurface = Math.max(0, Math.min(primaryShape.right, secondaryShape.right) - Math.max(primaryShape.left, secondaryShape.left)) *
                    Math.max(0, Math.min(primaryShape.bottom, secondaryShape.bottom) - Math.max(primaryShape.top, secondaryShape.top));

            float surfacePrimary = Math.abs(primaryShape.right - primaryShape.left) * Math.abs(primaryShape.bottom - primaryShape.top);

            return intersectionSurface / surfacePrimary;
        }

        return 0f;

    }

    private boolean overlaps(RectF primary, RectF secondary) {
        return primary.left < secondary.right && primary.right > secondary.left
                && primary.top < secondary.bottom && primary.bottom > secondary.top;
    }

    private RectF reCalcSize(RectF rect) {
        float sizeMultiplierX = (float) this.getWidth() / (float) Config.INPUT_SIZE;
        float sizeMultiplierY = (this.getHeight() - resultsViewHeight) / (float) Config.INPUT_SIZE;

        float width = rect.right * sizeMultiplierX;
        float height = rect.bottom * sizeMultiplierY;

        float left = sizeMultiplierX * rect.left;
        float top = sizeMultiplierY * rect.top + resultsViewHeight;

        float right = left + width;
        float bottom = top + height;

        return new RectF(left, top, right, bottom);
    }
}
