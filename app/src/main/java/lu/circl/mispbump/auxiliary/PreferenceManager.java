package lu.circl.mispbump.auxiliary;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import lu.circl.mispbump.models.UploadInformation;
import lu.circl.mispbump.models.restModels.Organisation;
import lu.circl.mispbump.models.restModels.User;
import lu.circl.mispbump.security.KeyStoreWrapper;

public class PreferenceManager {

    private static final String TAG = "PreferenceManager";

    private static final String PREFERENCES_FILE = "user_settings";

    private static final String SAVE_CREDENTIALS = "save_credentials";
    private static final String SERVER_URL = "server_url";
    private static final String AUTOMATION_KEY = "user_automation";

    private static final String USER_INFOS = "user_infos";
    private static final String USER_ORG_INFOS = "user_org_infos";

    private static final String UPLOAD_INFO = "upload_info";

    private SharedPreferences preferences;
    private static PreferenceManager instance;

    private PreferenceManager(Context context) {
        preferences = context.getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE);
    }

    /**
     * Helper class to save and retrieve (sensitive) information to and from SharedPreferences.
     *
     * @param context for accessing the SharedPreferences file.
     * @return singleton instance
     */
    public static PreferenceManager getInstance(Context context) {
        if (instance == null) {
            instance = new PreferenceManager(context);
        }

        return instance;
    }


    /**
     * Saves user infos from "users/view/me" (encrypted)
     *
     * @param user {@link User}
     */
    public void setUserInfo(User user) {
        try {
            String userStr = new Gson().toJson(user);
            KeyStoreWrapper keyStoreWrapper = new KeyStoreWrapper(KeyStoreWrapper.USER_INFO_ALIAS);
            String encryptedUserInfo = keyStoreWrapper.encrypt(userStr);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString(USER_INFOS, encryptedUserInfo);
            editor.apply();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns the user information if already stored and decrypts it.
     *
     * @return decrypted user info if any, else null
     */
    public User getUserInfo() {

        if (!preferences.contains(USER_INFOS)) {
            return null;
        }

        try {
            KeyStoreWrapper keyStoreWrapper = new KeyStoreWrapper(KeyStoreWrapper.USER_INFO_ALIAS);
            String decrypted = keyStoreWrapper.decrypt(preferences.getString(USER_INFOS, ""));
            return new Gson().fromJson(decrypted, User.class);
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        }

        return null;
    }


    /**
     * Save user org infos from "organisations/view/{orgId}" (encrypted)
     *
     * @param organisation Object representation of json organisation information
     */
    public void setUserOrgInfo(Organisation organisation) {
        try {
            String orgStr = new Gson().toJson(organisation);
            KeyStoreWrapper keyStoreWrapper = new KeyStoreWrapper(KeyStoreWrapper.USER_ORGANISATION_INFO_ALIAS);
            String encrypted = keyStoreWrapper.encrypt(orgStr);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString(USER_ORG_INFOS, encrypted);
            editor.apply();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        }

    }

    /**
     * Returns the user organisation information if already stored and decrypts it.
     *
     * @return decrypted user org info if any, else null
     */
    public Organisation getUserOrganisation() {

        if (!preferences.contains(USER_ORG_INFOS)) {
            return null;
        }

        try {
            KeyStoreWrapper keyStoreWrapper = new KeyStoreWrapper(KeyStoreWrapper.USER_ORGANISATION_INFO_ALIAS);
            String decrypted = keyStoreWrapper.decrypt(preferences.getString(USER_ORG_INFOS, ""));
            return new Gson().fromJson(decrypted, Organisation.class);
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        }

        return null;
    }


    /**
     * Encrypts the automation key and stores it in preferences.
     *
     * @param automationKey key entered in {@link lu.circl.mispbump.activities.LoginActivity}
     */
    public void setAutomationKey(String automationKey) {
        try {
            KeyStoreWrapper keyStoreWrapper = new KeyStoreWrapper(KeyStoreWrapper.AUTOMATION_ALIAS);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString(AUTOMATION_KEY, keyStoreWrapper.encrypt(automationKey));
            editor.apply();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        }
    }

    /**
     * Decrypts the stored automation key and returns it.
     *
     * @return decrypted automation key associated with the current user. If no user exists an empty
     * String is returned.
     */
    public String getAutomationKey() {

        if (!preferences.contains(AUTOMATION_KEY)) {
            return "";
        }

        try {
            KeyStoreWrapper keyStoreWrapper = new KeyStoreWrapper(KeyStoreWrapper.AUTOMATION_ALIAS);
            return keyStoreWrapper.decrypt(preferences.getString(AUTOMATION_KEY, ""));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        }

        return "";
    }

    /**
     * Delete the key to decrypt this entry and the entry itself.
     */
    public void clearAutomationKey() {
        // remove the key from KeyStore
        KeyStoreWrapper keyStoreWrapper = new KeyStoreWrapper(KeyStoreWrapper.AUTOMATION_ALIAS);
        keyStoreWrapper.deleteStoredKey();

        SharedPreferences.Editor editor = preferences.edit();
        editor.remove(AUTOMATION_KEY);
        editor.apply();
    }


    /**
     * Encrypts the server url and stores it in preferences.
     *
     * @param serverUrl url of the corresponding misp instance
     */
    public void setServerUrl(String serverUrl) {
        try {

            KeyStoreWrapper keyStoreWrapper = new KeyStoreWrapper(KeyStoreWrapper.SERVER_URL_ALIAS);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString(SERVER_URL, keyStoreWrapper.encrypt(serverUrl));
            editor.apply();

        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        }
    }

    /**
     * Decrypts the stored server url and returns it
     *
     * @return decrypted misp instance url
     */
    public String getServerUrl() {

        if (!preferences.contains(SERVER_URL)) {
            return "";
        }

        try {
            KeyStoreWrapper keyStoreWrapper = new KeyStoreWrapper(KeyStoreWrapper.SERVER_URL_ALIAS);
            return keyStoreWrapper.decrypt(preferences.getString(SERVER_URL, ""));

        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        }

        return "";
    }

    /**
     * Delete the key to decrypt this entry and the entry itself.
     */
    public void clearServerUrl() {
        // remove the key from KeyStore
        KeyStoreWrapper keyStoreWrapper = new KeyStoreWrapper(KeyStoreWrapper.SERVER_URL_ALIAS);
        keyStoreWrapper.deleteStoredKey();

        SharedPreferences.Editor editor = preferences.edit();
        editor.remove(SERVER_URL);
        editor.apply();
    }


    public void setUploadInformationList(List<UploadInformation> uploadInformationList) {
        KeyStoreWrapper ksw = new KeyStoreWrapper(KeyStoreWrapper.UPLOAD_INFORMATION_ALIAS);

        try {
            String cipherText = ksw.encrypt(new Gson().toJson(uploadInformationList));
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString(UPLOAD_INFO, cipherText);
            editor.apply();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        }
    }

    public List<UploadInformation> getUploadInformation() {
        KeyStoreWrapper ksw = new KeyStoreWrapper(KeyStoreWrapper.UPLOAD_INFORMATION_ALIAS);
        String storedUploadInfoString = preferences.getString(UPLOAD_INFO, null);

        Type type = new TypeToken<List<UploadInformation>>() {}.getType();

        if (storedUploadInfoString == null) {
            return null;
        }

        try {
            storedUploadInfoString = ksw.decrypt(storedUploadInfoString);
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        }

        return new Gson().fromJson(storedUploadInfoString, type);
    }

    public void addUploadInformation(UploadInformation uploadInformation) {
        List<UploadInformation> uploadInformationList = getUploadInformation();

        if (uploadInformationList == null) {
            uploadInformationList = new ArrayList<>();
            uploadInformationList.add(uploadInformation);
            setUploadInformationList(uploadInformationList);
        } else {

            // check if upload information already exists
            for (int i = 0; i < uploadInformationList.size(); i++) {
                if (uploadInformationList.get(i).getId().compareTo(uploadInformation.getId()) == 0) {
                    uploadInformationList.set(i, uploadInformation);
                    setUploadInformationList(uploadInformationList);
                    return;
                }
            }

            uploadInformationList.add(uploadInformation);
            setUploadInformationList(uploadInformationList);
        }
    }

    public boolean containsUploadInformation(UUID uuid) {
        List<UploadInformation> uploadInformationList = getUploadInformation();

        if (uploadInformationList == null) {
            return false;
        }

        for (UploadInformation ui : uploadInformationList) {
            if (ui.getId().compareTo(uuid) == 0) {
                return true;
            }
        }

        return false;
    }

    public boolean removeUploadInformation(UUID uuid) {
        Log.d("PREFS", "uuid to delete: " + uuid.toString());

        List<UploadInformation> uploadInformationList = getUploadInformation();

        for (UploadInformation ui : uploadInformationList) {

            Log.d("PREFS", "checking uuid: " + ui.getId().toString());

            if (ui.getId().compareTo(uuid) == 0) {
                if (uploadInformationList.size() == 1) {
                    clearUploadInformation();
                } else {
                    uploadInformationList.remove(ui);
                    setUploadInformationList(uploadInformationList);
                }
                return true;
            }
        }


        return false;
    }

    public void clearUploadInformation() {
        KeyStoreWrapper keyStoreWrapper = new KeyStoreWrapper(KeyStoreWrapper.UPLOAD_INFORMATION_ALIAS);
        keyStoreWrapper.deleteStoredKey();

        SharedPreferences.Editor editor = preferences.edit();
        editor.remove(UPLOAD_INFO);
        editor.apply();
    }


    /**
     * Set if credentials (authkey & server url) should be saved locally.
     *
     * @param save enable or disable
     * @deprecated currently not used because automation key is needed to do requests to your misp instance.
     * If this should be an option in future: misp automation key would be needed on each sync process.
     */
    public void setSaveCredentials(boolean save) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(SAVE_CREDENTIALS, save);
        editor.apply();
    }

    public boolean getSaveCredentials() {
        return preferences.getBoolean(SAVE_CREDENTIALS, false);
    }


    public void clearAllData() {
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.apply();
    }
}