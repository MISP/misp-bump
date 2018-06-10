package de.overview.wg.its.mispauth.auxiliary;

import android.content.Context;
import android.content.SharedPreferences;
import de.overview.wg.its.mispauth.model.Organisation;
import de.overview.wg.its.mispauth.model.User;
import org.json.JSONException;
import org.json.JSONObject;

public class PreferenceManager {

    private static PreferenceManager instance;
    private SharedPreferences sharedPreferences;

    private static String PREF_KEY_SERVER_URL = "key_server_url";
    private static String PREF_KEY_SERVER_API_KEY = "key_server_api_key";
    private static String PREF_KEY_MY_ORGANISATION = "key_my_organisation";
    private static String PREF_KEY_MY_USER = "key_my_user";

    private PreferenceManager(Context context) {
        sharedPreferences = android.preference.PreferenceManager.getDefaultSharedPreferences(context);
    }

	/**
	 * @return own Organisation if available, else null
	 */
	public Organisation getMyOrganisation() {
    	try {
		    JSONObject jsonObject = new JSONObject(sharedPreferences.getString(PREF_KEY_MY_ORGANISATION, ""));
		    Organisation org = new Organisation();
		    org.fromJSON(jsonObject);
		    return org;
	    } catch (JSONException e) {
		    e.printStackTrace();
	    }

	    return null;
    }
	public void setMyOrganisation(Organisation org) {
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putString(PREF_KEY_MY_ORGANISATION, org.toJSON().toString());
		editor.apply();
	}

	public User getMyUser() {
		try {
			JSONObject jsonObject = new JSONObject(sharedPreferences.getString(PREF_KEY_MY_USER, ""));
			User user = new User();
			user.fromJSON(jsonObject);
			return user;
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return null;
	}
	public void setMyUser(User user) {
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putString(PREF_KEY_MY_USER, user.toJSON().toString());
		editor.apply();
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

