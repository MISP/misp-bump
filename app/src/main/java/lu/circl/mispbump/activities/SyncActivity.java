package lu.circl.mispbump.activities;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.util.List;

import lu.circl.mispbump.R;
import lu.circl.mispbump.auxiliary.DialogManager;
import lu.circl.mispbump.auxiliary.PreferenceManager;
import lu.circl.mispbump.auxiliary.QrCodeGenerator;
import lu.circl.mispbump.auxiliary.RandomString;
import lu.circl.mispbump.fragments.CameraFragment;
import lu.circl.mispbump.fragments.UploadSettingsFragment;
import lu.circl.mispbump.models.SyncInformation;
import lu.circl.mispbump.models.UploadInformation;
import lu.circl.mispbump.security.DiffieHellman;

public class SyncActivity extends AppCompatActivity {

    enum SyncState {
        PUBLIC_KEY,
        DATA
    }

    enum UiState {
        PUBLIC_KEY_SHOW,
        PUBLIC_KEY_SHOW_AND_RECEIVED,
        SYNC_INFO_SHOW,
        SYNC_INFO_SHOW_AND_RECEIVED
    }

    private SyncState currentSyncState;

    private PreferenceManager preferenceManager;
    private UploadInformation uploadInformation;
    private QrCodeGenerator qrCodeGenerator;
    private DiffieHellman diffieHellman;

    private boolean foreignPublicKeyReceived, foreignSyncInfoReceived;

    // Fragments
    private CameraFragment cameraFragment;

    // Views
    private CoordinatorLayout rootLayout;
    private FrameLayout qrFrame;
    private ImageView qrCode;
    private TextView qrHint;
    private ImageButton prevButton, nextButton;

