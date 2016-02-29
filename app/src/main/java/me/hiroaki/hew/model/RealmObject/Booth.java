package me.hiroaki.hew.model.RealmObject;

import android.content.Context;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.UUID;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by hiroaki on 2016/02/14.
 */
public class Booth extends RealmObject implements Serializable {

	@PrimaryKey
	@SerializedName("id")
	private String id;

	@SerializedName("name")
	private String name;
	@SerializedName("description")
	private String description;
	@SerializedName("good")
	private int good;
	@SerializedName("representative")
	private String representative;

	private boolean doneGoodFlag = false;
	private boolean doneFeedbackFlag = false;


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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getGood() {
		return good;
	}

	public void setGood(int good) {
		this.good = good;
	}

	public String getRepresentative() {
		return representative;
	}

	public void setRepresentative(String representative) {
		this.representative = representative;
	}





	public boolean isDoneGoodFlag() {
		return doneGoodFlag;
	}

	public void setDoneGoodFlag(boolean doneGoodFlag) {
		this.doneGoodFlag = doneGoodFlag;
	}

	public boolean isDoneFeedbackFlag() {
		return doneFeedbackFlag;
	}

	public void setDoneFeedbackFlag(boolean doneFeedbackFlag) {
		this.doneFeedbackFlag = doneFeedbackFlag;
	}

	public static Booth getBooth(Context context, String id) {
		return Realm.getInstance(context)
				.where(Booth.class)
				.equalTo("id", id)
				.findFirst();
	}
}
