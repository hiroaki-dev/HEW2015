package me.hiroaki.hew.model;

import android.content.Context;

import com.google.gson.annotations.SerializedName;

/**
 * Created by hiroaki on 2016/03/04.
 */
public class LoginInfomation{

	@SerializedName("id")
	private String id;

	@SerializedName("name")
	private String name;

	@SerializedName("status")
	private boolean status;

	public boolean isStatus() {
		return status;
	}

	public void setStatus(boolean status) {
		this.status = status;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}



}

