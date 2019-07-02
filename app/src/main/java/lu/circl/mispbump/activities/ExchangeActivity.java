package lu.circl.mispbump.activities;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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
import java.security.spec.InvalidKeySpecException;
import java.util.List;

import lu.circl.mispbump.R;
import lu.circl.mispbump.auxiliary.DialogManager;
import lu.circl.mispbump.auxiliary.PreferenceManager;
import lu.circl.mispbump.auxiliary.QrCodeGenerator;
import lu.circl.mispbump.auxiliary.RandomString;
import lu.circl.mispbump.fragments.CameraFragment;
import lu.circl.mispbump.models.SyncInformation;
import lu.circl.mispbump.models.UploadInformation;
import lu.circl.mispbump.security.DiffieHellman;

public class ExchangeActivity extends AppCompatActivity {

    private PreferenceManager preferenceManager;
    private QrCodeGenerator qrCodeGenerator;
    private DiffieHellman diffieHellman;
    private UploadInformation uploadInformation;

    private CameraFragment cameraFragment;

    private CoordinatorLayout rootLayout;
    private View qrFrame;
    private TextView qrInfo;
    private ImageView qrCode;
    private ImageButton prevButton, nextButton;

    private Bitmap publicKeyQr, dataQr;

    private SyncState currentSyncState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sync);

        preferenceManager = PreferenceManager.getInstance(ExchangeActivity.this);
        qrCodeGenerator = new QrCodeGenerator(ExchangeActivity.this);
        diffieHellman = DiffieHellman.getInstance();

        initViews();
        initCamera();

        uploadInformation = new UploadInformation();
        publicKeyQr = generatePublicKeyBitmap();
        dataQr = generateLocalSyncInfoBitmap();

        setSyncState(SyncState.KEY_EXCHANGE);
    }


    private void initViews() {
        rootLayout = findViewById(R.id.rootLayout);

        qrFrame = findViewById(R.id.qrFrame);
        qrCode = findViewById(R.id.qrCode);
        qrInfo = findViewById(R.id.qrInfo);

        prevButton = findViewById(R.id.prevButton);
        prevButton.setOnClickListener(onPrevClicked());

        nextButton = findViewById(R.id.nextButton);
        nextButton.setOnClickListener(onNextClicked());
    }

    private void initCamera() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        cameraFragment = new CameraFragment();
        cameraFragment.setOnQrAvailableListener(onQrScanned());

        String fragmentTag = cameraFragment.getClass().getSimpleName();
        fragmentTransaction.add(R.id.fragmentContainer, cameraFragment, fragmentTag);
        fragmentTransaction.commit();
    }


    private Bitmap generatePublicKeyBitmap() {
        return qrCodeGenerator.generateQrCode(DiffieHellman.publicKeyToString(diffieHellman.getPublicKey()));
    }

    private Bitmap generateLocalSyncInfoBitmap() {
        uploadInformation.setLocal(generateLocalSyncInfo());
        return qrCodeGenerator.generateQrCode(new Gson().toJson(uploadInformation.getLocal()));
    }

    private SyncInformation generateLocalSyncInfo() {
        SyncInformation syncInformation = new SyncInformation();
        syncInformation.organisation = preferenceManager.getUserOrganisation().toSyncOrganisation();
        syncInformation.syncUserAuthkey = new RandomString(40).nextString();
        syncInformation.baseUrl = preferenceManager.getServerUrl();
        syncInformation.syncUserPassword = new RandomString(16).nextString();
        syncInformation.syncUserEmail = preferenceManager.getUserInfo().email;
        return syncInformation;
    }


    private void showQrCode(final Bitmap bitmap) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                qrCode.setImageBitmap(bitmap);
                qrFrame.setVisibility(View.VISIBLE);  // TODO animate
            }
        });
    }

    private void setSyncState(SyncState state) {

        Log.d("DEBUG", "current sync state: " + state);

        currentSyncState = state;

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                switch (currentSyncState) {
                    case KEY_EXCHANGE:
                        prevButton.setImageDrawable(getDrawable(R.drawable.ic_close));
                        prevButton.setVisibility(View.VISIBLE);
                        nextButton.setVisibility(View.GONE);

                        setCameraPreviewEnabled(true);
                        setReadQrStatus(ReadQrStatus.PENDING);
                        showQrCode(publicKeyQr);
                        break;
                    case KEY_EXCHANGE_DONE:
                        prevButton.setImageDrawable(getDrawable(R.drawable.ic_close));
                        prevButton.setVisibility(View.VISIBLE);
                        nextButton.setImageDrawable(getDrawable(R.drawable.ic_arrow_forward));
                        nextButton.setVisibility(View.VISIBLE);

                        setCameraPreviewEnabled(false);
                        setReadQrStatus(ReadQrStatus.SUCCESS);
                        showQrCode(publicKeyQr);
                        break;
                    case DATA_EXCHANGE:
                        prevButton.setImageDrawable(getDrawable(R.drawable.ic_arrow_back));
                        prevButton.setVisibility(View.VISIBLE);
                        nextButton.setVisibility(View.GONE);

                        setCameraPreviewEnabled(true);
                        setReadQrStatus(ReadQrStatus.PENDING);
                        showQrCode(dataQr);
                        break;
                    case DATA_EXCHANGE_DONE:
                        prevButton.setImageDrawable(getDrawable(R.drawable.ic_arrow_back));
                        prevButton.setVisibility(View.VISIBLE);
                        nextButton.setImageDrawable(getDrawable(R.drawable.ic_check));
                        nextButton.setVisibility(View.VISIBLE);

                        setCameraPreviewEnabled(false);
                        setReadQrStatus(ReadQrStatus.SUCCESS);
                        showQrCode(dataQr);
                        break;
                }
            }
        });
    }

    private void setReadQrStatus(ReadQrStatus status) {

        Log.d("DEBUG", "QR STATUS: " + status);

        final Drawable drawable;
        final int color;

        switch (status) {
            case PENDING:
                drawable = getDrawable(R.drawable.ic_info_outline);
                color = getColor(R.color.status_amber);
                break;
            case SUCCESS:
                drawable = getDrawable(R.drawable.ic_check_outline);
                color = getColor(R.color.status_green);
                break;
            case FAILURE:
                drawable = getDrawable(R.drawable.ic_error_outline);
                color = getColor(R.color.status_red);
                break;
            default:
                drawable = getDrawable(R.drawable.ic_info_outline);
                color = getColor(R.color.status_green);
                break;
        }

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                qrInfo.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
                qrInfo.setCompoundDrawableTintList(ColorStateList.valueOf(color));
            }
        });
    }

    private void setCameraPreviewEnabled(boolean enabled) {
        View view = findViewById(R.id.fragmentContainer);

        if (enabled) {
            Log.d("DEBUG", "cameraPreview enabled");
            view.animate()
                    .alpha(1f)
                    .setDuration(250)
                    .start();
            cameraFragment.setReadQrEnabled(true);
        } else {
            Log.d("DEBUG", "cameraPreview disabled");
            view.animate()
                    .alpha(0f)
                    .setDuration(250)
                    .start();
            cameraFragment.setReadQrEnabled(false);
        }
    }


    private CameraFragment.QrScanCallback onQrScanned() {
        return new CameraFragment.QrScanCallback() {
            @Override
            public void qrScanResult(String qrData) {
                cameraFragment.setReadQrEnabled(false);

                switch (currentSyncState) {
                    case KEY_EXCHANGE:
                        try {
                            diffieHellman.setForeignPublicKey(DiffieHellman.publicKeyFromString(qrData));
                            setSyncState(SyncState.KEY_EXCHANGE_DONE);
                        } catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
                            setReadQrStatus(ReadQrStatus.FAILURE);
                            cameraFragment.setReadQrEnabled(true);
                            Snackbar.make(rootLayout, "Public key not parsable", Snackbar.LENGTH_LONG).show();
                        }
                        break;
                    case DATA_EXCHANGE:
                        try {
                            final SyncInformation remoteSyncInfo = new Gson().fromJson(diffieHellman.decrypt(qrData), SyncInformation.class);
                            List<UploadInformation> uploadInformationList = preferenceManager.getUploadInformationList();

                            for (final UploadInformation ui : uploadInformationList) {
                                if (ui.getRemote().organisation.uuid.equals(remoteSyncInfo.organisation.uuid)) {
                                    DialogManager.syncAlreadyExistsDialog(ui.getRemote(), remoteSyncInfo, ExchangeActivity.this, new DialogManager.IDialogFeedback() {
                                        @Override
                                        public void positive() {
                                            // update remote info only
                                            uploadInformation.setUuid(ui.getUuid());
                                            uploadInformation.setDate();
                                        }

                                        @Override
                                        public void negative() {
                                            // replace credentials too
                                            preferenceManager.removeUploadInformation(ui.getUuid());
                                        }
                                    });

                                    break;
                                }
                            }

                            uploadInformation.setRemote(remoteSyncInfo);
                            preferenceManager.addUploadInformation(uploadInformation);
                            setSyncState(SyncState.DATA_EXCHANGE_DONE);
                        } catch (JsonSyntaxException e) {
                            setReadQrStatus(ReadQrStatus.FAILURE);
                            cameraFragment.setReadQrEnabled(true);
                            Snackbar.make(rootLayout, "Sync information not parsable", Snackbar.LENGTH_LONG).show();
                        }
                        break;
                }
            }
        };
    }

    private View.OnClickListener onPrevClicked() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (currentSyncState) {
                    case KEY_EXCHANGE:
                    case KEY_EXCHANGE_DONE:
                        // TODO warning that sync will be lost
                        finish();
                        break;
                    case DATA_EXCHANGE:
                    case DATA_EXCHANGE_DONE:
                        setSyncState(SyncState.KEY_EXCHANGE_DONE);
                        break;
                }
            }
        };
    }

    private View.OnClickListener onNextClicked() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (currentSyncState) {
                    case KEY_EXCHANGE_DONE:
                        setSyncState(SyncState.DATA_EXCHANGE);
                        break;
                    case DATA_EXCHANGE_DONE:
                        uploadInformation.setCurrentSyncStatus(UploadInformation.SyncStatus.PENDING);
                        preferenceManager.addUploadInformation(uploadInformation);
                        Intent i = new Intent(ExchangeActivity.this, UploadInfoActivity.class);
                        i.putExtra(UploadInfoActivity.EXTRA_UPLOAD_INFO_UUID, uploadInformation.getUuid());
                        startActivity(i);
                        finish();
                        break;
                }
            }
        };
    }


    private enum SyncState {
        KEY_EXCHANGE,
        KEY_EXCHANGE_DONE,
        DATA_EXCHANGE,
        DATA_EXCHANGE_DONE
    }

    private enum ReadQrStatus {
        PENDING,
        SUCCESS,
        FAILURE
    }
}
