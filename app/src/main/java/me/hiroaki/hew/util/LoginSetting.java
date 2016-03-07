package me.hiroaki.hew.util;

import android.content.Context;
import android.util.Log;

import me.hiroaki.hew.activity.LoginActivity;
import me.hiroaki.hew.model.SharedPreferencesWrapper;

/**
 * Created by hiroaki on 2016/03/03.
 */
public class LoginSetting extends SharedPreferencesWrapper {
	private static final String TAG = LoginActivity.class.getSimpleName();
	private static final String ID = "login_id";
	private static final String PASSWORD = "password";

	public LoginSetting(Context context) {
		super(context);
	}

	/**
	 * ログイン情報登録
	 */
	public void setLogin(String id, String password) {
		this.set(ID, id);
		this.set(PASSWORD, password);
	}

	public void removeLogin() {
		this.remove(ID);
		this.remove(PASSWORD);
	}

	public String getLoginId() {
		return this.get(ID);
	}

	public String getPassword() {
		return this.get(PASSWORD);
	}

	public boolean isLogin() {
		if (getLoginId().isEmpty()) {
			return false;
		}
		return true;
	}
}
