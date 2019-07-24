package lu.circl.mispbump.security;


import android.util.Base64;

import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyAgreement;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;


/**
 * This class provides the functionality generate a shared secret key.
 * Furthermore it contains the encryption/decryption methods.
 */
public class DiffieHellman {

    private static final String CIPHER_ALGORITHM = "AES/CBC/PKCS5Padding";
    private static final String KEY_PAIR_ALGORITHM = "EC";
    private static final int KEY_SIZE = 521; // 224 | 256 | 384 | 521
    private static final String KEY_AGREEMENT_ALGORITHM = "ECDH";
    private static final String KEY_FACTORY_ALGORITHM = "EC";

    private static DiffieHellman instance;

    private PublicKey publickey;
    private KeyAgreement keyAgreement;

    private byte[] sharedSecret;
    private IvParameterSpec ivParameterSpec;


    private DiffieHellman() {
        initialize();
    }

    /**
     * Singleton pattern
     *
     * @return {@link DiffieHellman}
     */
    public static DiffieHellman getInstance() {
        if (instance == null) {
            instance = new DiffieHellman();
        }

        return instance;
    }

    /**
     * Generates a public and a private key using an elliptic curve algorithm.
     * The private key is fed into the key agreement instance.
     */
    private void initialize() {
        try {
            KeyPairGenerator kpg = KeyPairGenerator.getInstance(KEY_PAIR_ALGORITHM);
            kpg.initialize(KEY_SIZE);

            KeyPair kp = kpg.generateKeyPair();
            publickey = kp.getPublic();

            keyAgreement = KeyAgreement.getInstance(KEY_AGREEMENT_ALGORITHM);
            keyAgreement.init(kp.getPrivate());

        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            e.printStackTrace();
        }
    }

    /**
     * Encrypts data.
     *
     * @param data data to encrypt
     * @return To String converted and encrypted data
     */
    public String encrypt(String data) {
        try {
            Cipher c = Cipher.getInstance(CIPHER_ALGORITHM);
            c.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(sharedSecret, CIPHER_ALGORITHM), ivParameterSpec);

            byte[] cipherText = c.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return Base64.encodeToString(cipherText, Base64.NO_WRAP);

        } catch (BadPaddingException | InvalidKeyException | NoSuchPaddingException | IllegalBlockSizeException | NoSuchAlgorithmException | InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }

        return data;
    }

    /**
     * Decrypts data with the current shared secret.
     *
     * @param data data to decrypt
     * @return To String converted and decrypted data
     */
    public String decrypt(String data) {
        try {
            Cipher c = Cipher.getInstance(CIPHER_ALGORITHM);
            c.init(Cipher.DECRYPT_MODE, new SecretKeySpec(sharedSecret, CIPHER_ALGORITHM), ivParameterSpec);

            byte[] cipherText = Base64.decode(data, Base64.NO_WRAP);
            return new String(c.doFinal(cipherText), StandardCharsets.UTF_8);

        } catch (BadPaddingException | InvalidKeyException | NoSuchPaddingException | IllegalBlockSizeException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }
        return data;
    }

    /**
     * Generates a shared secret and derives an initialisation vector from it.
     *
     * @param pk public key of the sync partner
     */
    public void setForeignPublicKey(PublicKey pk) {
        try {
            keyAgreement.doPhase(pk, true);

            byte[] tmpSharedSecret = keyAgreement.generateSecret();
            sharedSecret = Arrays.copyOfRange(tmpSharedSecret, 0, 32);

            byte[] inputVector = Arrays.copyOfRange(tmpSharedSecret, 32, 48);
            ivParameterSpec = new IvParameterSpec(inputVector);

        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
    }

    /**
     * @return this devices public key
     */
    public PublicKey getPublicKey() {
        return publickey;
    }

    public static String publicKeyToString(PublicKey key) {
        return Base64.encodeToString(key.getEncoded(), Base64.DEFAULT);
    }

    public static PublicKey publicKeyFromString(String key) throws NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] input = Base64.decode(key, Base64.DEFAULT);
        return KeyFactory.getInstance(KEY_FACTORY_ALGORITHM).generatePublic(new X509EncodedKeySpec(input));
    }
}
