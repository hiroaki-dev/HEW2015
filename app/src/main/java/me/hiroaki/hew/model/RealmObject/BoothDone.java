package me.hiroaki.hew.model.RealmObject;


import android.content.Context;
import android.util.Log;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by hiroaki on 2016/03/06.
 */
public class BoothDone extends RealmObject {
	private static final String TAG = BoothDone.class.getSimpleName();

	@PrimaryKey
	private String id;

	private boolean doneGoodFlag = false;
	private boolean doneFeedbackFlag = false;


	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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

	public static BoothDone getBoothDone(Context context, String id) {
		BoothDone boothDone = Realm.getInstance(context)
				.where(BoothDone.class)
				.equalTo("id", id)
				.findFirst();
		if (boothDone == null) {
			Realm realm = Realm.getInstance(context);
			realm.beginTransaction();
			boothDone = realm.createObject(BoothDone.class);
			boothDone.setId(id);
			boothDone.setDoneFeedbackFlag(false);
			boothDone.setDoneGoodFlag(false);
			realm.commitTransaction();

			boothDone = Realm.getInstance(context)
					.where(BoothDone.class)
					.equalTo("id", id)
					.findFirst();
		}
		return boothDone;
	}
}
