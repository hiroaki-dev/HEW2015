package me.hiroaki.hew.model.RealmObject;

import android.content.Context;

import com.google.gson.annotations.SerializedName;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by hiroaki on 2016/02/14.
 */
public class Questionnaire extends RealmObject {

	@PrimaryKey
	@SerializedName("id")
	private String id;

	@SerializedName("event_category_id")
	private int eventCategoryId;
	@SerializedName("line_num")
	private int lineNum;

	@SerializedName("content")
	private String content;
	@SerializedName("choice1")
	private String choice1;
	@SerializedName("choice2")
	private String choice2;
	@SerializedName("choice3")
	private String choice3;
	@SerializedName("choice4")
	private String choice4;
	@SerializedName("choice5")
	private String choice5;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}


	public int getEventCategoryId() {
		return eventCategoryId;
	}

	public void setEventCategoryId(int eventCategoryId) {
		this.eventCategoryId = eventCategoryId;
	}
	public int getLineNum() {
		return lineNum;
	}

	public void setLineNum(int lineNum) {
		this.lineNum = lineNum;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getChoice1() {
		return choice1;
	}

	public void setChoice1(String choice1) {
		this.choice1 = choice1;
	}

	public String getChoice2() {
		return choice2;
	}

	public void setChoice2(String choice2) {
		this.choice2 = choice2;
	}

	public String getChoice3() {
		return choice3;
	}

	public void setChoice3(String choice3) {
		this.choice3 = choice3;
	}

	public String getChoice4() {
		return choice4;
	}

	public void setChoice4(String choice4) {
		this.choice4 = choice4;
	}

	public String getChoice5() {
		return choice5;
	}

	public void setChoice5(String choice5) {
		this.choice5 = choice5;
	}

	public static Questionnaire getQuestionnaire(Context context, String id) {
		return Realm.getInstance(context)
				.where(Questionnaire.class)
				.equalTo("id", id)
				.findFirst();
	}
}
