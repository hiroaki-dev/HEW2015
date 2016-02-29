package me.hiroaki.hew.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.realm.Realm;
import me.hiroaki.hew.R;
import me.hiroaki.hew.model.RealmObject.Answer;
import me.hiroaki.hew.model.RealmObject.Booth;
import me.hiroaki.hew.model.EventResponse;
import me.hiroaki.hew.util.AppUtil;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
	private static final String TAG = LoginActivity.class.getSimpleName();

	@Bind(R.id.login_id)
	EditText loginId;

	@Bind(R.id.password)
	EditText password;

	@Bind(R.id.login)
	Button login;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		ButterKnife.bind(this);

		login.setOnClickListener(OnLoginClickListener);


	}


	View.OnClickListener OnLoginClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			getEvents();

			finish();
		}
	};




	private void getEvents() {
		Call<EventResponse> response = AppUtil.getHewApiInstance()
				.getEvents("ohs50300");
		response.enqueue(new Callback<EventResponse>() {
			@Override
			public void onResponse(Call<EventResponse> call, Response<EventResponse> response) {
				if (!response.isSuccess()) {
					Log.e(TAG, "Request is not success");
					return;
				}

				if (response.code() < 300) {
					Realm realm = Realm.getInstance(LoginActivity.this);
					realm.beginTransaction();
					realm.copyToRealmOrUpdate(response.body().getEvent());
					realm.commitTransaction();
					Log.d(TAG, response.body().getEvent().get(0).getStart().toString());
					Log.d(TAG, "Request is success. Response is saved.");
				} else {
					Log.e(TAG, "Request is success. But Response code is " + response.code());
				}

				Intent intent = new Intent(LoginActivity.this, EventListActivity.class);
				startActivity(intent);
			}

			@Override
			public void onFailure(Call<EventResponse> call, Throwable t) {
				Log.d(TAG, t.getMessage());
				Log.d(TAG, t.toString());
				Log.d(TAG, call.request().url().toString());
				Log.e(TAG, "Request is failure");
			}
		});
	}

	private void getBooths() {
		Call<List<Booth>> response = AppUtil.getHewApiInstance()
				.getBooths(1);
		response.enqueue(new Callback<List<Booth>>() {
			@Override
			public void onResponse(Call<List<Booth>> call, Response<List<Booth>> response) {
				if (!response.isSuccess()) {
					Log.e(TAG, "Request is not success");
					return;
				}

				if (response.code() < 300) {
					Realm realm = Realm.getInstance(LoginActivity.this);
					realm.beginTransaction();
					realm.copyToRealm(response.body());
					realm.commitTransaction();
					Log.d(TAG, "Request is success. Response is saved.");
				} else {
					Log.e(TAG, "Request is success. But Response code is " + response.code());
				}
			}

			@Override
			public void onFailure(Call<List<Booth>> call, Throwable t) {
				Log.e(TAG, "Request is failure");
			}
		});
	}

	private void postLogin() {
		Call<me.hiroaki.hew.model.Response> response = AppUtil.getHewApiInstance()
				.postLogin("ohs50300", "password");
		response.enqueue(new Callback<me.hiroaki.hew.model.Response>() {
			@Override
			public void onResponse(Call<me.hiroaki.hew.model.Response> call, Response<me.hiroaki.hew.model.Response> response) {
				if (!response.isSuccess()) {
					Log.e(TAG, "Request is not success");
					return;
				}

				if (response.code() < 300) {
					// TODO: ログイン処理
					Log.d(TAG, "Request is success. Response is saved.");
				} else {
					Log.e(TAG, "Request is success. But Response code is " + response.code());
				}
			}

			@Override
			public void onFailure(Call<me.hiroaki.hew.model.Response> call, Throwable t) {
				Log.e(TAG, "Request is failure");
			}
		});
	}


	private void postGood() {
		Call<me.hiroaki.hew.model.Response> response = AppUtil.getHewApiInstance()
				.postGood("HS22", true);
		response.enqueue(new Callback<me.hiroaki.hew.model.Response>() {
			@Override
			public void onResponse(Call<me.hiroaki.hew.model.Response> call, Response<me.hiroaki.hew.model.Response> response) {
				if (!response.isSuccess()) {
					Log.e(TAG, "Request is not success");
					return;
				}

				if (response.code() < 300) {
					// TODO: setDoneGoodFlag
					Log.d(TAG, "Request is success. Response is saved.");
					Log.d(TAG, "response.isSuccess() = " + response.body().isStatus());
				} else {
					Log.e(TAG, "Request is success. But Response code is " + response.code());
				}
			}

			@Override
			public void onFailure(Call<me.hiroaki.hew.model.Response> call, Throwable t) {
				Log.e(TAG, "Request is failure");
			}
		});
	}

	private void postOpinion() {
		Call<me.hiroaki.hew.model.Response> response = AppUtil.getHewApiInstance()
				.postOpinion("HS22", "ohs50300", "hogehgoehgoehogeohgeohogheohgeo");
		response.enqueue(new Callback<me.hiroaki.hew.model.Response>() {
			@Override
			public void onResponse(Call<me.hiroaki.hew.model.Response> call, Response<me.hiroaki.hew.model.Response> response) {
				if (!response.isSuccess()) {
					Log.e(TAG, "Request is not success");
					return;
				}

				if (response.code() < 300) {
					// TODO: setDoneGoodFlag
					Log.d(TAG, "Request is success. Response is saved.");
				} else {
					Log.e(TAG, "Request is success. But Response code is " + response.code());
				}
			}

			@Override
			public void onFailure(Call<me.hiroaki.hew.model.Response> call, Throwable t) {
				Log.e(TAG, "Request is failure");
			}
		});
	}

	private void postAnswers() {
		List<Answer> answers = new ArrayList<>();
		// TODO: ループ回す
		Answer answer = new Answer();
		answer.setQuestionnaireLineNum(1);
		answer.setAnswerNum(1);
		answers.add(answer);

		Call<me.hiroaki.hew.model.Response> response = AppUtil.getHewApiInstance()
				.postAnswers("HS22", "ohs50300", answers);
		response.enqueue(new Callback<me.hiroaki.hew.model.Response>() {
			@Override
			public void onResponse(Call<me.hiroaki.hew.model.Response> call, Response<me.hiroaki.hew.model.Response> response) {
				if (!response.isSuccess()) {
					Log.e(TAG, "Request is not success");
					return;
				}

				if (response.code() < 300) {
					// TODO: setDoneGoodFlag
					Log.d(TAG, "Request is success. Response is saved.");
				} else {
					Log.e(TAG, "Request is success. But Response code is " + response.code());
				}
			}

			@Override
			public void onFailure(Call<me.hiroaki.hew.model.Response> call, Throwable t) {
				Log.e(TAG, "Request is failure");
			}
		});
	}

	private void postFeedback() {
		List<Answer> answers = new ArrayList<>();
		// TODO: ループ回す
		Answer answer = new Answer();
		answer.setQuestionnaireLineNum(1);
		answer.setAnswerNum(1);
		answers.add(answer);

		Call<me.hiroaki.hew.model.Response> response = AppUtil.getHewApiInstance()
				.postFeedback("HS22", "ohs50300", "hogehogehogehogehoghego", answers);
		response.enqueue(new Callback<me.hiroaki.hew.model.Response>() {
			@Override
			public void onResponse(Call<me.hiroaki.hew.model.Response> call, Response<me.hiroaki.hew.model.Response> response) {
				if (!response.isSuccess()) {
					Log.e(TAG, "Request is not success");
					return;
				}

				if (response.code() < 300) {
					// TODO: setDoneGoodFlag
					Log.d(TAG, "Request is success. Response is saved.");
				} else {
					Log.e(TAG, "Request is success. But Response code is " + response.code());
				}
			}

			@Override
			public void onFailure(Call<me.hiroaki.hew.model.Response> call, Throwable t) {
				Log.e(TAG, "Request is failure");
			}
		});
	}
}
