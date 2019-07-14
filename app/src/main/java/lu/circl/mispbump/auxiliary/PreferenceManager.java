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
    public String getAuthKey() {

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
            return null;
        }

        try {
            KeyStoreWrapper keyStoreWrapper = new KeyStoreWrapper(KeyStoreWrapper.SERVER_URL_ALIAS);
            return keyStoreWrapper.decrypt(preferences.getString(SERVER_URL, null));

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


    private List<UploadInformation> cachedUploadInformationList;

    private void loadUploadInformationList() {
        KeyStoreWrapper ksw = new KeyStoreWrapper(KeyStoreWrapper.UPLOAD_INFORMATION_ALIAS);
        String storedUploadInfoString = preferences.getString(UPLOAD_INFO, null);

        Type type = new TypeToken<List<UploadInformation>>() {}.getType();

        if (storedUploadInfoString == null || storedUploadInfoString.isEmpty()) {
            cachedUploadInformationList = new ArrayList<>();
        } else {
            try {
                storedUploadInfoString = ksw.decrypt(storedUploadInfoString);
                cachedUploadInformationList = new Gson().fromJson(storedUploadInfoString, type);
            } catch (IllegalBlockSizeException | BadPaddingException | InvalidKeyException | InvalidAlgorithmParameterException | NoSuchPaddingException | NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
        }
    }

    private void saveUploadInformationList() {
        try {
            KeyStoreWrapper ksw = new KeyStoreWrapper(KeyStoreWrapper.UPLOAD_INFORMATION_ALIAS);
            String cipherText = ksw.encrypt(new Gson().toJson(cachedUploadInformationList));
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString(UPLOAD_INFO, cipherText);
            editor.apply();
        } catch (IllegalBlockSizeException | BadPaddingException | InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException e) {
            e.printStackTrace();
        }
    }

    public List<UploadInformation> getUploadInformationList() {
        if (cachedUploadInformationList == null) {
            loadUploadInformationList();
        }

        return cachedUploadInformationList;
    }

    public void setUploadInformationList(List<UploadInformation> uploadInformationList) {
        cachedUploadInformationList = uploadInformationList;
        saveUploadInformationList();
    }

    public UploadInformation getUploadInformation(UUID uuid) {
        if (cachedUploadInformationList == null) {
            loadUploadInformationList();
        }

        for (UploadInformation ui : cachedUploadInformationList) {
            if (ui.getUuid().compareTo(uuid) == 0) {
                return ui;
            }
        }

        return null;
    }

    public void addUploadInformation(UploadInformation uploadInformation) {
        if (cachedUploadInformationList == null) {
            loadUploadInformationList();
        }

        // update if exists
        for (int i = 0; i < cachedUploadInformationList.size(); i++) {
            if (cachedUploadInformationList.get(i).getUuid().compareTo(uploadInformation.getUuid()) == 0) {
                cachedUploadInformationList.set(i, uploadInformation);
                saveUploadInformationList();
                return;
            }
        }

        // else: add
        cachedUploadInformationList.add(uploadInformation);
        saveUploadInformationList();
    }

    public void removeUploadInformation(UUID uuid) {
        if (cachedUploadInformationList == null) {
            loadUploadInformationList();
        }

        for (UploadInformation ui : cachedUploadInformationList) {
            if (ui.getUuid().compareTo(uuid) == 0) {
                // if is last element, then clear everything including IV and key in KeyStore
                if (cachedUploadInformationList.size() == 1) {
                    clearUploadInformation();
                } else {
                    cachedUploadInformationList.remove(ui);
                    saveUploadInformationList();
                }
            }
        }
    }

    public void clearUploadInformation() {
        cachedUploadInformationList.clear();

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