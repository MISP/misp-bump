package de.overview.wg.its.mispbump.preferences;

import android.os.Bundle;
import android.support.v7.preference.PreferenceFragmentCompat;
import de.overview.wg.its.mispbump.R;

public class AppPreferenceFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.app_preferences);
    }

}
