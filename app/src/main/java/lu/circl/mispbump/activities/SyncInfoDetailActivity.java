package lu.circl.mispbump.activities;


import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroupOverlay;
import android.widget.CheckBox;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.UUID;

import lu.circl.mispbump.R;
import lu.circl.mispbump.auxiliary.PreferenceManager;
import lu.circl.mispbump.customViews.MaterialPasswordView;
import lu.circl.mispbump.customViews.MaterialPreferenceText;
import lu.circl.mispbump.models.SyncInformation;


public class SyncInfoDetailActivity extends AppCompatActivity {

    public static String EXTRA_SYNC_INFO_UUID = "EXTRA_SYNC_INFO_UUID";

    private PreferenceManager preferenceManager;
    private SyncInformation syncInformation;

    private boolean fabMenuExpanded;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sync_info_detail);

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

        uploadLayout.setVisibility(View.GONE);
        downloadLayout.setVisibility(View.GONE);

        fab.setOnClickListener(view -> {
            if (fabMenuExpanded) {
                uploadLayout.setVisibility(View.GONE);
                downloadLayout.setVisibility(View.GONE);

                fabMenuExpanded = false;
            } else {
                uploadLayout.setVisibility(View.VISIBLE);
                downloadLayout.setVisibility(View.VISIBLE);

                fabMenuExpanded = true;
            }
        });

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
        name.setSubtitle(syncInformation.getRemoteOrganisation().getName());

        MaterialPreferenceText uuid = findViewById(R.id.uuid);
        uuid.setSubtitle(syncInformation.getRemoteOrganisation().getUuid());

        MaterialPreferenceText sector = findViewById(R.id.sector);
        sector.setSubtitle(syncInformation.getRemoteOrganisation().getSector());

        MaterialPreferenceText description = findViewById(R.id.description);
        description.setSubtitle(syncInformation.getRemoteOrganisation().getDescription());

        // settings

        CheckBox allowSelfSigned = findViewById(R.id.checkbox_self_signed);
        allowSelfSigned.setChecked(syncInformation.getSyncServer().getSelf_signed());
        allowSelfSigned.setOnCheckedChangeListener((compoundButton, b) -> {
            syncInformation.getSyncServer().setSelf_signed(b);

        });

        CheckBox push = findViewById(R.id.checkbox_push);
        push.setChecked(syncInformation.getSyncServer().getPush());
        push.setOnCheckedChangeListener((compoundButton, b) -> syncInformation.getSyncServer().setPush(b));

        CheckBox pull = findViewById(R.id.checkbox_pull);
        pull.setChecked(syncInformation.getSyncServer().getPull());
        pull.setOnCheckedChangeListener((compundButton, b) -> syncInformation.getSyncServer().setPull(b));

        CheckBox cache = findViewById(R.id.checkbox_cache);
        cache.setChecked(syncInformation.getSyncServer().getCaching_enabled());
        cache.setOnCheckedChangeListener((compoundButton, b) -> syncInformation.getSyncServer().setCaching_enabled(b));

        // credentials

        MaterialPreferenceText email = findViewById(R.id.email);
        email.setSubtitle(syncInformation.getLocal().getSyncUser().getEmail());

        MaterialPasswordView password = findViewById(R.id.password);
        password.setPassword(syncInformation.getLocal().getSyncUser().getPassword());

        MaterialPasswordView authkey = findViewById(R.id.authkey);
        authkey.setPassword(syncInformation.getLocal().getSyncUser().getAuthkey());
    }


    public static void applyDim(@NonNull ViewGroup parent, float dimAmount) {
//      ViewGroup root = (ViewGroup) getWindow().getDecorView().getRootView();
        Drawable dim = new ColorDrawable(Color.BLACK);
        dim.setBounds(0, 0, parent.getWidth(), parent.getHeight());

        ValueAnimator valueAnimator = ValueAnimator.ofFloat(dimAmount);

        valueAnimator.addUpdateListener(valueAnim -> {
            float value = (float) valueAnim.getAnimatedValue();
            dim.setAlpha((int) (255 * value));
            ViewGroupOverlay overlay = parent.getOverlay();
            overlay.add(dim);
        });

        valueAnimator.start();
    }

    public static void clearDim(@NonNull ViewGroup parent) {
        ViewGroupOverlay overlay = parent.getOverlay();
        overlay.clear();
    }
}
