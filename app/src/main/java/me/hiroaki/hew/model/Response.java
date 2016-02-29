package me.hiroaki.hew.model;

import android.util.Log;

import com.google.gson.annotations.SerializedName;

/**
 * Created by hiroaki on 2016/02/14.
 */
public class Response {
	private static final String TAG = Response.class.getSimpleName();
	@SerializedName("status")
	private boolean status;

	public boolean isStatus() {
		return status;
	}

	public void setStatus(boolean status) {
		this.status = status;
	}
}
