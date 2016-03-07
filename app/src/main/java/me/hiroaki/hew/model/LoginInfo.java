package me.hiroaki.hew.model;

import android.content.Context;

import me.hiroaki.hew.util.LoginSetting;

/**
 * Created by hiroaki on 2016/03/04.
 */
public class LoginInfo extends LoginSetting {
	private static final String NAME = "name";
	public LoginInfo(Context context) {
		super(context);
	}

	public void setLogin(String id, String password, String name) {
		super.setLogin(id, password);
		this.set(NAME, name);
	}

	public String getName() {
		return this.get(NAME);
	}

	@Override
	public void removeLogin() {
		super.removeLogin();
		this.remove(NAME);
	}
}
