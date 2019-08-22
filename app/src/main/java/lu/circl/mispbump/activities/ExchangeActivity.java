package lu.circl.mispbump.activities;


import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import lu.circl.mispbump.R;
import lu.circl.mispbump.auxiliary.PreferenceManager;
import lu.circl.mispbump.auxiliary.QrCodeGenerator;
import lu.circl.mispbump.fragments.CameraFragment;
import lu.circl.mispbump.models.ExchangeInformation;
import lu.circl.mispbump.models.SyncInformation;
import lu.circl.mispbump.models.restModels.Server;
import lu.circl.mispbump.security.DiffieHellman;


public class ExchangeActivity extends AppCompatActivity {

    private PreferenceManager preferenceManager;
    private QrCodeGenerator qrCodeGenerator;
    private DiffieHellman diffieHellman;

    private CameraFragment cameraFragment;

    private ConstraintLayout rootLayout;
    private View qrFrame, scanFeedbackView, continueHintView, fragmentContainer;
    private TextView scanFeedbackText, qrContentInfo;
    private ImageView qrCode;
    private ImageButton prevButton, nextButton;

    private SyncInformation syncInformation;

    private Bitmap publicKeyQr, dataQr;

    private SyncState currentSyncState;
    private ReadQrStatus currentReadQrStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exchange);

        preferenceManager = PreferenceManager.getInstance(ExchangeActivity.this);
        qrCodeGenerator = new QrCodeGenerator(ExchangeActivity.this);
        diffieHellman = DiffieHellman.getInstance();

        initViews();
        initCamera();

        publicKeyQr = generatePublicKeyBitmap();

        syncInformation = new SyncInformation();

        setSyncState(SyncState.KEY_EXCHANGE);
    }


    private void initViews() {
        rootLayout = findViewById(R.id.rootLayout);
        fragmentContainer = findViewById(R.id.fragmentContainer);

        qrFrame = findViewById(R.id.qrFrame);
        qrCode = findViewById(R.id.qrCode);

        scanFeedbackView = findViewById(R.id.scanFeedbackView);
        scanFeedbackText = findViewById(R.id.scanFeedbackText);
        continueHintView = findViewById(R.id.continueHint);
        qrContentInfo = findViewById(R.id.qrContentInfo);

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

    private ExchangeInformation generateSyncExchangeInformation() {
        ExchangeInformation exchangeInformation = new ExchangeInformation();
        exchangeInformation.setOrganisation(preferenceManager.getUserOrganisation().toSyncOrganisation());
        exchangeInformation.setSyncUser(preferenceManager.getUserInfo().toSyncUser());
        exchangeInformation.setServer(new Server(preferenceManager.getUserCredentials().first));
        return exchangeInformation;
    }


    private Bitmap generatePublicKeyBitmap() {
        return qrCodeGenerator.generateQrCode(DiffieHellman.publicKeyToString(diffieHellman.getPublicKey()));
    }

    private Bitmap generateLocalSyncInfoBitmap() {
        ExchangeInformation exchangeInformation = generateSyncExchangeInformation();
        syncInformation.setLocal(exchangeInformation);
        return qrCodeGenerator.generateQrCode(diffieHellman.encrypt(new Gson().toJson(exchangeInformation)));
    }


    private void showQrCode(final Bitmap bitmap) {
        runOnUiThread(() -> {
            qrCode.setImageBitmap(bitmap);
            qrFrame.setVisibility(View.VISIBLE);
        });
    }

    private void setSyncState(SyncState state) {
        currentSyncState = state;

        runOnUiThread(() -> {
            switch (currentSyncState) {
                case KEY_EXCHANGE:
                    prevButton.setImageDrawable(getDrawable(R.drawable.ic_close));
                    prevButton.setVisibility(View.VISIBLE);
                    nextButton.setVisibility(View.GONE);

                    setCameraPreviewEnabled(true);
                    showQrCode(publicKeyQr);

                    setReadQrStatus(ReadQrStatus.PENDING);
                    scanFeedbackText.setText(R.string.scan_qr_hint);
                    qrContentInfo.setText(R.string.public_key);
                    break;
                case KEY_EXCHANGE_DONE:
                    prevButton.setImageDrawable(getDrawable(R.drawable.ic_close));
                    prevButton.setVisibility(View.VISIBLE);
                    nextButton.setImageDrawable(getDrawable(R.drawable.ic_arrow_forward));
                    nextButton.setVisibility(View.VISIBLE);

                    setCameraPreviewEnabled(false);
                    showQrCode(publicKeyQr);

                    setReadQrStatus(ReadQrStatus.SUCCESS);
                    scanFeedbackText.setText(R.string.public_key_received_hint);
                    qrContentInfo.setText(R.string.public_key);
                    break;
                case DATA_EXCHANGE:
                    prevButton.setImageDrawable(getDrawable(R.drawable.ic_arrow_back));
                    prevButton.setVisibility(View.VISIBLE);
                    nextButton.setVisibility(View.GONE);

                    setCameraPreviewEnabled(true);
                    showQrCode(dataQr);

                    setReadQrStatus(ReadQrStatus.PENDING);
                    scanFeedbackText.setText(R.string.scan_qr_hint);
                    qrContentInfo.setText(R.string.sync_information);
                    break;
                case DATA_EXCHANGE_DONE:
                    prevButton.setImageDrawable(getDrawable(R.drawable.ic_arrow_back));
                    prevButton.setVisibility(View.VISIBLE);
                    nextButton.setImageDrawable(getDrawable(R.drawable.ic_check));
                    nextButton.setVisibility(View.VISIBLE);

                    setCameraPreviewEnabled(false);
                    showQrCode(dataQr);

                    setReadQrStatus(ReadQrStatus.SUCCESS);
                    scanFeedbackText.setText(R.string.sync_info_received_hint);
                    qrContentInfo.setText(R.string.public_key);
                    break;
            }
        });
    }

    private void setReadQrStatus(ReadQrStatus status) {
        currentReadQrStatus = status;

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
                color = getColor(R.color.status_amber);
                break;
        }

        scanFeedbackText.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
        scanFeedbackText.setCompoundDrawableTintList(ColorStateList.valueOf(color));

        if (currentReadQrStatus == ReadQrStatus.SUCCESS) {
            continueHintView.setVisibility(View.VISIBLE);
            scanFeedbackView.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.white)));
            qrFrame.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.white)));

            fragmentContainer.animate().alpha(0).setDuration(250).start();
        } else {
            continueHintView.setVisibility(View.GONE);
            scanFeedbackView.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.white_80)));
            qrFrame.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.white_80)));

            fragmentContainer.animate().alpha(1).setDuration(250).start();
        }
    }

    private void setCameraPreviewEnabled(boolean enabled) {
        View view = findViewById(R.id.fragmentContainer);

        if (enabled) {
            view.animate()
                    .alpha(1f)
                    .setDuration(250)
                    .start();
            cameraFragment.setReadQrEnabled(true);
        } else {
            view.animate()
                    .alpha(0f)
                    .setDuration(250)
                    .start();
            cameraFragment.setReadQrEnabled(false);
        }
    }


    private CameraFragment.QrScanCallback onQrScanned() {
        return qrData -> {
            cameraFragment.setReadQrEnabled(false);

            switch (currentSyncState) {
                case KEY_EXCHANGE:
                    try {
                        diffieHellman.setForeignPublicKey(DiffieHellman.publicKeyFromString(qrData));
                        setSyncState(SyncState.KEY_EXCHANGE_DONE);
                        dataQr = generateLocalSyncInfoBitmap();
                    } catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
                        if (currentReadQrStatus == ReadQrStatus.PENDING) {
                            setReadQrStatus(ReadQrStatus.FAILURE);
                            Snackbar.make(rootLayout, "Public key not parsable", Snackbar.LENGTH_LONG).show();
                        }

                        cameraFragment.setReadQrEnabled(true);
                    }
                    break;
                case DATA_EXCHANGE:
                    try {
                        ExchangeInformation remoteSyncInfo = new Gson().fromJson(diffieHellman.decrypt(qrData), ExchangeInformation.class);
                        syncInformation.populateRemoteExchangeInformation(remoteSyncInfo);
                        preferenceManager.addSyncInformation(syncInformation);
                        setSyncState(SyncState.DATA_EXCHANGE_DONE);
                    } catch (JsonSyntaxException e) {
                        if (currentReadQrStatus == ReadQrStatus.PENDING) {
                            setReadQrStatus(ReadQrStatus.FAILURE);
                            Snackbar.make(rootLayout, "Sync information not parsable", Snackbar.LENGTH_LONG).show();
                        }

                        cameraFragment.setReadQrEnabled(true);
                    }
                    break;
            }
        };
    }

    private View.OnClickListener onPrevClicked() {
        return v -> {
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
        };
    }

    private View.OnClickListener onNextClicked() {
        return v -> {
            switch (currentSyncState) {
                case KEY_EXCHANGE_DONE:
                    setSyncState(SyncState.DATA_EXCHANGE);
                    break;
                case DATA_EXCHANGE_DONE:
                    Intent i = new Intent(ExchangeActivity.this, SyncInfoDetailActivity.class);
                    i.putExtra(SyncInfoDetailActivity.EXTRA_SYNC_INFO_UUID, syncInformation.getUuid());
                    startActivity(i);
                    finish();
                    break;
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
