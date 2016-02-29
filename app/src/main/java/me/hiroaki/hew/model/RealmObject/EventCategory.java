package me.hiroaki.hew.model.RealmObject;

import android.content.Context;

import com.google.gson.annotations.SerializedName;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by hiroaki on 2016/02/17.
 */
public class EventCategory extends RealmObject {

	@PrimaryKey
	@SerializedName("id")
	private int id;

	@SerializedName("name")
	private String name;
	@SerializedName("opinion_flag")
	private boolean opinionFlag;
	@SerializedName("good_flag")
	private boolean goodFlag;
	@SerializedName("question_flag")
	private boolean questionFlag;
	@SerializedName("questionnaire")
	private RealmList<Questionnaire> questionnaires;

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

	public boolean isOpinionFlag() {
		return opinionFlag;
	}

	public void setOpinionFlag(boolean opinionFlag) {
		this.opinionFlag = opinionFlag;
	}

	public boolean isGoodFlag() {
		return goodFlag;
	}

	public void setGoodFlag(boolean goodFlag) {
		this.goodFlag = goodFlag;
	}

	public boolean isQuestionFlag() {
		return questionFlag;
	}

	public void setQuestionFlag(boolean questionFlag) {
		this.questionFlag = questionFlag;
	}

	public RealmList<Questionnaire> getQuestionnaires() {
		return questionnaires;
	}

	public void setQuestionnaires(RealmList<Questionnaire> questionnaires) {
		this.questionnaires = questionnaires;
	}

	public static EventCategory getEventCategory(Context context, int id) {
		return Realm.getInstance(context)
				.where(EventCategory.class)
				.equalTo("id", id)
				.findFirst();
	}
}
