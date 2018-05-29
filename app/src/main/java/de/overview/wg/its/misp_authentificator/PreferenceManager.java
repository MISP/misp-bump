package de.overview.wg.its.misp_authentificator;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferenceManager {

    private static PreferenceManager instance;
    private Context context;
    private SharedPreferences sharedPreferences;

    private String PREF_KEY_SERVER_URL;
    private String PREF_KEY_SERVER_API_KEY;

    private PreferenceManager(Context context) {
        this.context = context;
        sharedPreferences = android.preference.PreferenceManager.getDefaultSharedPreferences(context);

        PREF_KEY_SERVER_URL = context.getResources().getString(R.string.key_server_url);
        PREF_KEY_SERVER_API_KEY = context.getResources().getString(R.string.key_server_api_key);
    }

    public String getMyServerUrl() {
        return sharedPreferences.getString(PREF_KEY_SERVER_URL, "");
    }
    public void setMyServerUrl(String serverUrl) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(PREF_KEY_SERVER_URL, serverUrl);
        editor.apply();
    }

    public String getMyServerApiKey() {
        return sharedPreferences.getString(PREF_KEY_SERVER_API_KEY, "");
    }
    public void setMyServerApiKey(String apiKey) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(PREF_KEY_SERVER_API_KEY, apiKey);
        editor.apply();
    }

    public void deleteAllLocalData() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }

    public static PreferenceManager Instance(Context context) {
        if(instance == null) {
            instance = new PreferenceManager(context);
        }

        return instance;
    }
}
