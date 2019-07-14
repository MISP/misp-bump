package lu.circl.mispbump.activities;

import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AnticipateOvershootInterpolator;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import lu.circl.mispbump.R;
import lu.circl.mispbump.auxiliary.PreferenceManager;
import lu.circl.mispbump.auxiliary.QrCodeGenerator;
import lu.circl.mispbump.fragments.CameraFragment;
import lu.circl.mispbump.models.UploadInformation;
import lu.circl.mispbump.security.DiffieHellman;

public class ExchangeActivity2 extends AppCompatActivity {

    private PreferenceManager preferenceManager;
    private QrCodeGenerator qrCodeGenerator;
    private DiffieHellman diffieHellman;
    private UploadInformation uploadInformation;

    private CameraFragment cameraFragment;

    private Bitmap publicKeyQr, dataQr;

    private TextView titleView, hintView;
    private View fragmentContainer;
    private ImageView qrCode;
    private ImageButton prevButton, nextButton, qrInfoButton;

    private boolean isDone = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exchange_2);

        qrCodeGenerator = new QrCodeGenerator(ExchangeActivity2.this);

        initViews();
        initCamera();


    }


    private void initViews() {
        titleView = findViewById(R.id.title);
        fragmentContainer = findViewById(R.id.fragmentContainer);
        qrCode = findViewById(R.id.qrCode);
        qrCode.setImageBitmap(qrCodeGenerator.generateQrCode("Sample content"));

        prevButton = findViewById(R.id.prevButton);
        prevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isDone = !isDone;
                toggleLayoutChange(isDone);
            }
        });

        nextButton = findViewById(R.id.nextButton);
        qrInfoButton = findViewById(R.id.qrInfoButton);
    }

    private void initCamera() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        cameraFragment = new CameraFragment();

        fragmentTransaction.add(R.id.fragmentContainer, cameraFragment, CameraFragment.class.getSimpleName());
        fragmentTransaction.commit();
    }


    private void toggleLayoutChange(final boolean done) {

        final View doneText = findViewById(R.id.scanFeedbackView);
        View constraintLayout = findViewById(R.id.qrFrame);

        if (done) {
            fragmentContainer.animate().alpha(0f).setDuration(250).start();
            doneText.setVisibility(View.VISIBLE);
            doneText.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.white)));

//            doneText.setAlpha(0);
//            doneText.setTranslationY(100);
//            doneText.animate()
//                    .alpha(1)
//                    .translationY(0)
//                    .setInterpolator(new DecelerateInterpolator())
//                    .start();
//
//            constraintLayout.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.white)));
//            constraintLayout.animate().translationY(-50).setDuration(250).start();

            nextButton.setTranslationX(200);
            nextButton.animate()
                    .translationX(0)
                    .setInterpolator(new AnticipateOvershootInterpolator())
                    .setDuration(250).start();
        } else {
            fragmentContainer.animate().alpha(1f).setDuration(250).start();
            doneText.setVisibility(View.GONE);
            doneText.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.white_80)));

//            doneText.animate()
//                    .alpha(0)
//                    .translationY(100)
//                    .start();
//
//            constraintLayout.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.white_80)));
//            constraintLayout.animate().translationY(0).setDuration(250).start();

            nextButton.setTranslationX(0);
            nextButton.animate().translationX(200).setDuration(250).start();
        }
    }
}
