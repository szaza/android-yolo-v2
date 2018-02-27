package org.tensorflow.yolo.util.comparator;

import android.util.Size;

import java.util.Comparator;

/**
 * Compares two {@code Size}s based on their areas.
 *
 * Created by Zoltan Szabo on 1/13/18.
 * URL: https://github.com/szaza/android-yolo-v2
 */
public class CompareSizesByArea implements Comparator<Size> {
    @Override
    public int compare(final Size lhs, final Size rhs) {
        // We cast here to ensure the multiplications won't overflow
        return Long.signum((long) lhs.getWidth() * lhs.getHeight()
                - (long) rhs.getWidth() * rhs.getHeight());
    }
}
