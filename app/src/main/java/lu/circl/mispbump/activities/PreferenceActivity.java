package lu.circl.mispbump.activities;


import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import lu.circl.mispbump.R;
import lu.circl.mispbump.auxiliary.PreferenceManager;


public class PreferenceActivity extends AppCompatActivity {

    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preference);

        preferenceManager = PreferenceManager.getInstance(PreferenceActivity.this);

        initializeViews();
    }

    private void initializeViews() {
        Toolbar myToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);

        ActionBar ab = getSupportActionBar();
        assert ab != null;
        ab.setDisplayHomeAsUpEnabled(true);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        PreferencesFragment preferencesFragment = new PreferencesFragment();
        preferencesFragment.onDeleteAllSyncsListener = preference -> {
            preferenceManager.clearUploadInformation();
            return true;
        };

        fragmentTransaction.add(R.id.fragmentContainer, preferencesFragment, PreferencesFragment.class.getSimpleName());
        fragmentTransaction.commit();
    }

    public static class PreferencesFragment extends PreferenceFragmentCompat {

        Preference.OnPreferenceClickListener onDeleteAllSyncsListener;

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.preference_screen_main, rootKey);

            Preference deleteAllSyncInfo = findPreference("PREF_DELETE_ALL_SYNCS");
            assert deleteAllSyncInfo != null;
            deleteAllSyncInfo.setOnPreferenceClickListener(onDeleteAllSyncsListener);
        }
    }
}
