package lu.circl.mispbump.auxiliary;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

/**
 * Creates and show dialogs.
 * Automatically takes care of using the UI Thread.
 */
public class DialogManager {

    /**
     * Interface to give feedback about the user choice in dialogs.
     */
    public interface IDialogFeedback {
        void positive();
        void negative();
    }

    /**
     * Dialog to display a received public key.
     * @param publicKey the public key to display
     * @param context needed to build and show the dialog
     * @param callback {@link IDialogFeedback}
     */
    public static void publicKeyDialog(String publicKey, Context context, final IDialogFeedback callback) {
        final AlertDialog.Builder adb = new AlertDialog.Builder(context);
        adb.setTitle("Public Key Received");
        adb.setMessage(publicKey);
        adb.setPositiveButton("Accept", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                callback.positive();
            }
        });

        adb.setNegativeButton("Reject", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                callback.negative();
            }
        });

        Activity act = (Activity) context;

        act.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adb.create().show();
            }
        });
    }

    /**
     * Dialog to ask the user if his sync partner already scanned the displayed qr code.
     * @param context needed to build and show the dialog
     * @param callback {@link IDialogFeedback}
     */
    public static void confirmProceedDialog(Context context, final IDialogFeedback callback) {
        final AlertDialog.Builder adb = new AlertDialog.Builder(context);
        adb.setTitle("Really continue?");
        adb.setMessage("Was this QR Code already scanned by your partner?");
        adb.setPositiveButton("Yes, continue", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                callback.positive();
            }
        });

        adb.setNegativeButton("No, show QR Code", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                callback.negative();
            }
        });

        Activity act = (Activity) context;

        act.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adb.create().show();
            }
        });
    }
}
