package lu.circl.mispbump.activities;


import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
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

    private UUID syncUUID;
    private PreferenceManager preferenceManager;
    private SyncInformation syncInformation;

    private View.OnClickListener onUploadClicked = v -> {
        preferenceManager.addSyncInformation(syncInformation);
        Intent upload = new Intent(SyncInfoDetailActivity.this, UploadActivity.class);
        upload.putExtra(UploadActivity.EXTRA_SYNC_INFO_UUID, syncInformation.getUuid().toString());
        startActivity(upload);
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sync_info_detail);

        preferenceManager = PreferenceManager.getInstance(SyncInfoDetailActivity.this);
        syncUUID = getExtraUuid();
        syncInformation = preferenceManager.getSyncInformation(syncUUID);

        if (syncInformation == null) {
            throw new RuntimeException("Could not find UploadInformation with UUID {" + syncUUID + "}");
        }

        initToolbar();
        initViews();
        populateContent();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_sync_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        //            preferenceManager.removeUploadInformation(syncUUID);
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        } else return item.getItemId() == R.id.menu_delete_sync;

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

    private void initViews() {
        FloatingActionButton uploadFab = findViewById(R.id.fab_main);
        uploadFab.setOnClickListener(onUploadClicked);
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
            syncInformation.setHasUnpublishedChanges(true);
        });

        MaterialPreferenceSwitch allowPush = findViewById(R.id.switch_allow_push);
        allowPush.setChecked(syncInformation.getRemote().getServer().getPush());
        allowPush.setOnCheckedChangeListener((cb, b) -> {
            syncInformation.getRemote().getServer().setPush(b);
            syncInformation.setHasUnpublishedChanges(true);
        });

        MaterialPreferenceSwitch allowPull = findViewById(R.id.switch_allow_pull);
        allowPull.setChecked(syncInformation.getRemote().getServer().getPull());
        allowPull.setOnCheckedChangeListener((cb, b) -> {
            syncInformation.getRemote().getServer().setPull(b);
            syncInformation.setHasUnpublishedChanges(true);
        });

        MaterialPreferenceSwitch allowCache = findViewById(R.id.switch_allow_cache);
        allowCache.setChecked(syncInformation.getRemote().getServer().getCachingEnabled());
        allowCache.setOnCheckedChangeListener((cb, b) -> {
            syncInformation.getRemote().getServer().setCachingEnabled(b);
            syncInformation.setHasUnpublishedChanges(true);
        });

        // credentials

        MaterialPreferenceText email = findViewById(R.id.email);
        email.setSubtitle(syncInformation.getLocal().getSyncUser().getEmail());

        MaterialPasswordView password = findViewById(R.id.password);
        password.setPassword(syncInformation.getLocal().getSyncUser().getPassword());

        MaterialPasswordView authkey = findViewById(R.id.authkey);
        authkey.setPassword(syncInformation.getLocal().getSyncUser().getAuthkey());
    }
}