    private Bitmap publicKeyQrCode, syncInfoQrCode;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sync);

        init();
        initViews();

        switchState(SyncState.PUBLIC_KEY);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        switch (currentSyncState) {
            case PUBLIC_KEY:
                // TODO warn that sync is maybe not complete ... ?
                break;
            case DATA:
                switchState(SyncState.PUBLIC_KEY);
                break;
        }
    }

    private void init() {
        preferenceManager = PreferenceManager.getInstance(SyncActivity.this);
        diffieHellman = DiffieHellman.getInstance();

        qrCodeGenerator = new QrCodeGenerator(SyncActivity.this);
        publicKeyQrCode = qrCodeGenerator.generateQrCode(DiffieHellman.publicKeyToString(diffieHellman.getPublicKey()));

        uploadInformation = new UploadInformation();
    }

    private void initViews() {
        rootLayout = findViewById(R.id.rootLayout);

        qrFrame = findViewById(R.id.qrFrame);
        qrCode = findViewById(R.id.qrCode);
        qrHint = findViewById(R.id.qrHint);

        prevButton = findViewById(R.id.prevButton);
        prevButton.setOnClickListener(onPrevClicked);

        nextButton = findViewById(R.id.nextButton);
        nextButton.setOnClickListener(onNextClicked);
    }

    private void switchState(SyncState state) {
        switchFragment(state);
        displayQr(state);

        switch (state) {
            case PUBLIC_KEY:
                if (foreignPublicKeyReceived) {
                    switchUiState(UiState.PUBLIC_KEY_SHOW_AND_RECEIVED);
                    cameraFragment.setReadQrEnabled(false);
                } else {
                    switchUiState(UiState.PUBLIC_KEY_SHOW);
                    cameraFragment.setReadQrEnabled(true);
                }
                break;
            case DATA:
                if (foreignSyncInfoReceived) {
                    switchUiState(UiState.SYNC_INFO_SHOW_AND_RECEIVED);
                    cameraFragment.setReadQrEnabled(false);
                } else {
                    switchUiState(UiState.SYNC_INFO_SHOW);
                    cameraFragment.setReadQrEnabled(true);
                }
                break;
        }

        currentSyncState = state;
    }

    private void switchFragment(SyncState state) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        String camTag = CameraFragment.class.getSimpleName();
        String settingsTag = UploadSettingsFragment.class.getSimpleName();

        switch (state) {
            case PUBLIC_KEY:
            case DATA:
                cameraFragment = (CameraFragment) fragmentManager.findFragmentByTag(camTag);
                if (cameraFragment != null) {
                    fragmentTransaction.show(cameraFragment);
                } else {
                    cameraFragment = new CameraFragment();
                    cameraFragment.setOnQrAvailableListener(onReadQrCode);
                    fragmentTransaction.add(R.id.fragmentContainer, cameraFragment, camTag);
                }

                UploadSettingsFragment uploadSettingsFragment = (UploadSettingsFragment) fragmentManager.findFragmentByTag(settingsTag);
                if (uploadSettingsFragment != null) {
                    fragmentTransaction.hide(uploadSettingsFragment);
                }

                fragmentTransaction.commit();
                break;
        }
    }

    private void displayQr(SyncState state) {
        switch (state) {
            case PUBLIC_KEY:
                qrCode.setImageBitmap(publicKeyQrCode);
                qrFrame.setVisibility(View.VISIBLE);
                break;
            case DATA:
                qrCode.setImageBitmap(syncInfoQrCode);
                qrFrame.setVisibility(View.VISIBLE);
                break;
        }
    }

    private void switchUiState(final UiState state) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                switch (state) {
                    case PUBLIC_KEY_SHOW:
                        prevButton.setImageDrawable(getDrawable(R.drawable.ic_close));
                        prevButton.setVisibility(View.VISIBLE);
                        nextButton.setVisibility(View.INVISIBLE);
                        qrReceivedFeedback(false);
                        break;
                    case PUBLIC_KEY_SHOW_AND_RECEIVED:
                        prevButton.setImageDrawable(getDrawable(R.drawable.ic_close));
                        nextButton.setImageDrawable(getDrawable(R.drawable.ic_arrow_forward));
                        prevButton.setVisibility(View.VISIBLE);
                        nextButton.setVisibility(View.VISIBLE);
                        cameraFragment.disablePreview();
                        qrReceivedFeedback(true);
                        break;
                    case SYNC_INFO_SHOW:
                        prevButton.setImageDrawable(getDrawable(R.drawable.ic_arrow_back));
                        nextButton.setImageDrawable(getDrawable(R.drawable.ic_arrow_forward));
                        nextButton.setVisibility(View.INVISIBLE);
                        prevButton.setVisibility(View.VISIBLE);
                        cameraFragment.enablePreview();
                        qrReceivedFeedback(false);
                        break;
                    case SYNC_INFO_SHOW_AND_RECEIVED:
                        prevButton.setImageDrawable(getDrawable(R.drawable.ic_arrow_back));
                        nextButton.setImageDrawable(getDrawable(R.drawable.ic_check));
                        nextButton.setVisibility(View.VISIBLE);
                        prevButton.setVisibility(View.VISIBLE);
                        cameraFragment.disablePreview();
                        qrReceivedFeedback(true);
                        break;
                }
            }
        });
    }

    // listener

    private View.OnClickListener onPrevClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (currentSyncState) {
                case PUBLIC_KEY:
                    finish();
                    break;
                case DATA:
                    switchState(SyncState.PUBLIC_KEY);
                    break;
            }
        }
    };

    private View.OnClickListener onNextClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (currentSyncState) {
                case PUBLIC_KEY:
                    switchState(SyncState.DATA);
                    break;

                case DATA:
                    uploadInformation.setCurrentSyncStatus(UploadInformation.SyncStatus.PENDING);
                    preferenceManager.addUploadInformation(uploadInformation);

                    Intent i = new Intent(SyncActivity.this, UploadInfoActivity.class);
                    i.putExtra(UploadInfoActivity.EXTRA_UPLOAD_INFO_UUID, uploadInformation.getUuid());
                    startActivity(i);
                    finish();
                    break;
            }
        }
    };

    private CameraFragment.QrScanCallback onReadQrCode = new CameraFragment.QrScanCallback() {
        @Override
        public void qrScanResult(String qrData) {
            cameraFragment.setReadQrEnabled(false);
            switch (currentSyncState) {
                case PUBLIC_KEY:
                    try {
                        final PublicKey pk = DiffieHellman.publicKeyFromString(qrData);
                        diffieHellman.setForeignPublicKey(pk);
                        syncInfoQrCode = generateSyncInfoQr();
                        switchUiState(UiState.PUBLIC_KEY_SHOW_AND_RECEIVED);
                        foreignPublicKeyReceived = true;
                    } catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
                        Snackbar.make(rootLayout, "Invalid key", Snackbar.LENGTH_SHORT).show();
                        switchUiState(UiState.PUBLIC_KEY_SHOW);
                    }
                    break;

                case DATA:
                    try {
                        final SyncInformation remoteSyncInfo = new Gson().fromJson(diffieHellman.decrypt(qrData), SyncInformation.class);

                        List<UploadInformation> uploadInformationList = preferenceManager.getUploadInformationList();

                        if (uploadInformationList != null) {
                            for (final UploadInformation ui : uploadInformationList) {
                                if (ui.getRemote().organisation.uuid.equals(remoteSyncInfo.organisation.uuid)) {
                                    DialogManager.syncAlreadyExistsDialog(SyncActivity.this, new DialogManager.IDialogFeedback() {
                                        @Override
                                        public void positive() {
                                            uploadInformation.setUuid(ui.getUuid());
                                        }

                                        @Override
                                        public void negative() {
                                            finish();
                                        }
                                    });
                                }
                            }
                        }

                        uploadInformation.setRemote(remoteSyncInfo);
                        switchUiState(UiState.SYNC_INFO_SHOW_AND_RECEIVED);
                        foreignSyncInfoReceived = true;
                    } catch (JsonSyntaxException e) {
                        Snackbar.make(rootLayout, "Sync information unreadable", Snackbar.LENGTH_SHORT).show();
                        switchUiState(UiState.SYNC_INFO_SHOW);
                    }
                    break;
            }
        }
    };

    // aux

    private SyncInformation generateLocalSyncInfo() {
        SyncInformation syncInformation = new SyncInformation();
        syncInformation.organisation = preferenceManager.getUserOrganisation().toSyncOrganisation();
        syncInformation.syncUserAuthkey = new RandomString(40).nextString();
        syncInformation.baseUrl = preferenceManager.getServerUrl();
        syncInformation.syncUserPassword = new RandomString(16).nextString();
        syncInformation.syncUserEmail = preferenceManager.getUserInfo().email;

        return syncInformation;
    }

    private Bitmap generateSyncInfoQr() {
        SyncInformation syncInformation = generateLocalSyncInfo();

        uploadInformation.setLocal(syncInformation);

        // encrypt serialized content
        String encrypted = diffieHellman.encrypt(new Gson().toJson(syncInformation));

        // generate QR code
        return qrCodeGenerator.generateQrCode(encrypted);
    }

    private void qrReceivedFeedback(final boolean done) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (done) {
                    qrHint.setCompoundDrawablesWithIntrinsicBounds(getDrawable(R.drawable.ic_check_outline), null, null, null);
                    qrHint.setCompoundDrawableTintList(ColorStateList.valueOf(getColor(R.color.status_green)));
                } else {
                    qrHint.setCompoundDrawablesWithIntrinsicBounds(getDrawable(R.drawable.ic_info_outline), null, null, null);
                    qrHint.setCompoundDrawableTintList(ColorStateList.valueOf(getColor(R.color.status_amber)));
                }
            }
        });
    }
}
