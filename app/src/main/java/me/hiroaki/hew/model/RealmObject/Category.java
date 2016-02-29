package me.hiroaki.hew.model.RealmObject;

import android.content.Context;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by hiroaki on 2016/02/14.
 */
public class Category extends RealmObject implements Serializable{

	@PrimaryKey
	@SerializedName("id")
	private String id;
	@SerializedName("event_category")
	private EventCategory eventCategory;
	@SerializedName("booths")
	private RealmList<Booth> booths;


	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public EventCategory getEventCategory() {
		return eventCategory;
	}

	public void setEventCategory(EventCategory eventCategory) {
		this.eventCategory = eventCategory;
	}

	public RealmList<Booth> getBooths() {
		return booths;
	}

	public void setBooths(RealmList<Booth> booths) {
		this.booths = booths;
	}

	public static Category getCategory(Context context, String id) {
		return Realm.getInstance(context)
				.where(Category.class)
				.equalTo("id", id)
				.findFirst();
	}
}
