package lu.circl.mispbump.auxiliary;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import java.security.PublicKey;

import lu.circl.mispbump.R;
import lu.circl.mispbump.models.SyncInformation;
import lu.circl.mispbump.security.DiffieHellman;

/**
 * Creates and show dialogs.
 * Automatically takes care of using the UI Thread.
 */
public class DialogManager {

    /**
     * Dialog to display a received public key.
     *
     * @param publicKey the public key to display
     * @param context   needed to build and show the dialog
     * @param callback  {@link IDialogFeedback}
     */
    public static void publicKeyDialog(PublicKey publicKey, Context context, final IDialogFeedback callback) {
        final AlertDialog.Builder adb = new AlertDialog.Builder(context);
        adb.setTitle("Public Key");

        String message = "Algorithm: " + publicKey.getAlgorithm() + "\n" +
                "Format: " + publicKey.getFormat() + "\n" +
                "Content: \n" + DiffieHellman.publicKeyToString(publicKey);

        adb.setMessage(message);
        adb.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (callback != null) {
                    callback.positive();
                }
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
     * Dialog to display a received public key.
     *
     * @param syncInformation {@link SyncInformation}
     * @param context   needed to build and show the dialog
     * @param callback  {@link IDialogFeedback}
     */
    public static void syncInformationDialog(SyncInformation syncInformation, Context context, final IDialogFeedback callback) {
        final AlertDialog.Builder adb = new AlertDialog.Builder(context);
        adb.setTitle("Sync information received");
        adb.setMessage(syncInformation.organisation.name);
        adb.setPositiveButton("Accept", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (callback != null) {
                    callback.positive();
                }
            }
        });

        adb.setNegativeButton("Reject", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (callback != null) {
                    callback.negative();
                }
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
     *
     * @param context  needed to build and show the dialog
     * @param callback {@link IDialogFeedback}
     */
    public static void confirmProceedDialog(Context context, final IDialogFeedback callback) {
        final AlertDialog.Builder adb = new AlertDialog.Builder(context);
        adb.setTitle("Continue?");
        adb.setMessage("Only continue if your partner already scanned this QR code");
        adb.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (callback != null) {
                    callback.positive();
                }
            }
        });

        adb.setNegativeButton("Show QR code again", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (callback != null) {
                    callback.negative();
                }
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
     * Dialog to provide login information.
     *
     * @param context needed to build and show the dialog
     */
    public static void loginHelpDialog(Context context) {
        final AlertDialog.Builder adb = new AlertDialog.Builder(context);
        adb.setTitle(R.string.app_name);
        adb.setMessage(R.string.login_help_text);
        adb.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
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
     * Interface to give feedback about the user choice in dialogs.
     */
    public interface IDialogFeedback {
        void positive();

        void negative();
    }
}
