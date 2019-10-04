package lu.circl.mispbump.activities;


import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.UUID;

import lu.circl.mispbump.R;
import lu.circl.mispbump.auxiliary.PreferenceManager;
import lu.circl.mispbump.customViews.MaterialPasswordView;
import lu.circl.mispbump.customViews.MaterialPreferenceSwitch;
import lu.circl.mispbump.customViews.MaterialPreferenceText;
import lu.circl.mispbump.models.SyncInformation;


public class SyncInfoDetailActivity extends AppCompatActivity {

    public static String EXTRA_SYNC_INFO_UUID = "EXTRA_SYNC_INFO_UUID";

    private PreferenceManager preferenceManager;
    private SyncInformation syncInformation;

    private boolean fabMenuExpanded;
    private boolean dataLocallyChanged;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sync_info_detail_v2);

        preferenceManager = PreferenceManager.getInstance(SyncInfoDetailActivity.this);
        syncInformation = preferenceManager.getSyncInformation(getExtraUuid());

        if (syncInformation == null) {
            throw new RuntimeException("Could not find UploadInformation with UUID {" + getExtraUuid().toString() + "}");
        }

        initToolbar();
        initFabMenu();
        populateContent();
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (dataLocallyChanged) {
            syncInformation.setSyncedWithRemote(false);
        }

        preferenceManager.addSyncInformation(syncInformation);
    }


    private UUID getExtraUuid() {
        return (UUID) getIntent().getSerializableExtra(EXTRA_SYNC_INFO_UUID);
    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar ab = getSupportActionBar();
        assert ab != null;

        ab.setDisplayHomeAsUpEnabled(true);
    }

    private void initFabMenu() {
        FloatingActionButton fab = findViewById(R.id.fab_main);
        FloatingActionButton fabUpload = findViewById(R.id.fab_upload);
        FloatingActionButton fabDownload = findViewById(R.id.fab_download);

        LinearLayout uploadLayout = findViewById(R.id.layout_upload);
        LinearLayout downloadLayout = findViewById(R.id.layout_download);

        TextView uploadText = findViewById(R.id.fab_upload_text);
        TextView downloadText = findViewById(R.id.fab_download_text);

        View menuBackground = findViewById(R.id.menu_background);

        uploadLayout.setVisibility(View.GONE);
        downloadLayout.setVisibility(View.GONE);

        int animationSpeed = 200;

        ValueAnimator openAnimation = ValueAnimator.ofFloat(0f, 1f);
        openAnimation.setDuration(animationSpeed);
        openAnimation.setInterpolator(new DecelerateInterpolator());
        openAnimation.addUpdateListener(updateAnimation -> {
            float x = (float) updateAnimation.getAnimatedValue();

            fabUpload.setAlpha(x);
            fabUpload.setTranslationY((1 - x) * 50);
            uploadText.setAlpha(x);
            uploadText.setTranslationX((1 - x) * -200);

            fabDownload.setAlpha(x);
            fabDownload.setTranslationY((1 - x) * 50);
            downloadText.setAlpha(x);
            downloadText.setTranslationX((1 - x) * -200);

            menuBackground.setAlpha(x * 0.9f);
        });
        openAnimation.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                uploadLayout.setVisibility(View.VISIBLE);
                downloadLayout.setVisibility(View.VISIBLE);
            }
            @Override
            public void onAnimationEnd(Animator animator) {
            }
            @Override
            public void onAnimationCancel(Animator animator) {

            }
            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });

        ValueAnimator closeAnimation = ValueAnimator.ofFloat(1f, 0f);
        closeAnimation.setDuration(animationSpeed);
        closeAnimation.setInterpolator(new DecelerateInterpolator());
        closeAnimation.addUpdateListener(updateAnimation -> {
            float x = (float) updateAnimation.getAnimatedValue();

            fabUpload.setAlpha(x);
            fabUpload.setTranslationY((1 - x) * 50);
            uploadText.setAlpha(x);
            uploadText.setTranslationX((1 - x) * -200);

            fabDownload.setAlpha(x);
            fabDownload.setTranslationY((1 - x) * 50);
            downloadText.setAlpha(x);
            downloadText.setTranslationX((1 - x) * -200);

            menuBackground.setAlpha(x * 0.9f);
        });
        closeAnimation.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                uploadLayout.setVisibility(View.VISIBLE);
                downloadLayout.setVisibility(View.VISIBLE);

            }
            @Override
            public void onAnimationEnd(Animator animator) {
                uploadLayout.setVisibility(View.GONE);
                downloadLayout.setVisibility(View.GONE);
            }
            @Override
            public void onAnimationCancel(Animator animator) {

            }
            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });

        AnimatedVectorDrawable open = (AnimatedVectorDrawable) getDrawable(R.drawable.animated_arrow_cloud_down);
        AnimatedVectorDrawable close = (AnimatedVectorDrawable) getDrawable(R.drawable.animated_arrow_down_cloud);

        View.OnClickListener expandCollapseClick = view -> {
            if (fabMenuExpanded) {
                menuBackground.setClickable(false);

                fab.setImageDrawable(close);
                close.start();

                closeAnimation.start();
                fabMenuExpanded = false;
            } else {
                menuBackground.setClickable(true);

                fab.setImageDrawable(open);
                open.start();

                openAnimation.start();
                fabMenuExpanded = true;
            }
        };

        menuBackground.setOnClickListener(expandCollapseClick);
        menuBackground.setClickable(false);
        fab.setOnClickListener(expandCollapseClick);

        fabUpload.setOnClickListener(view -> {
            preferenceManager.addSyncInformation(syncInformation);

            Intent upload = new Intent(SyncInfoDetailActivity.this, UploadActivity.class);
            upload.putExtra(UploadActivity.EXTRA_SYNC_INFO_UUID, syncInformation.getUuid().toString());
            startActivity(upload);
        });

        fabDownload.setOnClickListener(view -> {

        });
    }

    private void populateContent() {

        // information

        MaterialPreferenceText name = findViewById(R.id.name);
        name.setSubtitle(syncInformation.getRemote().getOrganisation().getName());

        MaterialPreferenceText uuid = findViewById(R.id.uuid);
        uuid.setSubtitle(syncInformation.getRemote().getOrganisation().getUuid());

        MaterialPreferenceText sector = findViewById(R.id.sector);
        sector.setSubtitle(syncInformation.getRemote().getOrganisation().getSector());

        MaterialPreferenceText description = findViewById(R.id.description);
        description.setSubtitle(syncInformation.getRemote().getOrganisation().getDescription());

        // settings

        MaterialPreferenceSwitch allowSelfSigned = findViewById(R.id.switch_allow_self_signed);
        allowSelfSigned.setChecked(syncInformation.getRemote().getServer().getSelfSigned());
        allowSelfSigned.setOnCheckedChangeListener((cb, b) -> {
            syncInformation.getRemote().getServer().setSelfSigned(b);
            dataLocallyChanged = true;
        });

        MaterialPreferenceSwitch allowPush = findViewById(R.id.switch_allow_push);
        allowPush.setChecked(syncInformation.getRemote().getServer().getPush());
        allowPush.setOnCheckedChangeListener((cb, b) -> {
            syncInformation.getRemote().getServer().setPush(b);
            dataLocallyChanged = true;
        });

        MaterialPreferenceSwitch allowPull = findViewById(R.id.switch_allow_pull);
        allowPull.setChecked(syncInformation.getRemote().getServer().getPull());
        allowPull.setOnCheckedChangeListener((cb, b) -> {
            syncInformation.getRemote().getServer().setPull(b);
            dataLocallyChanged = true;
        });

        MaterialPreferenceSwitch allowCache = findViewById(R.id.switch_allow_cache);
        allowCache.setChecked(syncInformation.getRemote().getServer().getCachingEnabled());
        allowCache.setOnCheckedChangeListener((cb, b) -> {
            syncInformation.getRemote().getServer().setCachingEnabled(b);
            dataLocallyChanged = true;
        });

//        CheckBox allowSelfSigned = findViewById(R.id.checkbox_self_signed);
//        allowSelfSigned.setChecked(syncInformation.getRemote().getServer().getSelfSigned());
//        allowSelfSigned.setOnCheckedChangeListener((compoundButton, b) -> {
//            syncInformation.getRemote().getServer().setSelfSigned(b);
//            dataLocallyChanged = true;
//        });

//        CheckBox push = findViewById(R.id.checkbox_push);
//        push.setChecked(syncInformation.getRemote().getServer().getPush());
//        push.setOnCheckedChangeListener((compoundButton, b) -> {
//            syncInformation.getRemote().getServer().setPush(b);
//            dataLocallyChanged = true;
//        });
//
//        CheckBox pull = findViewById(R.id.checkbox_pull);
//        pull.setChecked(syncInformation.getRemote().getServer().getPull());
//        pull.setOnCheckedChangeListener((compundButton, b) -> {
//            syncInformation.getRemote().getServer().setPull(b);
//            dataLocallyChanged = true;
//        });
//
//        CheckBox cache = findViewById(R.id.checkbox_cache);
//        cache.setChecked(syncInformation.getRemote().getServer().getCachingEnabled());
//        cache.setOnCheckedChangeListener((compoundButton, b) -> {
//            syncInformation.getRemote().getServer().setCachingEnabled(b);
//            dataLocallyChanged = true;
//        });

        // credentials

        MaterialPreferenceText email = findViewById(R.id.email);
        email.setSubtitle(syncInformation.getLocal().getSyncUser().getEmail());

        MaterialPasswordView password = findViewById(R.id.password);
        password.setPassword(syncInformation.getLocal().getSyncUser().getPassword());

        MaterialPasswordView authkey = findViewById(R.id.authkey);
        authkey.setPassword(syncInformation.getLocal().getSyncUser().getAuthkey());
    }
}
