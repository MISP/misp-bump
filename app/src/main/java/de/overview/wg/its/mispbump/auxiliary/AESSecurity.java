package de.overview.wg.its.mispbump.auxiliary;

import android.util.Base64;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

public class AESSecurity {

	private static final String TAG = "MISP_LOGGING";
	private static final String ALGORITHM = "AES";

	private static AESSecurity instance;

	private PublicKey publickey;
	private KeyAgreement keyAgreement;
	private byte[] sharedSecret;

	private AESSecurity() {
		initialize();
	}

	private void initialize() {
		KeyPairGenerator kpg = null;

		try {
			kpg = KeyPairGenerator.getInstance("EC");
			kpg.initialize(256);

			KeyPair kp = kpg.generateKeyPair();

			publickey = kp.getPublic();

			keyAgreement = KeyAgreement.getInstance("ECDH");
			keyAgreement.init(kp.getPrivate());

		} catch (NoSuchAlgorithmException | InvalidKeyException e) {
			e.printStackTrace();
		}
	}

	public void setForeignPublicKey(PublicKey publickey) {
		try {
			keyAgreement.doPhase(publickey, true);
			sharedSecret = keyAgreement.generateSecret();
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		}
	}

	public String encrypt(String data) {
		try {
			Key key = generateKey();
			Cipher c = Cipher.getInstance(ALGORITHM);
			c.init(Cipher.ENCRYPT_MODE, key);

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
			Cipher c = Cipher.getInstance(ALGORITHM);
			c.init(Cipher.DECRYPT_MODE, key);

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
		return new SecretKeySpec(sharedSecret, ALGORITHM);
	}

	public static String publicKeyToString(PublicKey key) {
		return Base64.encodeToString(key.getEncoded(), Base64.DEFAULT);
	}
	public static PublicKey publicKeyFromString(String key) {

		KeyFactory kf = null;

		byte[] input = Base64.decode(key, Base64.DEFAULT);

		try {
			kf = KeyFactory.getInstance("EC"); // normal: DH
			return kf.generatePublic(new X509EncodedKeySpec(input));
		} catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
			e.printStackTrace();
		}

		return null;
	}

	public static AESSecurity getInstance() {
		if(instance == null) {
			instance = new AESSecurity();
		}

		return instance;
	}
}