package lu.circl.mispbump.activities;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceScreen;

import lu.circl.mispbump.R;


public class PreferenceActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preference);

        Toolbar myToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);

        ActionBar ab = getSupportActionBar();
        assert ab != null;
        ab.setDisplayHomeAsUpEnabled(true);

        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragmentContainer, new PreferencesFragment(PreferenceActivity.this))
                .commit();
    }

    public static class PreferencesFragment extends PreferenceFragmentCompat {

        private Context context;

        PreferencesFragment(Context context) {
            this.context = context;
        }

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            PreferenceScreen preferenceScreen = getPreferenceManager().createPreferenceScreen(context);
            setPreferenceScreen(preferenceScreen);

            // General

//            PreferenceCategory generalCategory = new PreferenceCategory(context);
//            generalCategory.setTitle("General");
//            getPreferenceScreen().addPreference(generalCategory);
//
//            SwitchPreference fetchOnlyLocalSyncs = new SwitchPreference(context);
//            fetchOnlyLocalSyncs.setTitle("Display local syncs only");
//            fetchOnlyLocalSyncs.setSummaryOn("Only those syncs that were made with MISPbump are displayed.");
//            fetchOnlyLocalSyncs.setSummaryOff("Existing syncs from your MISP instance are fetched (experimental)");
//            fetchOnlyLocalSyncs.setChecked(preferenceManager.getShowLocalSyncsOnly());
//            fetchOnlyLocalSyncs.setOnPreferenceChangeListener((preference, newValue) -> {
//                preferenceManager.setShowLocalSyncsOnly((boolean) newValue);
//                return true;
//            });
//
//            generalCategory.addPreference(fetchOnlyLocalSyncs);

            // App Information

            PreferenceCategory appInfoCategory = new PreferenceCategory(context);
            appInfoCategory.setTitle("App Information");
            getPreferenceScreen().addPreference(appInfoCategory);

            Preference githubPreference = new Preference(context);
            githubPreference.setIcon(context.getDrawable(R.drawable.ic_github));
            githubPreference.setTitle("Github");
            githubPreference.setSummary("Visit the Github project");
            Intent viewOnGithub = new Intent(Intent.ACTION_VIEW);
            viewOnGithub.setData(Uri.parse("https://github.com/MISP/misp-bump"));
            githubPreference.setIntent(viewOnGithub);

            Preference versionPreference = new Preference(context);
            versionPreference.setIcon(context.getDrawable(R.drawable.ic_info_outline_dark));
            versionPreference.setTitle("Version");
            versionPreference.setSummary("1.0");

            appInfoCategory.addPreference(githubPreference);
            appInfoCategory.addPreference(versionPreference);
        }
    }
}
