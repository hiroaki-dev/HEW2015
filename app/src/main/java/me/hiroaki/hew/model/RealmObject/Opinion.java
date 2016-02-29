package me.hiroaki.hew.model.RealmObject;

import android.content.Context;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by hiroaki on 2016/02/29.
 */
public class Opinion extends RealmObject {

	@PrimaryKey
	private int id;

	private String studentId;
	private String boothId;
	private String content;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getStudentId() {
		return studentId;
	}

	public void setStudentId(String studentId) {
		this.studentId = studentId;
	}

	public String getBoothId() {
		return boothId;
	}

	public void setBoothId(String boothId) {
		this.boothId = boothId;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}


	public static Opinion getOpinion(Context context, int id) {
		return Realm.getInstance(context)
				.where(Opinion.class)
				.equalTo("id", id)
				.findFirst();
	}

	public static Opinion getOpinion(Context context, String boothId, String studentId) {
		return Realm.getInstance(context)
				.where(Opinion.class)
				.equalTo("studentId", studentId)
				.equalTo("boothId", boothId)
				.findFirst();
	}

	public static int getAutoIncrementId(Context context) {
		Number num =  Realm.getInstance(context)
				.where(Opinion.class)
				.max("id");
		if (num == null) return 1;
		else return num.intValue() + 1;
	}


}
