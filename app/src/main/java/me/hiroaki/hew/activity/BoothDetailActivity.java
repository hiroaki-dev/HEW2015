package me.hiroaki.hew.activity;

import android.content.Intent;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.realm.Realm;
import me.hiroaki.hew.R;
import me.hiroaki.hew.model.RealmObject.Booth;
import me.hiroaki.hew.model.RealmObject.BoothDone;
import me.hiroaki.hew.model.RealmObject.Category;
import me.hiroaki.hew.model.Response;
import me.hiroaki.hew.util.AppUtil;
import retrofit2.Call;
import retrofit2.Callback;

public class BoothDetailActivity extends AppCompatActivity {
	private static final String TAG = BoothDetailActivity.class.getSimpleName();

	public static final String EXTRA_BOOTH = "booth";
	public static final String EXTRA_CATEGORY = "category";

	@Bind(R.id.toolbar)
	Toolbar toolbar;
	@Bind(R.id.good)
	FloatingActionButton good;
	@Bind(R.id.responsible)
	TextView responsible;
	@Bind(R.id.detail)
	TextView detail;
	@Bind(R.id.button)
	AppCompatButton button;
	@Bind(R.id.backdrop)
	AppCompatImageView backdrop;
	Category category;
	Booth booth;
	BoothDone boothDone;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_booth_detail);
		ButterKnife.bind(this);

		setSupportActionBar(toolbar);

		ActionBar actionBar = getSupportActionBar();
		actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);
		actionBar.setDisplayHomeAsUpEnabled(true);


		Intent intent = getIntent();
		String boothId = intent.getStringExtra(EXTRA_BOOTH);
		String categoryId = intent.getStringExtra(EXTRA_CATEGORY);
		category = Category.getCategory(this, categoryId);

		booth = Booth.getBooth(this, boothId);
		boothDone = BoothDone.getBoothDone(this, booth.getId());
		if (!category.getEventCategory().isQuestionFlag() && !category.getEventCategory().isOpinionFlag()){
			button.setVisibility(View.GONE);
		}

		actionBar.setTitle(booth.getName());
		Picasso.with(this).load(AppUtil.getBoothImageUrl(booth.getId())).error(R.drawable.no_image_event).into(backdrop);

		backdrop.setOnClickListener(OnBackDropClickListener);

		button.setOnClickListener(OnButtonClickListener);

		if (!category.getEventCategory().isGoodFlag()) {
			CoordinatorLayout.LayoutParams p = (CoordinatorLayout.LayoutParams) good.getLayoutParams();
			p.setBehavior(null);
			p.setAnchorId(View.NO_ID);
			good.setLayoutParams(p);
			good.setVisibility(View.GONE);
		} else {
			good.setVisibility(View.VISIBLE);
		}

		if (boothDone.isDoneGoodFlag()) {
			good.setImageDrawable(getResources().getDrawable(R.drawable.ic_thumb_up_yellow_600_24dp));
		} else {
			good.setImageDrawable(getResources().getDrawable(R.drawable.ic_thumb_up_grey_300_24dp));
		}
		good.setOnClickListener(OnGoodClickListener);

		String representative = null;
		for (String person: AppUtil.getSplitedString(booth.getRepresentative())) {
			if (representative == null) {
				representative = person;
				continue;
			}
			representative += "\n" +person;
		}
		responsible.setText(representative);
		detail.setText(booth.getDescription());



	}

	View.OnClickListener OnGoodClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			postGood();
		}
	};

	private void postGood() {
		Call<Response> response = AppUtil.getHewApiInstance()
				.postGood(booth.getId(), !boothDone.isDoneGoodFlag() ? "true" : "false");
		response.enqueue(new Callback<Response>() {
			@Override
			public void onResponse(Call<me.hiroaki.hew.model.Response> call, retrofit2.Response<Response> response) {
				if (!response.isSuccess()) {
					Log.e(TAG, "Request is not success");
					return;
				}

				if (response.code() < 300) {
					// TODO: setDoneGoodFlag
					Log.d(TAG, "Request is success. Response is saved.");
					Log.d(TAG, "response.isSuccess() = " + response.body().isStatus());
					if (!boothDone.isDoneGoodFlag()) {
						good.setImageDrawable(getResources().getDrawable(R.drawable.ic_thumb_up_yellow_600_24dp));
					} else {
						good.setImageDrawable(getResources().getDrawable(R.drawable.ic_thumb_up_grey_300_24dp));
					}

					Realm realm = Realm.getInstance(BoothDetailActivity.this);
					realm.beginTransaction();
					boothDone.setDoneGoodFlag(!boothDone.isDoneGoodFlag());
					realm.commitTransaction();
				} else {
					Log.e(TAG, "Request is success. But Response code is " + response.code());
					Toast.makeText(getApplicationContext(), "送信できませんでした", Toast.LENGTH_LONG).show();
				}
			}

			@Override
			public void onFailure(Call<me.hiroaki.hew.model.Response> call, Throwable t) {
				Log.e(TAG, "Request is failure");
			}
		});
	}

	View.OnClickListener OnBackDropClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			MaterialDialog dialog = new MaterialDialog.Builder(BoothDetailActivity.this)
					.title(booth.getName())
					.customView(R.layout.dialog_booth_detail, true)
					.build();
			ImageView boothImage = (ImageView) dialog.findViewById(R.id.booth_image);
			TextView boothDetail = (TextView) dialog.findViewById(R.id.booth_detail);
			TextView boothRepresentative = (TextView) dialog.findViewById(R.id.booth_representative);

			Picasso.with(BoothDetailActivity.this).load(AppUtil.getBoothImageUrl(booth.getId())).error(R.drawable.no_image_booth).into(boothImage);
			boothDetail.setText(booth.getDescription());
			String[] representatives = AppUtil.getSplitedString(booth.getRepresentative());
			String representative = "";
			if (representatives.length > 1) {
				for (String person : representatives) {
					representative += person + "\n";
				}
			} else representative = representatives[0];
			boothRepresentative.setText(representative);

			dialog.show();
		}
	};

	@Override
	protected void onResume() {
		super.onResume();
		BoothDone boothDone = BoothDone.getBoothDone(this, booth.getId());
		if (boothDone.isDoneFeedbackFlag()) {
			button.setText(R.string.answered);
			button.setEnabled(false);
		} else {
			button.setEnabled(true);
		}
	}

	View.OnClickListener OnButtonClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			Intent intent = new Intent(BoothDetailActivity.this, FeedBackActivity.class);
			intent.putExtra(FeedBackActivity.EXTRA_BOOTH, booth.getId());
			intent.putExtra(FeedBackActivity.EXTRA_EVENT_CATEGORY, category.getEventCategory().getId());
			startActivity(intent);
		}
	};

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		switch (id) {
			case  android.R.id.home :
				finish();
				break;
			default:
				break;
		}

		return super.onOptionsItemSelected(item);
	}
}
