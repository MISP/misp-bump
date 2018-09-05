package de.overview.wg.its.mispbump.auxiliary;

import android.util.Base64;
import android.util.Log;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;

public class AESSecurity {

	private static final String TAG = "MISP_LOGGING";

    private static final String ENCRYPT_ALGORITHM = "AES/CBC/PKCS5Padding";
    private static final String KEY_PAIR_ALGORITHM = "EC";
    private static final int KEY_SIZE = 521; // 224 | 256 | 384 | 521
    private static final String KEY_AGREEMENT_ALGORITHM = "ECDH";

    private static AESSecurity instance;

	private PublicKey publickey;
	private KeyAgreement keyAgreement;

	private byte[] sharedSecret;
    private IvParameterSpec ivParameterSpec;

	private AESSecurity() {
		initialize();
	}

    /***
     * Generates a public and a private key using an elliptic curve algorithm (256 bit)
     * The private key is fed into the key agreement instance
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

    /***
     * Generates a shared secret with a given public key
     * @param publickey
     */
	public void setForeignPublicKey(PublicKey publickey) {

	    try {

			keyAgreement.doPhase(publickey, true);

			byte[] tmpSharedSecret = keyAgreement.generateSecret();

            sharedSecret = Arrays.copyOfRange(tmpSharedSecret, 0, 32);

            byte[] inputVector = Arrays.copyOfRange(sharedSecret, 32, 48);

            ivParameterSpec = new IvParameterSpec(inputVector);

        } catch (InvalidKeyException e) {
			e.printStackTrace();
		}
	}

	public String encrypt(String data) {
		try {

			Key key = generateKey();
			Cipher c = Cipher.getInstance(ENCRYPT_ALGORITHM);

            try {
                c.init(Cipher.ENCRYPT_MODE, key, ivParameterSpec);
            } catch (InvalidAlgorithmParameterException e) {
                e.printStackTrace();
            }

            byte[] encVal = c.doFinal(data.getBytes());
			return Base64.encodeToString(encVal, 0);

		} catch (BadPaddingException | InvalidKeyException | NoSuchPaddingException | IllegalBlockSizeException | NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return data;
	}

	public String decrypt(String data) {
		try {
			Key key = generateKey();

			Cipher c = Cipher.getInstance(ENCRYPT_ALGORITHM);

            try {
                c.init(Cipher.DECRYPT_MODE, key, ivParameterSpec);
            } catch (InvalidAlgorithmParameterException e) {
                e.printStackTrace();
            }

            byte[] decoded = Base64.decode(data, 0);
			byte[] decValue = c.doFinal(decoded);
			return new String(decValue);
		} catch (BadPaddingException | InvalidKeyException | NoSuchPaddingException | IllegalBlockSizeException | NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return data;
	}

	public PublicKey getPublicKey() {
		return publickey;
	}

	private Key generateKey() {

		return new SecretKeySpec(sharedSecret, ENCRYPT_ALGORITHM);

	}

	public static String publicKeyToString(PublicKey key) {
		return Base64.encodeToString(key.getEncoded(), Base64.DEFAULT);
	}

	public static PublicKey publicKeyFromString(String key) {

        try {

            byte[] input = Base64.decode(key, Base64.DEFAULT);
            return KeyFactory.getInstance("EC").generatePublic(new X509EncodedKeySpec(input));

		} catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
			e.printStackTrace();
		}

		return null;
	}

	public static AESSecurity getInstance() {

	    //todo: make singleton again

//		if(instance == null) {
//			instance = new AESSecurity();
//		}
//
//		return instance;

        return new AESSecurity();
	}
}