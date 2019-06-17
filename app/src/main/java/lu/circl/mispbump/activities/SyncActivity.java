package lu.circl.mispbump.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;

import lu.circl.mispbump.R;
import lu.circl.mispbump.auxiliary.PreferenceManager;
import lu.circl.mispbump.auxiliary.QrCodeGenerator;
import lu.circl.mispbump.auxiliary.RandomString;
import lu.circl.mispbump.cam.CameraFragment;
import lu.circl.mispbump.custom_views.ExtendedBottomSheetBehavior;
import lu.circl.mispbump.fragments.SyncOptionsFragment;
import lu.circl.mispbump.models.SyncInformation;
import lu.circl.mispbump.models.UploadInformation;
import lu.circl.mispbump.security.DiffieHellman;

/**
 * This class provides the sync functionality.
 * It collects the necessary information, guides through the process and finally completes with
 * the upload to the misp instance.
 */
public class SyncActivity extends AppCompatActivity {

    // layout
    private CoordinatorLayout layout;
    private ImageView qrCodeView, bottomSheetIcon;
    private TextView bottomSheetText;
    private ImageButton prevButton, nextButton;
    private ExtendedBottomSheetBehavior bottomSheetBehavior;

    // dependencies
    private PreferenceManager preferenceManager;
    private DiffieHellman diffieHellman;

    private UploadInformation uploadInformation;

    // fragments
    private CameraFragment cameraFragment;
    private SyncOptionsFragment syncOptionsFragment;

    // qr codes
    private QrCodeGenerator qrCodeGenerator;
    private Bitmap publicKeyQr, syncInfoQr;

    private SyncState currentSyncState = SyncState.settings;

    private enum SyncState {
        settings(0),
        publicKeyExchange(1),
        dataExchange(2);


        private final int value;

        SyncState(final int value) {
            this.value = value;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sync);
        initializeViews();
    }

    private void initializeViews() {
        // Root Layout
        layout = findViewById(R.id.rootLayout);

        // prev button
        prevButton = findViewById(R.id.prevButton);
        prevButton.setOnClickListener(onPrevClicked);

        // next button
        nextButton = findViewById(R.id.nextButton);
        nextButton.setOnClickListener(onNextClicked);

        // QR Code View
        qrCodeView = findViewById(R.id.qrcode);
        qrCodeGenerator = new QrCodeGenerator(SyncActivity.this);

        bottomSheetIcon = findViewById(R.id.bottomSheetIcon);
        bottomSheetText = findViewById(R.id.bottomSheetText);

        diffieHellman = DiffieHellman.getInstance();
        preferenceManager = PreferenceManager.getInstance(this);

        View bottomSheet = findViewById(R.id.bottomSheet);
        bottomSheetBehavior = (ExtendedBottomSheetBehavior) BottomSheetBehavior.from(bottomSheet);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        bottomSheetBehavior.setSwipeable(false);
        bottomSheetBehavior.setHideable(false);

        publicKeyQr = generatePublicKeyQr();

        switchState(SyncState.settings);
    }

