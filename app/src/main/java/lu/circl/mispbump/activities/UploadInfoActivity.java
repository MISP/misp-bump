package lu.circl.mispbump.activities;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import java.util.UUID;

import lu.circl.mispbump.R;
import lu.circl.mispbump.auxiliary.PreferenceManager;
import lu.circl.mispbump.fragments.UploadInfoFragment;
import lu.circl.mispbump.fragments.UploadSettingsFragment;
import lu.circl.mispbump.models.UploadInformation;

public class UploadInfoActivity extends AppCompatActivity {

    public static String EXTRA_UPLOAD_INFO_UUID = "uploadInformation";

    private PreferenceManager preferenceManager;
    private UploadInformation uploadInformation;
    private ViewPagerAdapter viewPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_information_2);

        preferenceManager = PreferenceManager.getInstance(UploadInfoActivity.this);

        // tint statusBar
        getWindow().setStatusBarColor(getColor(R.color.grey_light));
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        parseExtra();
        initToolbar();
        initViewPager();
        initViews();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_upload_info, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delete:
                // TODO delete
                return true;

            case android.R.id.home:
                saveCurrentSettings();
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        // refresh current uploadInformation
        if (uploadInformation != null) {
            uploadInformation = preferenceManager.getUploadInformation(uploadInformation.getUuid());
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        saveCurrentSettings();
    }


    private void parseExtra() {
        Intent i = getIntent();

        UUID currentUUID = (UUID) i.getSerializableExtra(EXTRA_UPLOAD_INFO_UUID);
        uploadInformation = preferenceManager.getUploadInformation(currentUUID);

        if (uploadInformation == null) {
            throw new RuntimeException("Could not find UploadInformation with UUID {" + currentUUID.toString() + "}");
        }
    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar ab = getSupportActionBar();
        assert ab != null;


        TextView tv = findViewById(R.id.syncStatus);
        int statusColor;
        String statusText;
        Drawable statusDrawable;

        switch (uploadInformation.getCurrentSyncStatus()) {
            case COMPLETE:
                statusColor = getColor(R.color.status_green);
                statusText = "Successfully uploaded";
                statusDrawable = getDrawable(R.drawable.ic_check_outline);
                break;

            case FAILURE:
                statusColor = getColor(R.color.status_red);
                statusText = "Error while uploading";
                statusDrawable = getDrawable(R.drawable.ic_error_outline);
                break;

            case PENDING:
                statusColor = getColor(R.color.status_amber);
                statusText = "Not uploaded yet";
                statusDrawable = getDrawable(R.drawable.ic_pending);
                break;

            default:
                statusColor = getColor(R.color.status_green);
                statusText = "Successfully uploaded";
                statusDrawable = getDrawable(R.drawable.ic_check_outline);
                break;
        }

        tv.setText(statusText);
        tv.setTextColor(statusColor);
        tv.setCompoundDrawablesWithIntrinsicBounds(null, null, statusDrawable, null);
        tv.setCompoundDrawableTintList(ColorStateList.valueOf(statusColor));

        ab.setTitle(uploadInformation.getRemote().organisation.name);

        ab.setDisplayShowTitleEnabled(true);
        ab.setDisplayShowCustomEnabled(false);
        ab.setDisplayHomeAsUpEnabled(true);
    }

    private void initViewPager() {
        ViewPager viewPager = findViewById(R.id.viewPager);
        TabLayout tabLayout = findViewById(R.id.tabLayout);

        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), uploadInformation);

        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void initViews() {
        FloatingActionButton fab = findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                saveCurrentSettings();

                Intent i = new Intent(UploadInfoActivity.this, UploadActivity.class);
                i.putExtra(UploadActivity.EXTRA_UPLOAD_INFO, uploadInformation.getUuid());
                startActivity(i);
            }
        });
    }

    private void saveCurrentSettings() {
        uploadInformation.setAllowSelfSigned(viewPagerAdapter.uploadSettingsFragment.getAllowSelfSigned());
        uploadInformation.setPull(viewPagerAdapter.uploadSettingsFragment.getPull());
        uploadInformation.setPush(viewPagerAdapter.uploadSettingsFragment.getPush());
        uploadInformation.setCached(viewPagerAdapter.uploadSettingsFragment.getCache());

        preferenceManager.addUploadInformation(uploadInformation);
    }


    class ViewPagerAdapter extends FragmentPagerAdapter {

        private UploadSettingsFragment uploadSettingsFragment;
        private UploadInfoFragment uploadInfoFragment = new UploadInfoFragment();

        ViewPagerAdapter(@NonNull FragmentManager fm, UploadInformation uploadInformation) {
            super(fm, ViewPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
            uploadSettingsFragment = new UploadSettingsFragment(uploadInformation);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return uploadSettingsFragment;

                case 1:
                    return uploadInfoFragment;

                default:
                    uploadSettingsFragment = new UploadSettingsFragment();
                    return uploadSettingsFragment;
            }
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Permissions";

                case 1:
                    return "Credentials";

                default:
                    return "N/A";
            }
        }

        @Override
        public int getCount() {
            return 2;
        }
    }
}
