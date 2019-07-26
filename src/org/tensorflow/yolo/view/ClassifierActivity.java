package org.tensorflow.yolo.view;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.media.Image;
import android.media.ImageReader;
import android.media.ImageReader.OnImageAvailableListener;
import android.os.SystemClock;
import android.util.Log;
import android.util.Size;
import android.util.TypedValue;

import org.tensorflow.yolo.R;
import org.tensorflow.yolo.TensorFlowImageRecognizer;
import org.tensorflow.yolo.model.Recognition;
import org.tensorflow.yolo.util.ImageUtils;
import org.tensorflow.yolo.view.components.BorderedText;

import java.util.List;
import java.util.Vector;

import static org.tensorflow.yolo.Config.INPUT_SIZE;
import static org.tensorflow.yolo.Config.LOGGING_TAG;

/**
 * Classifier activity class
 * Modified by Zoltan Szabo
 */
public class ClassifierActivity extends TextToSpeechActivity implements OnImageAvailableListener {
    private boolean MAINTAIN_ASPECT = true;
    private float TEXT_SIZE_DIP = 10;

    private TensorFlowImageRecognizer recognizer;
    private Integer sensorOrientation;
    private int previewWidth = 0;
    private int previewHeight = 0;
    private Bitmap croppedBitmap = null;
    private boolean computing = false;
    private Matrix frameToCropTransform;

    private OverlayView overlayView;
    private BorderedText borderedText;
    private long lastProcessingTimeMs;

// shimatani
    private String lastRecognizedClass = "";
    private String nowRecognizedClass = "";
    private String tts = "";
    private String msgInf1 = "";
    private String msgInf2 = "";
    private int matchCount = 0;
    private String lastResult = "";
    private String nowResult = "";

    @Override
    public void onPreviewSizeChosen(final Size size, final int rotation) {
        final float textSizePx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                TEXT_SIZE_DIP, getResources().getDisplayMetrics());
        borderedText = new BorderedText(textSizePx * 2);    // shimatani
        borderedText.setTypeface(Typeface.MONOSPACE);

        recognizer = TensorFlowImageRecognizer.create(getAssets());

        overlayView = (OverlayView) findViewById(R.id.overlay);
        previewWidth = size.getWidth();
        previewHeight = size.getHeight();

        final int screenOrientation = getWindowManager().getDefaultDisplay().getRotation();

        Log.i(LOGGING_TAG, String.format("Sensor orientation: %d, Screen orientation: %d",
                rotation, screenOrientation));

        sensorOrientation = rotation + screenOrientation;

        Log.i(LOGGING_TAG, String.format("Initializing at size %dx%d", previewWidth, previewHeight));

        croppedBitmap = Bitmap.createBitmap(INPUT_SIZE, INPUT_SIZE, Config.ARGB_8888);

        frameToCropTransform = ImageUtils.getTransformationMatrix(previewWidth, previewHeight,
                INPUT_SIZE, INPUT_SIZE, sensorOrientation, MAINTAIN_ASPECT);
        frameToCropTransform.invert(new Matrix());

