package me.hiroaki.hew.model.RealmObject;

import android.content.Context;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by hiroaki on 2016/02/11.
 */
public class Event extends RealmObject {
	@PrimaryKey
	@SerializedName("id")
	private int id;

	@SerializedName("name")
	private String name;
	@SerializedName("start")
	private Date start;
	@SerializedName("end")
	private Date end;
	@SerializedName("category")
	private RealmList<Category> category;





	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getStart() {
		return start;
	}

	public void setStart(Date start) {
		this.start = start;
	}

	public Date getEnd() {
		return end;
	}

	public void setEnd(Date end) {
		this.end = end;
	}

	public RealmList<Category> getCategory() {
		return category;
	}

	public void setCategory(RealmList<Category> category) {
		this.category = category;
	}

	public static Event getEvent(Context context, int id) {
		return Realm.getInstance(context)
				.where(Event.class)
				.equalTo("id", id)
				.findFirst();
	}

}
