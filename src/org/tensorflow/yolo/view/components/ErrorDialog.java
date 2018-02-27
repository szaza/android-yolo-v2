package org.tensorflow.yolo.view.components;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

/**
 * Created by Zoltan Szabo on 1/13/18.
 *
 * Shows an error message dialog.
 * URL: https://github.com/szaza/android-yolo-v2
 */
public class ErrorDialog extends DialogFragment {
    private static final String ARG_MESSAGE = "message";

    public static ErrorDialog newInstance(final String message) {
        final ErrorDialog dialog = new ErrorDialog();
        final Bundle args = new Bundle();
        args.putString(ARG_MESSAGE, message);
        dialog.setArguments(args);
        return dialog;
    }

    @Override
    public Dialog onCreateDialog(final Bundle savedInstanceState) {
        final Activity activity = getActivity();
        return new AlertDialog.Builder(activity)
                .setMessage(getArguments().getString(ARG_MESSAGE))
                .setPositiveButton(android.R.string.ok,
                        (final DialogInterface dialogInterface, final int i) -> activity.finish())
                .create();
    }
}
