package me.hiroaki.hew.util;

import android.util.Base64;

import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;

/**
 * Created by cymedix on 15/12/09.
 * 暗号化を行う
 */
public class Encryptor {

	private static final int KEY_LENGTH_BYTES = 16;
	private static final int KEY_LENGTH_BITS  = KEY_LENGTH_BYTES * 8;

	/**
	 * ランダムなキーを生成する
	 */
	public static Key generateKey() {
		try {
			KeyGenerator generator = KeyGenerator.getInstance("AES");
			SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
			generator.init(KEY_LENGTH_BITS, random);
			return generator.generateKey();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 文字列を暗号化する
	 * @param plainText
	 * @param skey
	 * @return
	 * @throws GeneralSecurityException
	 */
	public static String encrypt(String plainText, Key skey) {
		if (plainText == null) {
			return null;
		}

		try {
			Cipher cipher = Cipher.getInstance("AES");
			cipher.init(Cipher.ENCRYPT_MODE, skey);
			byte[] result = cipher.doFinal(plainText.getBytes());
			return Base64.encodeToString(result, Base64.URL_SAFE | Base64.NO_WRAP);
		} catch (GeneralSecurityException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 文字列を複合化する
	 * @param plainText
	 * @param skey
	 * @return
	 */
	public static String decrypt(String plainText, Key skey) {
		if (plainText == null) {
			return null;
		}

		try {
			Cipher cipher = Cipher.getInstance("AES");
			cipher.init(Cipher.DECRYPT_MODE, skey);
			byte[] result = cipher.doFinal(Base64.decode(plainText, Base64.URL_SAFE | Base64.NO_WRAP));
			return new String(result);
		} catch (GeneralSecurityException e) {
			throw new RuntimeException(e);
		}
	}
}