        addCallback((final Canvas canvas) -> renderAdditionalInformation(canvas));
    }

    @Override
    public void onImageAvailable(final ImageReader reader) {
        Image image = null;

        try {
            image = reader.acquireLatestImage();

            if (image == null) {
                return;
            }

            if (computing) {
                image.close();
                return;
            }

            computing = true;
            fillCroppedBitmap(image);
            image.close();
        } catch (final Exception ex) {
            if (image != null) {
                image.close();
            }
            Log.e(LOGGING_TAG, ex.getMessage());
        }

        runInBackground(this::run);
    }

    private String makeTts(String nowRecognizedClass) {
        switch (nowRecognizedClass) {
            case "Ｃ型リモコン":   // 0
                msgInf1 = "Ｃ型リモコンのケースは、１３番、";
                msgInf2 = "プリント基板は１９番です。";
                break;
            case "軍手・革手":   // 1
                msgInf1 = "軍手、耐カット軍手、皮手は、";
                msgInf2 = "７番です。";
                break;
            case "マーカー": // 2
                msgInf1 = "マーカー、蛍光・水性・油性・修正ペン、";
                msgInf2 = "シャーペン、ボールペンは、１３番です。";
                break;
            case "Ｇ型リモコン":  // 3
                msgInf1 = "Ｇ型リモコンのケースは、１３番、";
                msgInf2 = "プリント基板は１９番です。";
                break;
            case "Ｅ型リモコン":    // 4
                msgInf1 = "Ｅ型リモコンのケースは、１３番、";
                msgInf2 = "プリント基板は１９番です。";
                break;
            case "ボタン電池":  // 5
                msgInf1 = "水銀を含まないボタン電池は、２３番です。";
                msgInf2 = "乾電池も２３番です。";
                break;
            case "乾電池":    // 6
                msgInf1 = "乾電池は２３番です。";
                msgInf2 = "水銀を含まないボタン電池も、２３番です。";
                break;
            case "タイラップ":    // 7
                msgInf1 = "白いタイラップは、１０番です。";
                msgInf2 = "白以外のタイラップは、１２番です。";
                break;
            case "ＰＰバンド":    // 8
                msgInf1 = "ＰＰバンドは、９番です。";
                msgInf2 = "";
                break;
            default:
                msgInf1 = "";
                msgInf2 = "";
                break;
        }
        return msgInf1 + msgInf2;
    }

    private void fillCroppedBitmap(final Image image) {
            Bitmap rgbFrameBitmap = Bitmap.createBitmap(previewWidth, previewHeight, Config.ARGB_8888);
            rgbFrameBitmap.setPixels(ImageUtils.convertYUVToARGB(image, previewWidth, previewHeight),
                    0, previewWidth, 0, 0, previewWidth, previewHeight);
            new Canvas(croppedBitmap).drawBitmap(rgbFrameBitmap, frameToCropTransform, null);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (recognizer != null) {
            recognizer.close();
        }
    }

    private void renderAdditionalInformation(final Canvas canvas) {
        final Vector<String> lines = new Vector();
        if (recognizer != null) {
            for (String line : recognizer.getStatString().split("\n")) {
                lines.add(line);
            }
        }

// shimatani
        // lines.add("Frame: " + previewWidth + "x" + previewHeight);
        // lines.add("View: " + canvas.getWidth() + "x" + canvas.getHeight());
        // lines.add("Rotation: " + sensorOrientation);
        // lines.add("Inference time: " + lastProcessingTimeMs + "ms");

        Log.d(LOGGING_TAG, String.format("検出名: %s", nowRecognizedClass));
        lines.add("検出名: " + nowRecognizedClass);

        String msgInf = makeTts(nowRecognizedClass);

        lines.add(msgInf1);
        lines.add(msgInf2);

        borderedText.drawLines(canvas, 10, 10, lines);
    }

    private void run() {
        final long startTime = SystemClock.uptimeMillis();
        final List<Recognition> results = recognizer.recognizeImage(croppedBitmap);
        lastProcessingTimeMs = SystemClock.uptimeMillis() - startTime;
        overlayView.setResults(results);

        // shimatani
        Log.d(LOGGING_TAG, "Debug: runInBackground()");

        if (!(results.isEmpty())) {
            nowResult = results.get(0).getTitle();
            Log.d(LOGGING_TAG, String.format("Find: %s", nowResult));

            if (!(results.isEmpty()) && lastResult.equals(nowResult)) {
                matchCount++;
                if (matchCount > 3) {
                    // match 4 times
                    matchCount = 0;

                    if (!(results.isEmpty() || lastRecognizedClass.equals(nowResult))) {
                        nowRecognizedClass = nowResult;
                        Log.d(LOGGING_TAG, "Match 4 times: " + nowRecognizedClass);
                        lastRecognizedClass = nowRecognizedClass;

                        tts = makeTts(nowRecognizedClass);
                        Log.d(LOGGING_TAG, "makeTts(): " + tts);
                        if (!(tts.equals(""))) {
                            speak2(results, tts);
                            tts = "";
                        }
                    }
                } else {
                    // nothing to do
                }
            } else {
                matchCount = 0;
            }
            lastResult = nowResult;
            Log.d(LOGGING_TAG, String.format("matchCount: %d", matchCount));
        }

        requestRender();
        computing = false;
    }
}
