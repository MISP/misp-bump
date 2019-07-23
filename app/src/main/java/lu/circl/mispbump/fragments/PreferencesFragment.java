package lu.circl.mispbump.fragments;

import android.os.Bundle;

import androidx.preference.PreferenceFragmentCompat;

import lu.circl.mispbump.R;

public class PreferencesFragment extends PreferenceFragmentCompat {
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.app_preferences, rootKey);
    }
}
