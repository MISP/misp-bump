package de.overview.wg.its.mispbump;

import android.animation.Animator;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.*;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.gson.Gson;
import de.overview.wg.its.mispbump.auxiliary.AESSecurity;
import de.overview.wg.its.mispbump.auxiliary.PreferenceManager;
import de.overview.wg.its.mispbump.auxiliary.RandomString;
import de.overview.wg.its.mispbump.auxiliary.TempAuth;
import de.overview.wg.its.mispbump.cam.CameraFragment;
import de.overview.wg.its.mispbump.model.*;
import net.glxn.qrgen.android.QRCode;
import org.json.JSONException;

public class QrSyncActivity extends AppCompatActivity implements View.OnClickListener {

    private enum ScanState {
        public_key,
        information
    }

    private ScanState currentScanState;

    private FloatingActionButton proceedToSyncInfoFab, proceedToSyncUploadFab;
    private PreferenceManager preferenceManager;
    private View qrBackground;
    private ImageView qrImage;
    private CameraFragment cameraFragment;
    private AESSecurity cryptography;

    private SyncInformationQr receivedSyncInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_qr_sync);

        initializeContent();
        startPublicKeyExchange();
    }

    @Override
    public void onClick(View v) {

        int id = v.getId();

        switch (id) {

            case R.id.fab_continue_sync_info:
                acceptProceedDialog(new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startSyncInformationExchange();
                    }
                });
                break;

            case R.id.fab_continue_sync_upload:

                acceptProceedDialog(new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startSyncUpload();
                    }
                });

                break;

            case R.id.close:
                finish();
                break;
        }
    }


    private void initializeContent() {

        proceedToSyncInfoFab = findViewById(R.id.fab_continue_sync_info);
        proceedToSyncInfoFab.hide();
        proceedToSyncInfoFab.setOnClickListener(this);

        proceedToSyncUploadFab = findViewById(R.id.fab_continue_sync_upload);
        proceedToSyncUploadFab.hide();
        proceedToSyncUploadFab.setOnClickListener(this);

        ImageButton close = findViewById(R.id.close);
        close.setOnClickListener(this);

        qrBackground = findViewById(R.id.qr_background);
        qrBackground.setVisibility(View.INVISIBLE);
        qrImage = findViewById(R.id.qr_imageView);

        preferenceManager = PreferenceManager.Instance(this);
        cryptography = AESSecurity.getInstance();

        cameraFragment = new CameraFragment();
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        String camTag = cameraFragment.getClass().getSimpleName();
        transaction.replace(R.id.fragment_container, cameraFragment, camTag);
        transaction.commit();

    }

    private void startPublicKeyExchange() {

        currentScanState = ScanState.public_key;

        User myUser = preferenceManager.getMyUser();
        Organisation myOrg = preferenceManager.getMyOrganisation();
        String pubKey = AESSecurity.publicKeyToString(cryptography.getPublicKey());

        PublicKeyQr publicKeyQr = new PublicKeyQr(myOrg.getName(), myUser.getEmail(), pubKey);

        showQr(publicKeyQr.toJSON().toString());

        cameraFragment.setReadQrEnabled(true);
    }

    private void receivedPublicKey(PublicKeyQr publicKeyQr) {
        cryptography.setForeignPublicKey(AESSecurity.publicKeyFromString(publicKeyQr.getKey()));

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                proceedToSyncInfoFab.show();
            }
        });
    }

    private void startSyncInformationExchange() {

        currentScanState = ScanState.information;

        Organisation myOrg = preferenceManager.getMyOrganisation();

        proceedToSyncInfoFab.setVisibility(View.GONE);
        cameraFragment.setReadQrEnabled(true);

        TempAuth.TMP_AUTH_KEY = new RandomString(40).nextString();

        Server serverForMeOnOtherInstance = new Server();
        serverForMeOnOtherInstance.setAuthkey(TempAuth.TMP_AUTH_KEY);
        serverForMeOnOtherInstance.setName("SyncServer for " + myOrg.getName());
        serverForMeOnOtherInstance.setUrl(preferenceManager.getMyServerUrl());

        final SyncInformationQr siqr = new SyncInformationQr(
                preferenceManager.getMyOrganisation(),
                serverForMeOnOtherInstance,
                preferenceManager.getMyUser());


        showQr(cryptography.encrypt(siqr.toJSON().toString()));

        cameraFragment.setReadQrEnabled(true);

    }

    private void receivedSyncInformation(SyncInformationQr syncInformationQr) {

        receivedSyncInfo = syncInformationQr;

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                proceedToSyncUploadFab.setVisibility(View.VISIBLE);
            }
        });

    }

    public void onReadQrCode(String qrData) {

        switch (currentScanState) {
            case public_key:

                try {

                    publicKeyReceivedDialog(new PublicKeyQr(qrData));

                } catch (JSONException e) {
                    notExpectedFormatDialog();
                }

                break;

            case information:

                try {

                    syncInformationReceivedDialog(new SyncInformationQr(qrData));

                } catch (JSONException e) {

                    notExpectedFormatDialog();

                }

                break;
        }

        cameraFragment.setReadQrEnabled(false);
    }

    private void showQr(String qrData) {
        generateQr(qrData);

        if (qrBackground.getVisibility() == View.VISIBLE) {                          // First close if visible
            circularReveal(qrBackground, false, 300, 0);     // close directly
            circularReveal(qrBackground, true, 300, 350);    // open 250ms later
        } else {
            circularReveal(qrBackground, true, 300, 0);      // if not visible just open directly
        }
    }

    private void hideQr() {
        if (qrBackground.getVisibility() == View.VISIBLE) {
            circularReveal(qrBackground, false, 200, 0);
        }
    }

    private void generateQr(String data) {
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        int width = (int) (size.x * 0.8f);

        //noinspection SuspiciousNameCombination
        qrImage.setImageBitmap(QRCode.from(data)
                .withColor(0xFF000000, 0x00FFFFFF)
                .withSize(width, width)
                .bitmap());
    }

    private void startSyncUpload() {
        Intent i = new Intent(this, SyncUploadActivity.class);
        i.putExtra(SyncUploadActivity.PARTNER_INFO_BUNDLE_KEY, new Gson().toJson(receivedSyncInfo));
        startActivity(i);
        finish();
    }

    private void publicKeyReceivedDialog(final PublicKeyQr pkqr) {

        cameraFragment.setReadQrEnabled(false);

        AlertDialog.Builder adb = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();

        @SuppressLint("InflateParams") View title = inflater.inflate(R.layout.dialog_public_key, null);
        adb.setCustomTitle(title);

        adb.setMessage("\nOrganisation: " + pkqr.getOrganisation() + "\nEmail: " + pkqr.getEmail());

        adb.setPositiveButton(getString(R.string.accept), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                receivedPublicKey(pkqr);
            }
        });

        adb.setNegativeButton(getString(R.string.reject), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                cameraFragment.setReadQrEnabled(true);
            }
        });

        adb.setCancelable(false);

        Dialog d = adb.create();

        //noinspection ConstantConditions
        d.getWindow().setWindowAnimations(R.style.DialogAnimation);
        d.getWindow().setDimAmount(0.8f);
        d.show();
    }

    private void syncInformationReceivedDialog(final SyncInformationQr siqr) {

        cameraFragment.setReadQrEnabled(false);

        AlertDialog.Builder adb = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();

        @SuppressLint("InflateParams") View title = inflater.inflate(R.layout.dialog_sync_info, null);
        adb.setCustomTitle(title);

        @SuppressLint("InflateParams") View orgView = inflater.inflate(R.layout.view_organisation, null);

        TextView orgTitle = orgView.findViewById(R.id.organisation_title);
        orgTitle.setText(siqr.getOrganisation().getName());

        TextView orgUuid = orgView.findViewById(R.id.organisation_uuid);
        orgUuid.setText(siqr.getOrganisation().getUuid());

        TextView orgDesc = orgView.findViewById(R.id.organisation_description);
        orgDesc.setText(siqr.getOrganisation().getDescription());

        TextView orgNat = orgView.findViewById(R.id.organisation_nationality);
        orgNat.setText(siqr.getOrganisation().getNationality());

        TextView orgSec = orgView.findViewById(R.id.organisation_sector);
        orgSec.setText(siqr.getOrganisation().getSector());

        TextView orgUser = orgView.findViewById(R.id.organisation_user_count);
        orgUser.setText("" + siqr.getOrganisation().getUserCount());

        adb.setView(orgView);

        adb.setPositiveButton(getString(R.string.accept), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                receivedSyncInformation(siqr);
            }
        });

        adb.setNegativeButton(getString(R.string.reject), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                cameraFragment.setReadQrEnabled(true);
            }
        });

        Dialog d = adb.create();
        //noinspection ConstantConditions
        d.getWindow().setWindowAnimations(R.style.DialogAnimation);
        d.getWindow().setDimAmount(0.8f);

        d.show();
    }

    private void acceptProceedDialog(Dialog.OnClickListener posListener) {

        AlertDialog.Builder adb = new AlertDialog.Builder(this);

        adb.setTitle("Proceed");

        if (currentScanState == ScanState.public_key) {
            adb.setMessage("Did your sync partner already scan your Public Key?");
        } else {
            adb.setMessage("Did your sync partner already scan your Sync Information?");
        }

        adb.setPositiveButton("Yes", posListener);
        adb.setNegativeButton("No", null);

        adb.create().show();
    }

    private void notExpectedFormatDialog() {

        AlertDialog.Builder adb = new AlertDialog.Builder(this);

        switch (currentScanState) {
            case public_key:
                adb.setTitle("Public Key Expected");
                adb.setMessage("Please tell your Sync Partner to go back to the Public Key exchange");
                break;

            case information:
                adb.setTitle("Sync Information Expected");
                adb.setMessage("Please tell your Sync Partner to proceed to the Sync Information exchange");
                break;
        }

        adb.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                cameraFragment.setReadQrEnabled(true);
            }
        });

    }

    private void circularReveal(final View v, final boolean open, final long duration, final long startDelay) {

        v.post(new Runnable() {
            @Override
            public void run() {
                int cx = v.getWidth() / 2;
                int cy = v.getHeight() / 2;

                float finalRadius = (float) Math.hypot(cx, cy);

                Animator anim;

                if (open) {
                    anim = ViewAnimationUtils.createCircularReveal(v, cx, cy, 0, finalRadius);
                } else {
                    anim = ViewAnimationUtils.createCircularReveal(v, cx, cy, finalRadius, 0);
                }

                anim.setInterpolator(new DecelerateInterpolator());
                anim.setDuration(duration);

                anim.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        qrBackground.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        if (!open) {
                            qrBackground.setVisibility(View.INVISIBLE);
                        }
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                });

                anim.setStartDelay(startDelay);

                anim.start();

            }
        });

    }
}
