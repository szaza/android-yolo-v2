/* Copyright 2018 The Android YOLOv2 sample application Authors.
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
package org.tensorflow.yolo.model;

/**
 * Model to store the position of the bounding boxes
 *
 * Created by Zoltan Szabo on 1/14/18.
 * URL: https://github.com/szaza/android-yolo-v2
 */

public class BoxPosition {
    private float left;
    private float top;
    private float right;
    private float bottom;
    private float width;
    private float height;

    public BoxPosition(float left, float top, float width, float height) {
        this.left = left;
        this.top = top;
        this.width = width;
        this.height = height;

        init();
    }

    public BoxPosition(BoxPosition boxPosition) {
        this.left = boxPosition.left;
        this.top = boxPosition.top;
        this.width = boxPosition.width;
        this.height = boxPosition.height;

        init();
    }

    public void init() {
        float tmpLeft = this.left;
        float tmpTop = this.top;
        float tmpRight = this.left + this.width;
        float tmpBottom = this.top + this.height;

        this.left = Math.min(tmpLeft, tmpRight); // left should have lower value as right
        this.top = Math.min(tmpTop, tmpBottom);  // top should have lower value as bottom
        this.right = Math.max(tmpLeft, tmpRight);
        this.bottom = Math.max(tmpTop, tmpBottom);
    }

    public float getLeft() {
        return left;
    }

    public float getTop() {
        return top;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    public float getRight() {
        return right;
    }

    public float getBottom() {
        return bottom;
    }

    @Override
    public String toString() {
        return "BoxPosition{" +
                "left=" + left +
                ", top=" + top +
                ", width=" + width +
                ", height=" + height +
                '}';
    }
}