    /**
     * Called when "next button" is pressed
     */
    private View.OnClickListener onNextClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (currentSyncState) {
                case settings:
                    uploadInformation = new UploadInformation();
                    uploadInformation.setAllowSelfSigned(syncOptionsFragment.getAllowSelfSigned());
                    uploadInformation.setPush(syncOptionsFragment.getPush());
                    uploadInformation.setPull(syncOptionsFragment.getPull());
                    uploadInformation.setCached(syncOptionsFragment.getCache());

                    switchState(SyncState.publicKeyExchange);
                    break;

                case publicKeyExchange:
                    switchState(SyncState.dataExchange);
                    break;

                case dataExchange:
                    Intent upload = new Intent(SyncActivity.this, UploadActivity.class);
                    upload.putExtra(UploadActivity.EXTRA_UPLOAD_INFO, new Gson().toJson(uploadInformation));
                    startActivity(upload);
                    overridePendingTransition(R.anim.slide_in_right, android.R.anim.slide_out_right);
                    finish();
                    break;
            }
        }
    };

    /**
     * Called when "prev button" is clicked
     */
    private View.OnClickListener onPrevClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (currentSyncState) {
                case settings:
                    finish();
                    break;

                case publicKeyExchange:
                    switchState(SyncState.settings);
                    break;

                case dataExchange:
                    switchState(SyncState.publicKeyExchange);
                    break;
            }
        }
    };

    /**
     * Called when the camera fragment detects a qr code
     */
    private CameraFragment.QrScanCallback onQrCodeScanned = new CameraFragment.QrScanCallback() {
        @Override
        public void qrScanResult(String qrData) {
            cameraFragment.setReadQrEnabled(false);
            switch (currentSyncState) {
                case publicKeyExchange:
                    try {
                        final PublicKey pk = DiffieHellman.publicKeyFromString(qrData);
                        diffieHellman.setForeignPublicKey(pk);

                        syncInfoQr = generateSyncInfoQr();

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                nextButton.setVisibility(View.VISIBLE);
                                cameraFragment.disablePreview();
                                qrReceivedFeedback();
                            }
                        });
                    } catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
                        Snackbar.make(layout, "Invalid key", Snackbar.LENGTH_SHORT).show();
                        cameraFragment.setReadQrEnabled(true);
                    }
                    break;

                case dataExchange:
                    cameraFragment.setReadQrEnabled(false);

                    try {
                        final SyncInformation remoteSyncInfo = new Gson().fromJson(diffieHellman.decrypt(qrData), SyncInformation.class);
                        uploadInformation.setRemote(remoteSyncInfo);

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                cameraFragment.disablePreview();
                                nextButton.setVisibility(View.VISIBLE);
                                qrReceivedFeedback();
                            }
                        });

                    } catch (JsonSyntaxException e) {
                        Snackbar.make(layout, "Sync information unreadable", Snackbar.LENGTH_SHORT).show();
                        cameraFragment.setReadQrEnabled(true);
                    }
                    break;
            }
        }
    };


    private void switchUiState(SyncState state) {

        bottomSheetIcon.setVisibility(View.INVISIBLE);
        bottomSheetBehavior.setSwipeable(false);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

        switch (state) {
            case settings:
                prevButton.setImageDrawable(getDrawable(R.drawable.ic_close));
                prevButton.setVisibility(View.VISIBLE);
                nextButton.setVisibility(View.VISIBLE);
                hideQrCode();
                break;
            case publicKeyExchange:
                prevButton.setImageDrawable(getDrawable(R.drawable.ic_arrow_back));
                prevButton.setVisibility(View.VISIBLE);

                nextButton.setImageDrawable(getDrawable(R.drawable.ic_arrow_forward));
                nextButton.setVisibility(View.GONE);
                showQrCode(publicKeyQr);
                break;
            case dataExchange:
                prevButton.setImageDrawable(getDrawable(R.drawable.ic_arrow_back));
                prevButton.setVisibility(View.VISIBLE);

                nextButton.setImageDrawable(getDrawable(R.drawable.ic_cloud_upload));
                nextButton.setVisibility(View.GONE);

                cameraFragment.enablePreview();
                cameraFragment.setReadQrEnabled(true);
                showQrCode(syncInfoQr);
                break;
        }
    }

    private void switchState(SyncState state) {

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        if (currentSyncState != state) {
            if (state.value < currentSyncState.value) {
                transaction.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            } else {
                transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        }

        currentSyncState = state;

        switchUiState(currentSyncState);

        switch (currentSyncState) {
            case settings:
                String fragTag = SyncOptionsFragment.class.getSimpleName();
                syncOptionsFragment = (SyncOptionsFragment) fragmentManager.findFragmentByTag(fragTag);

                if (syncOptionsFragment == null) {
                    syncOptionsFragment = new SyncOptionsFragment();
                }

                transaction.replace(R.id.sync_fragment_container, syncOptionsFragment, fragTag);
                transaction.commit();
                break;

            case publicKeyExchange:
                fragTag = CameraFragment.class.getSimpleName();
                cameraFragment = (CameraFragment) fragmentManager.findFragmentByTag(fragTag);

                if (cameraFragment == null) {
                    cameraFragment = new CameraFragment();
                    cameraFragment.setOnQrAvailableListener(onQrCodeScanned);
                }

                transaction.replace(R.id.sync_fragment_container, cameraFragment, fragTag);
                transaction.commit();
                break;

            case dataExchange:
                fragTag = CameraFragment.class.getSimpleName();
                cameraFragment = (CameraFragment) fragmentManager.findFragmentByTag(fragTag);

                if (cameraFragment == null) {
                    cameraFragment = new CameraFragment();
                    cameraFragment.setOnQrAvailableListener(onQrCodeScanned);
                }

                transaction.replace(R.id.sync_fragment_container, cameraFragment, fragTag);
                transaction.commit();
                break;
        }
    }


    private Bitmap generatePublicKeyQr() {
        return qrCodeGenerator.generateQrCode(DiffieHellman.publicKeyToString(diffieHellman.getPublicKey()));
    }

    private Bitmap generateSyncInfoQr() {
        SyncInformation syncInformation = new SyncInformation();
        syncInformation.organisation = preferenceManager.getUserOrganisation().toSyncOrganisation();
        syncInformation.syncUserAuthkey = new RandomString(40).nextString();
        syncInformation.baseUrl = preferenceManager.getServerUrl();
        syncInformation.syncUserPassword = new RandomString(16).nextString();
        syncInformation.syncUserEmail = preferenceManager.getUserInfo().email;

        uploadInformation.setLocal(syncInformation);

        // encrypt serialized content
        String encrypted = diffieHellman.encrypt(new Gson().toJson(syncInformation));

        // generate QR code
        return qrCodeGenerator.generateQrCode(encrypted);
    }


    private void showQrCode(final Bitmap bitmap) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                qrCodeView.setImageBitmap(bitmap);
                qrCodeView.setAlpha(0f);
                qrCodeView.setVisibility(View.VISIBLE);
                qrCodeView.setScaleX(0.9f);
                qrCodeView.setScaleY(0.6f);
                qrCodeView.animate()
                        .scaleX(1f)
                        .scaleY(1f)
                        .alpha(1f)
                        .setDuration(250)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                qrCodeView.setVisibility(View.VISIBLE);
                            }
                        });
            }
        });
    }

    private void hideQrCode() {

        if (qrCodeView.getVisibility() == View.GONE) {
            return;
        }

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                qrCodeView.setAlpha(1f);
                qrCodeView.setVisibility(View.VISIBLE);
                qrCodeView.setScaleX(1f);
                qrCodeView.setScaleY(1f);
                qrCodeView.animate()
                        .scaleX(0f)
                        .scaleY(0f)
                        .alpha(0f)
                        .setDuration(250)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                qrCodeView.setVisibility(View.GONE);
                            }
                        });
            }
        });
    }

    private void qrReceivedFeedback() {
        bottomSheetIcon.setScaleX(0f);
        bottomSheetIcon.setScaleY(0f);
        bottomSheetIcon.setVisibility(View.VISIBLE);
        bottomSheetIcon.animate()
                .scaleY(1f)
                .scaleX(1f)
                .setDuration(250);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        bottomSheetBehavior.setSwipeable(true);

        switch (currentSyncState) {
            case publicKeyExchange:
                bottomSheetText.setText("Received public key from partner");
                break;

            case dataExchange:
                bottomSheetText.setText("Received sync information from partner");
                break;
        }
    }

}
