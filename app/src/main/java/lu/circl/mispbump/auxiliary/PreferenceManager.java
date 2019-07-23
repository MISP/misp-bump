package lu.circl.mispbump.auxiliary;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.core.util.Pair;

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
import lu.circl.mispbump.models.restModels.Role;
import lu.circl.mispbump.models.restModels.User;
import lu.circl.mispbump.security.KeyStoreWrapper;

public class PreferenceManager {

    private static final String PREFERENCES_FILE = "user_settings";

    private static final String USER_CREDENTIALS = "user_credentials";
    private static final String USER_INFOS = "user_infos";
    private static final String USER_ORG_INFOS = "user_org_infos";

    private static final String UPLOAD_INFO = "upload_info";

    private static final String MISP_ROLES = "misp_roles";

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
     * Save downloaded MISP roles on device.
     *
     * @param roles {@link Role}
     */
    public void setRoles(Role[] roles) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(MISP_ROLES, new Gson().toJson(roles));
        editor.apply();
    }

    /**
     * Gets downloaded and saved MISP roles if available.
     * <p/>
     * Roles are downloaded on login and updated with each profile update.
     *
     * @return {@link Role}[] or null
     */
    public Role[] getRoles() {
        Type type = new TypeToken<Role[]>() {
        }.getType();
        String rolesString = preferences.getString(MISP_ROLES, "");

        assert rolesString != null;
        if (rolesString.isEmpty()) {
            return null;
        } else {
            return new Gson().fromJson(rolesString, type);
        }
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


    public void setUserCredentials(String url, String authkey) {
        try {
            KeyStoreWrapper keyStoreWrapper = new KeyStoreWrapper(KeyStoreWrapper.USER_CREDENTIALS_ALIAS);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString(USER_CREDENTIALS, keyStoreWrapper.encrypt(new Gson().toJson(new Pair<>(url, authkey))));
            editor.apply();
        } catch (NoSuchAlgorithmException | InvalidKeyException | BadPaddingException | IllegalBlockSizeException | NoSuchPaddingException e) {
            e.printStackTrace();
        }
    }

    public Pair<String, String> getUserCredentials() {
        if (!preferences.contains(USER_CREDENTIALS)) {
            return null;
        }

        try {
            KeyStoreWrapper keyStoreWrapper = new KeyStoreWrapper(KeyStoreWrapper.USER_CREDENTIALS_ALIAS);
            Type type = new TypeToken<Pair<String, String>>() {}.getType();
            String serializedCreds = keyStoreWrapper.decrypt(preferences.getString(USER_CREDENTIALS, ""));
            return new Gson().fromJson(serializedCreds, type);
        } catch (InvalidKeyException | InvalidAlgorithmParameterException | NoSuchPaddingException | BadPaddingException | IllegalBlockSizeException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return null;
    }


    private List<UploadInformation> cachedUploadInformationList;

    private void loadUploadInformationList() {
        KeyStoreWrapper ksw = new KeyStoreWrapper(KeyStoreWrapper.UPLOAD_INFORMATION_ALIAS);
        String storedUploadInfoString = preferences.getString(UPLOAD_INFO, null);

        Type type = new TypeToken<List<UploadInformation>>() {
        }.getType();

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


    public void clearAllData() {
        SharedPreferences.Editor editor = preferences.edit();

//        clearServerUrl();
//        clearAutomationKey();
        clearUploadInformation();

        editor.clear();
        editor.apply();
    }
}