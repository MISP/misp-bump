package lu.circl.mispbump.activities;

import android.content.Intent;
import android.os.Bundle;
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
import lu.circl.mispbump.auxiliary.DialogManager;
import lu.circl.mispbump.auxiliary.PreferenceManager;
import lu.circl.mispbump.fragments.UploadCredentialsFragment;
import lu.circl.mispbump.fragments.UploadSettingsFragment;
import lu.circl.mispbump.models.UploadInformation;

public class UploadInfoActivity extends AppCompatActivity {

    public static String EXTRA_UPLOAD_INFO_UUID = "uploadInformationUuid";

    private PreferenceManager preferenceManager;
    private UploadInformation uploadInformation;
    private ViewPagerAdapter viewPagerAdapter;

    private FloatingActionButton fab;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_information);

        preferenceManager = PreferenceManager.getInstance(UploadInfoActivity.this);

        // tint statusBar
        getWindow().setStatusBarColor(getColor(R.color.colorPrimary));
//        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        parseExtra();
        initToolbar();
        initViewPager();
        initViews();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // refresh current uploadInformation
        if (uploadInformation != null) {
            uploadInformation = preferenceManager.getUploadInformation(uploadInformation.getUuid());
            initContent();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        saveCurrentSettings();
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
                DialogManager.deleteSyncInformationDialog(UploadInfoActivity.this, new DialogManager.IDialogFeedback() {
                    @Override
                    public void positive() {
                        preferenceManager.removeUploadInformation(uploadInformation.getUuid());
                        finish();
                    }

                    @Override
                    public void negative() {}
                });

                return true;

            case android.R.id.home:
                saveCurrentSettings();
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
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

        TextView toolbarTitle = findViewById(R.id.toolbarTitle);
        toolbarTitle.setText(uploadInformation.getRemote().organisation.getName());

        ab.setHomeAsUpIndicator(R.drawable.ic_close);

        ab.setDisplayShowTitleEnabled(false);
        ab.setDisplayHomeAsUpEnabled(true);
    }

    private void initViewPager() {
        ViewPager viewPager = findViewById(R.id.viewPager);
        TabLayout tabLayout = findViewById(R.id.tabLayout);

        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), uploadInformation);
        viewPager.setAdapter(viewPagerAdapter);

        viewPager.addOnPageChangeListener(onPageChangeListener());

        tabLayout.setupWithViewPager(viewPager);
    }

    private void initViews() {
        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // for the UploadActivity to have the latest settings of this UploadInfoObject
                saveCurrentSettings();

                Intent i = new Intent(UploadInfoActivity.this, UploadActivity.class);
                i.putExtra(UploadActivity.EXTRA_UPLOAD_INFO, uploadInformation.getUuid());
                startActivity(i);
            }
        });
    }

    private void initContent() {
        switch (uploadInformation.getCurrentSyncStatus()) {
            case COMPLETE:

                break;

            case FAILURE:

                break;

            case PENDING:

                break;

            default:

                break;
        }
    }

    private void saveCurrentSettings() {
        uploadInformation.setAllowSelfSigned(viewPagerAdapter.uploadSettingsFragment.getAllowSelfSigned());
        uploadInformation.setPull(viewPagerAdapter.uploadSettingsFragment.getPull());
        uploadInformation.setPush(viewPagerAdapter.uploadSettingsFragment.getPush());
        uploadInformation.setCached(viewPagerAdapter.uploadSettingsFragment.getCache());

        preferenceManager.addUploadInformation(uploadInformation);
    }


    private ViewPager.OnPageChangeListener onPageChangeListener() {
        return new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (position == 0) {
                    float scale = (1 - positionOffset);
                    fab.setScaleX(scale);
                    fab.setScaleY(scale);
                }
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        };
    }


    class ViewPagerAdapter extends FragmentPagerAdapter {

        private UploadSettingsFragment uploadSettingsFragment;
        private UploadCredentialsFragment uploadCredentialsFragment;

        ViewPagerAdapter(@NonNull FragmentManager fm, UploadInformation uploadInformation) {
            super(fm, ViewPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
            uploadSettingsFragment = new UploadSettingsFragment(uploadInformation);
            uploadCredentialsFragment = new UploadCredentialsFragment(uploadInformation);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return uploadSettingsFragment;

                case 1:
                    return uploadCredentialsFragment;

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
                    return getString(R.string.settings);

                case 1:
                    return getString(R.string.credentials);

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
