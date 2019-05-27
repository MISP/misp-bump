package lu.circl.mispbump.auxiliary;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import lu.circl.mispbump.restful_client.Organisation;
import lu.circl.mispbump.restful_client.User;
import lu.circl.mispbump.security.KeyStoreWrapper;

public class PreferenceManager {


    private static final String PREFERENCES_FILE = "user_settings";

    private static final String SAVE_CREDENTIALS = "save_credentials";
    private static final String SERVER_URL = "server_url";
    private static final String AUTOMATION_KEY = "user_automation";

    private static final String USER_INFOS = "user_infos";
    private static final String USER_ORG_INFOS = "user_org_infos";

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
     * @param user
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
     * @param automationKey
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
     * @return the decr
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