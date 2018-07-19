package de.overview.wg.its.mispauth.auxiliary;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import de.overview.wg.its.mispauth.model.Organisation;
import de.overview.wg.its.mispauth.model.SyncedPartner;
import de.overview.wg.its.mispauth.model.User;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.List;

public class PreferenceManager {

    private static PreferenceManager instance;

	private SharedPreferences userPreferences;
    private SharedPreferences credentialPreferences;
    private SharedPreferences syncedInstancesPreferences;

    private static final String CREDENTIAL_PREFERENCE = "de.overview.wg.its.mispauth.credential_preference";
	private static final String SAVED_INSTANCES_PREFERENCE = "de.overview.wg.its.mispauth.saved_instances_preference";
	private static final String USER_PREFERENCE = "de.overview.wg.its.mispauth.user_preferences";

    private static String PREF_KEY_SERVER_URL = "key_server_url";
    private static String PREF_KEY_SERVER_API_KEY = "key_server_api_key";
    private static String PREF_KEY_MY_ORGANISATION = "key_my_organisation";
    private static String PREF_KEY_MY_USER = "key_my_user";
	private static String PREF_KEY_SAVE_AUTHKEY_ENABLED = "key_save_authkey_enabled";
	private static String PREF_KEY_SYNCED_ORGANISATIONS = "key_synced_organisations";

    private PreferenceManager(Context context) {
    	credentialPreferences = context.getSharedPreferences(CREDENTIAL_PREFERENCE, Context.MODE_PRIVATE);
    	syncedInstancesPreferences = context.getSharedPreferences(SAVED_INSTANCES_PREFERENCE, Context.MODE_PRIVATE);
    	userPreferences = context.getSharedPreferences(USER_PREFERENCE, Context.MODE_PRIVATE);
    }

    public List<SyncedPartner> getSyncedPartnerList() {
		String list = syncedInstancesPreferences.getString(PREF_KEY_SYNCED_ORGANISATIONS, "");
		Type type = new TypeToken<List<SyncedPartner>>() {}.getType();
	    return new Gson().fromJson(list, type);
    }
    public void setSyncedPartnerList(List<SyncedPartner> syncedPartnerList) {
		String json = new Gson().toJson(syncedPartnerList);
		SharedPreferences.Editor editor = syncedInstancesPreferences.edit();
		editor.putString(PREF_KEY_SYNCED_ORGANISATIONS, json);
		editor.apply();
    }

    public boolean saveAuthkeyEnabledExists() {
	    return userPreferences.contains(PREF_KEY_SAVE_AUTHKEY_ENABLED);
    }
    public boolean saveAuthkeyEnabled() {
		return userPreferences.getBoolean(PREF_KEY_SAVE_AUTHKEY_ENABLED, false);
    }
	public void setSaveAuthkeyEnabled(boolean save) {
    	SharedPreferences.Editor editor = userPreferences.edit();
    	editor.putBoolean(PREF_KEY_SAVE_AUTHKEY_ENABLED, save);
    	editor.apply();
	}

	/**
	 * @return own Organisation if available, else null
	 */
	public Organisation getMyOrganisation() {
    	try {
		    JSONObject jsonObject = new JSONObject(credentialPreferences.getString(PREF_KEY_MY_ORGANISATION, ""));
		    Organisation org = new Organisation();
		    org.fromJSON(jsonObject);
		    return org;
	    } catch (JSONException e) {
		    e.printStackTrace();
	    }

	    return null;
    }
	public void setMyOrganisation(Organisation org) {
		SharedPreferences.Editor editor = credentialPreferences.edit();
		editor.putString(PREF_KEY_MY_ORGANISATION, org.toJSON().toString());
		editor.apply();
	}

	public User getMyUser() {
		try {
			JSONObject jsonObject = new JSONObject(credentialPreferences.getString(PREF_KEY_MY_USER, ""));
			User user = new User();
			user.fromJSON(jsonObject);
			return user;
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return null;
	}
	public void setMyUser(User user) {
		SharedPreferences.Editor editor = credentialPreferences.edit();
		editor.putString(PREF_KEY_MY_USER, user.toJSON().toString());
		editor.apply();
	}

    public String getMyServerUrl() {
        return credentialPreferences.getString(PREF_KEY_SERVER_URL, "");
    }
    public void setMyServerUrl(String serverUrl) {
        SharedPreferences.Editor editor = credentialPreferences.edit();
        editor.putString(PREF_KEY_SERVER_URL, serverUrl);
        editor.apply();
    }

    public String getMyServerApiKey() {
        return credentialPreferences.getString(PREF_KEY_SERVER_API_KEY, "");
    }
    public void setMyServerApiKey(String apiKey) {
        SharedPreferences.Editor editor = credentialPreferences.edit();
        editor.putString(PREF_KEY_SERVER_API_KEY, apiKey);
        editor.apply();
    }


    public void clearUserPreferences() {
		userPreferences.edit().clear().apply();
    }
    public void clearCredentialPreferences() {
		credentialPreferences.edit().clear().apply();
    }
    public void clearSyncedInformationPreferences() {
		syncedInstancesPreferences.edit().clear().apply();
    }


    public static PreferenceManager Instance(Context context) {
        if(instance == null) {
            instance = new PreferenceManager(context);
        }

        return instance;
    }
}

