package lu.circl.mispbump.security;

import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.util.Base64;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.Arrays;
import java.util.Enumeration;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;

public class KeyStoreWrapper {

    public static final String USER_INFO_ALIAS = "ALIAS_USER_INFO";
    public static final String USER_ORGANISATION_INFO_ALIAS = "ALIAS_USER_ORGANISATION_INFO";
    public static final String AUTOMATION_ALIAS = "ALIAS_AUTOMATION_KEY";
    public static final String SERVER_URL_ALIAS = "ALIAS_SERVER_URL";
    public static final String UPLOAD_INFORMATION_ALIAS = "ALIAS_UPLOAD_INFORMATION";

    private static final String KEYSTORE_PROVIDER = "AndroidKeyStore";
    private static final String CIPHER_ALGORITHM = "AES/GCM/NoPadding";

    private String KEYSTORE_ALIAS;

    /**
     * Wraps the android key store to easily encrypt and decrypt sensitive data.
     *
     * @param alias identifies a key store entry (see public static ALIAS variables).
     */
    public KeyStoreWrapper(String alias) {
        KEYSTORE_ALIAS = alias;
    }

    /**
     * @return wheter an entry for this alias already exists.
     */
    private boolean isInitialized() {
        try {
            KeyStore ks = KeyStore.getInstance(KEYSTORE_PROVIDER);
            ks.load(null);

            if (ks.containsAlias(KEYSTORE_ALIAS)) {
                return true;
            }
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    /**
     * @return SecretKey associated with the given alias.
     */
    private SecretKey getStoredKey() {
        try {

            KeyStore ks = KeyStore.getInstance(KEYSTORE_PROVIDER);
            ks.load(null);
            return (SecretKey) ks.getKey(KEYSTORE_ALIAS, null);

        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (UnrecoverableKeyException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Generates a new key.
     *
     * @return the newly generated key.
     */
    private SecretKey generateKey() {
        try {

            // androids key generator
            final KeyGenerator keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, KEYSTORE_PROVIDER);

            // specs for the generated key
            final KeyGenParameterSpec keyGenParameterSpec = new KeyGenParameterSpec.Builder(KEYSTORE_ALIAS,
                    KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                    .setKeySize(256)
                    .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                    .build();

            // initialize KeyGenerator and generate a secret key
            keyGenerator.init(keyGenParameterSpec);
            return keyGenerator.generateKey();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Deletes the key associated with the current alias.
     */
    public void deleteStoredKey() {
        try {
            KeyStore ks = KeyStore.getInstance(KEYSTORE_PROVIDER);
            ks.load(null);
            ks.deleteEntry(KEYSTORE_ALIAS);
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Encrypt data with given algorithm and key associated with alias.
     *
     * @param data data to encrypt.
     * @return encrypted data as String.
     * @throws NoSuchPaddingException    padding not found
     * @throws NoSuchAlgorithmException  algorithm not found
     * @throws InvalidKeyException       invalid key
     * @throws BadPaddingException       bad padding
     * @throws IllegalBlockSizeException illegal block size
     */
    public String encrypt(String data) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        SecretKey secretKey;

        if (isInitialized()) {
            secretKey = getStoredKey();
        } else {
            secretKey = generateKey();
        }

        final Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);

        byte[] byteData = data.getBytes(StandardCharsets.UTF_8);
        byte[] combined = getCombinedArray(cipher.getIV(), cipher.doFinal(byteData));
        return Base64.encodeToString(combined, Base64.NO_WRAP);
    }

    /**
     * Decrypts data with given algorithm and key associated with alias.
     *
     * @param input encrypted data.
     * @return decrypted data as String.
     * @throws NoSuchPaddingException             padding not found
     * @throws NoSuchAlgorithmException           algorithm not found
     * @throws InvalidAlgorithmParameterException invalid algorithm parameters
     * @throws InvalidKeyException                invalid key
     * @throws BadPaddingException                bad padding
     * @throws IllegalBlockSizeException          illegal block size
     */
    public String decrypt(String input) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        byte[] in = Base64.decode(input, Base64.NO_WRAP);
        IvAndData ivAndData = splitCombinedArray(in, 12);

        final Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
        final GCMParameterSpec gcmSpec = new GCMParameterSpec(128, ivAndData.iv);

        cipher.init(Cipher.DECRYPT_MODE, getStoredKey(), gcmSpec);

        return new String(cipher.doFinal(ivAndData.data), StandardCharsets.UTF_8);
    }

    /**
     * Removes all aliases and the associated keys.
     * Note: all encrypted data cannot be decrypted anymore!
     */
    public static void deleteAllStoredKeys() {
        try {

            KeyStore ks = KeyStore.getInstance(KEYSTORE_PROVIDER);
            ks.load(null);
            Enumeration<String> aliases = ks.aliases();

            while (aliases.hasMoreElements()) {
                String alias = aliases.nextElement();
                ks.deleteEntry(alias);
            }

        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Combine IV and encrypted data.
     *
     * @param iv            initialisation vector
     * @param encryptedData encrypted data
     * @return combination of iv and encrypted data
     */
    private byte[] getCombinedArray(byte[] iv, byte[] encryptedData) {
        byte[] combined = new byte[iv.length + encryptedData.length];
        for (int i = 0; i < combined.length; ++i) {
            combined[i] = i < iv.length ? iv[i] : encryptedData[i - iv.length];
        }
        return combined;
    }

    private IvAndData splitCombinedArray(byte[] input, int ivLength) {
        byte[] iv = Arrays.copyOfRange(input, 0, ivLength);
        byte[] data = Arrays.copyOfRange(input, ivLength, input.length);
        return new IvAndData(iv, data);
    }

    public class IvAndData {
        IvAndData(byte[] iv, byte[] data) {
            this.iv = iv;
            this.data = data;
        }

        byte[] iv;
        byte[] data;
    }
}