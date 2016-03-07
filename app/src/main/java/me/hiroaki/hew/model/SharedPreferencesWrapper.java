package me.hiroaki.hew.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;
import android.util.Log;

import java.security.Key;

import javax.crypto.spec.SecretKeySpec;

import me.hiroaki.hew.util.Encryptor;

/**
 * Created by cymedix on 15/12/09.
 */
public class SharedPreferencesWrapper {
	private static final String TAG = SharedPreferencesWrapper.class.getSimpleName();

	private static final String APP_BASE_CONFIG_KEY = "sec_base_key_name";
	// 設定ファイル名
	private static final String CONFIG_FILE_NAME = "shared_data";

	private Context mContext;
	private SharedPreferences mPreference;
	private Key mKey;

	public SharedPreferencesWrapper(Context context) {
		mContext = context;
		mPreference = mContext.getSharedPreferences(SharedPreferencesWrapper.CONFIG_FILE_NAME, Context.MODE_PRIVATE);

		// 復元用のキーを取得/初回起動時はキーを生成します。
		String keyStr = this.getPreference().getString(SharedPreferencesWrapper.APP_BASE_CONFIG_KEY, "");
		if (keyStr.length() == 0) {
			// 生成
			mKey = Encryptor.generateKey();
			String base64key = Base64.encodeToString(mKey.getEncoded(), Base64.URL_SAFE | Base64.NO_WRAP);
			SharedPreferences.Editor editor = this.getPreference().edit();
			editor.putString(SharedPreferencesWrapper.APP_BASE_CONFIG_KEY, base64key);
			editor.commit();
		} else {
			// 復元
			byte[] keyBytes = Base64.decode(keyStr, Base64.URL_SAFE | Base64.NO_WRAP);
			mKey = new SecretKeySpec(keyBytes, "AES");
		}
	}

	protected Context getContext() {
		return mContext;
	}

	protected SharedPreferences getPreference() {
		return mPreference;
	}

	/**
	 * データの保存
	 * @param  key
	 * @param  value
	 * @return void
	 */
	protected void set(String key, String value) {
		SharedPreferences.Editor editor = this.getPreference().edit();
		// key / valueともに暗号化する
		editor.putString(Encryptor.encrypt(key, mKey), Encryptor.encrypt(value, mKey));
		editor.commit();
	}

	/**
	 * データを取得
	 * @param  key
	 * @return String value
	 */
	protected String get(String key) {
		String value = this.getPreference().getString(Encryptor.encrypt(key, mKey), "");
		if (value.length() == 0) {
			Log.d(TAG, "value.length == 0");
			return "";
		} else {
			Log.d(TAG, "value.length != 0");
			return Encryptor.decrypt(value, mKey);
		}
	}

	/**
	 * キー名に該当するデータを削除
	 * @param  key
	 * @return void
	 */
	protected void remove(String key) {
		SharedPreferences.Editor editor = this.getPreference().edit();
		editor.remove(Encryptor.encrypt(key, mKey));
		editor.commit();
	}

}