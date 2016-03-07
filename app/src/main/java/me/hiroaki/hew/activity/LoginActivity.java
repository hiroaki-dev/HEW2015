package me.hiroaki.hew.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.realm.Realm;
import me.hiroaki.hew.R;
import me.hiroaki.hew.model.LoginInfo;
import me.hiroaki.hew.model.RealmObject.Booth;
import me.hiroaki.hew.model.LoginInfomation;
import me.hiroaki.hew.util.AppUtil;
import me.hiroaki.hew.util.LoginSetting;
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

	LoginInfo loginInfo;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		ButterKnife.bind(this);
		loginInfo = new LoginInfo(this);


		if (loginInfo.isLogin()) {
			Intent intent = new Intent(LoginActivity.this, EventListActivity.class);
			startActivity(intent);
			finish();
		}
		login.setOnClickListener(OnLoginClickListener);

	}


	View.OnClickListener OnLoginClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			postLogin();
		}
	};


	private void postLogin() {
		if (!loginId.getText().toString().equals("guest")) {
			Toast.makeText(getApplicationContext(), "配布版ではguestユーザ以外ログインできません", Toast.LENGTH_LONG).show();
			return;
		}
		Call<LoginInfomation> response = AppUtil.getHewApiInstance()
				.postLogin(loginId.getText().toString(), password.getText().toString());
		response.enqueue(new Callback<LoginInfomation>() {
			@Override
			public void onResponse(Call<LoginInfomation> call, Response<LoginInfomation> response) {
				if (!response.isSuccess()) {
					Log.e(TAG, "Request is not success");
					return;
				}

				if (response.code() == 200) {
					if (response.body().isStatus()) {
						loginInfo.setLogin(loginId.getText().toString(), password.getText().toString(), response.body().getName());

						Intent intent = new Intent(LoginActivity.this, EventListActivity.class);
						startActivity(intent);
						finish();
					} else {
						Toast.makeText(getApplicationContext(), "ログインIDまたはパスワードが間違っています", Toast.LENGTH_LONG).show();
					}
				} else {
					Toast.makeText(getApplicationContext(), response.message(), Toast.LENGTH_LONG).show();
					Log.e(TAG, "Request is success. But Response code is " + response.code());
				}
			}

			@Override
			public void onFailure(Call<LoginInfomation> call, Throwable t) {
				Toast.makeText(getApplicationContext(), t.getLocalizedMessage(), Toast.LENGTH_LONG).show();
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






}
