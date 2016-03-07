package me.hiroaki.hew.model.RealmObject;

import android.content.Context;

import com.google.gson.annotations.SerializedName;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmResults;
import io.realm.annotations.PrimaryKey;

/**
 * Created by hiroaki on 2016/02/14.
 */
public class Answer extends RealmObject {
	@PrimaryKey
	private int id;

	@SerializedName("booth_id")
	private String boothId;
	@SerializedName("student_id")
	private String studentId;
	@SerializedName("eventCategoryId")
	private int eventCategoryId;
	@SerializedName("questionnaire_line_num")
	private int questionnaireLineNum;
	@SerializedName("answer_num")
	private int answerNum;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getBoothId() {
		return boothId;
	}

	public void setBoothId(String boothId) {
		this.boothId = boothId;
	}

	public String getStudentId() {
		return studentId;
	}

	public void setStudentId(String studentId) {
		this.studentId = studentId;
	}

	public int getEventCategoryId() {
		return eventCategoryId;
	}

	public void setEventCategoryId(int eventCategoryId) {
		this.eventCategoryId = eventCategoryId;
	}

	public int getQuestionnaireLineNum() {
		return questionnaireLineNum;
	}

	public void setQuestionnaireLineNum(int questionnaireLineNum) {
		this.questionnaireLineNum = questionnaireLineNum;
	}

	public int getAnswerNum() {
		return answerNum;
	}

	public void setAnswerNum(int answerNum) {
		this.answerNum = answerNum;
	}

	public static Answer getAnswer(Context context, int id) {
		return Realm.getInstance(context)
				.where(Answer.class)
				.equalTo("id", id)
				.findFirst();
	}

	public static Answer getAnswer(Context context, String boothId, String studentId, int eventCategoryId, int questionnaireLineNum) {
		return Realm.getInstance(context)
				.where(Answer.class)
				.equalTo("boothId", boothId)
				.equalTo("studentId", studentId)
				.equalTo("eventCategoryId", eventCategoryId)
				.equalTo("questionnaireLineNum", questionnaireLineNum)
				.findFirst();
	}

	public static RealmResults<Answer> getAnswers(Context context, String boothId) {
		return Realm.getInstance(context)
				.where(Answer.class)
				.equalTo("boothId", boothId)
				.findAll();
	}

	public static RealmResults<Answer> getAllAnswers(Context context) {
		return Realm.getInstance(context)
				.where(Answer.class)
				.findAll();
	}

	public static int getAutoIncrementId(Context context) {
		Number num =  Realm.getInstance(context)
				.where(Answer.class)
				.max("id");
		if (num == null) return 1;
		else return num.intValue() + 1;
	}

}
