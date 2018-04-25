package org.tensorflow.yolo.util;

import android.graphics.Matrix;
import android.media.Image;

import java.nio.ByteBuffer;

/**
 * Utility class for manipulating images.
 * Modified by Zoltan Szabo
 * URL: https://github.com/szaza/android-yolo-v2
 **/
public class ImageUtils {
    // This value is 2 ^ 18 - 1, and is used to clamp the RGB values before their ranges
    // are normalized to eight bits.
    static final int kMaxChannelValue = 262143;

    public static int[] convertYUVToARGB(final Image image, final int previewWidth, final int previewHeight) {
        final Image.Plane[] planes = image.getPlanes();
        byte[][] yuvBytes = fillBytes(planes);
        return ImageUtils.convertYUV420ToARGB8888(yuvBytes[0], yuvBytes[1], yuvBytes[2], previewWidth,
                previewHeight, planes[0].getRowStride(), planes[1].getRowStride(), planes[1].getPixelStride());
    }

    private static byte[][] fillBytes(final Image.Plane[] planes) {
        byte[][] yuvBytes = new byte[3][];
        for (int i = 0; i < planes.length; ++i) {
            final ByteBuffer buffer = planes[i].getBuffer();
            if (yuvBytes[i] == null) {
                yuvBytes[i] = new byte[buffer.capacity()];
            }
            buffer.get(yuvBytes[i]);
        }

        return yuvBytes;
    }

    private static int[] convertYUV420ToARGB8888(byte[] yData, byte[] uData, byte[] vData, int width, int height,
                                                 int yRowStride, int uvRowStride, int uvPixelStride) {
        int[] out = new int[width * height];
        int i = 0;
        for (int y = 0; y < height; y++) {
            int pY = yRowStride * y;
            int uv_row_start = uvRowStride * (y >> 1);
            int pU = uv_row_start;
            int pV = uv_row_start;

            for (int x = 0; x < width; x++) {
                int uv_offset = (x >> 1) * uvPixelStride;
                out[i++] = YUV2RGB(
                        convertByteToInt(yData, pY + x),
                        convertByteToInt(uData, pU + uv_offset),
                        convertByteToInt(vData, pV + uv_offset));
            }
        }

        return out;
    }

    private static int convertByteToInt(byte[] arr, int pos) {
        return arr[pos] & 0xFF;
    }

    private static int YUV2RGB(int nY, int nU, int nV) {
        nY -= 16;
        nU -= 128;
        nV -= 128;
        if (nY < 0) nY = 0;

        int nR = 1192 * nY + 1634 * nV;
        int nG = 1192 * nY - 833 * nV - 400 * nU;
        int nB = 1192 * nY + 2066 * nU;

        nR = Math.min(kMaxChannelValue, Math.max(0, nR));
        nG = Math.min(kMaxChannelValue, Math.max(0, nG));
        nB = Math.min(kMaxChannelValue, Math.max(0, nB));

        nR = (nR >> 10) & 0xff;
        nG = (nG >> 10) & 0xff;
        nB = (nB >> 10) & 0xff;

        return 0xff000000 | (nR << 16) | (nG << 8) | nB;
    }

    public static Matrix getTransformationMatrix(final int srcWidth, final int srcHeight,
                                                 final int dstWidth, final int dstHeight,
                                                 final int applyRotation, final boolean maintainAspectRatio) {
        final Matrix matrix = new Matrix();

        if (applyRotation != 0) {
            // Translate so center of image is at origin.
            matrix.postTranslate(-srcWidth / 2.0f, -srcHeight / 2.0f);

            // Rotate around origin.
            matrix.postRotate(applyRotation);
        }

        // Account for the already applied rotation, if any, and then determine how
        // much scaling is needed for each axis.
        final boolean transpose = (Math.abs(applyRotation) + 90) % 180 == 0;

        final int inWidth = transpose ? srcHeight : srcWidth;
        final int inHeight = transpose ? srcWidth : srcHeight;

        // Apply scaling if necessary.
        if (inWidth != dstWidth || inHeight != dstHeight) {
            final float scaleFactorX = dstWidth / (float) inWidth;
            final float scaleFactorY = dstHeight / (float) inHeight;

            if (maintainAspectRatio) {
                // Scale by minimum factor so that dst is filled completely while
                // maintaining the aspect ratio. Some image may fall off the edge.
                final float scaleFactor = Math.max(scaleFactorX, scaleFactorY);
                matrix.postScale(scaleFactor, scaleFactor);
            } else {
                // Scale exactly to fill dst from src.
                matrix.postScale(scaleFactorX, scaleFactorY);
            }
        }

        if (applyRotation != 0) {
            // Translate back from origin centered reference to destination frame.
            matrix.postTranslate(dstWidth / 2.0f, dstHeight / 2.0f);
        }

        return matrix;
    }
}