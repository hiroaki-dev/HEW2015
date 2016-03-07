package me.hiroaki.hew.activity;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmResults;
import me.hiroaki.hew.R;
import me.hiroaki.hew.feedback.OnPageScrolledListener;
import me.hiroaki.hew.fragment.FeedBackFragment.OnQuestionnaireCheckedChangeListener;
import me.hiroaki.hew.model.RealmObject.Answer;
import me.hiroaki.hew.model.RealmObject.BoothDone;
import me.hiroaki.hew.model.RealmObject.Opinion;
import me.hiroaki.hew.model.Response;
import me.hiroaki.hew.util.AppUtil;
import me.hiroaki.hew.util.LoginSetting;
import me.hiroaki.hew.viewpager.ScrollCancelViewPager;
import me.hiroaki.hew.viewpager.ViewPagerAdapter;
import me.hiroaki.hew.fragment.FeedBackFragment;
import me.hiroaki.hew.fragment.OpinionFragment;
import me.hiroaki.hew.model.RealmObject.Booth;
import me.hiroaki.hew.model.RealmObject.EventCategory;
import me.hiroaki.hew.model.RealmObject.Questionnaire;
import retrofit2.Call;
import retrofit2.Callback;


public class FeedBackActivity extends AppCompatActivity
		implements OnQuestionnaireCheckedChangeListener, OpinionFragment.OnOpinionTextChangeListener, FeedBackFragment.OnRadioStatusChangeListener {
	private static final String TAG = FeedBackActivity.class.getSimpleName();

	public static final String EXTRA_EVENT_CATEGORY = "event_category";
	public static final String EXTRA_BOOTH = "booth_category";


	@Bind(R.id.toolbar)
	Toolbar toolbar;

	@Bind(R.id.stepPager)
	ScrollCancelViewPager viewPager;

	@Bind(R.id.stepPrev)
	TextView stepPrev;

	@Bind(R.id.stepNext)
	TextView stepNext;

	@Bind(R.id.stepEnd)
	TextView stepEnd;

	LoginSetting loginSetting;
	EventCategory eventCategory;
	Booth booth;

	List<OnPageScrolledListener> pageScrolledListener;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_feed_back);
		pageScrolledListener = new ArrayList<>();
		ButterKnife.bind(this);

		setSupportActionBar(toolbar);

		ActionBar actionBar = getSupportActionBar();
		actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);
		actionBar.setDisplayHomeAsUpEnabled(true);

		loginSetting = new LoginSetting(this);

		Intent intent = getIntent();
		int eventCategoryId = intent.getIntExtra(EXTRA_EVENT_CATEGORY, 0);
		String boothId = intent.getStringExtra(EXTRA_BOOTH);


		eventCategory = EventCategory.getEventCategory(this, eventCategoryId);
		booth = Booth.getBooth(this, boothId);

		stepPrev.setOnClickListener(OnPrevClickListener);
		stepEnd.setOnClickListener(OnEndClickListener);

		RealmResults<Questionnaire> questionnaires = Realm.getInstance(this)
				.where(Questionnaire.class)
				.equalTo("eventCategoryId", eventCategoryId)
				.findAll();

		ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
		if (eventCategory.isQuestionFlag()) {
			adapter = addQuestionnaire(adapter, questionnaires);
		}
		if (eventCategory.isOpinionFlag()) {
			adapter = addOpinion(adapter);
		}
		viewPager.setAdapter(adapter);
		viewPager.addOnPageChangeListener(OnFeedbackChangeListener);

	}

	View.OnClickListener OnPrevClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			viewPager.arrowScroll(ViewPager.FOCUS_LEFT);
		}
	};

	View.OnClickListener OnNextClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			viewPager.arrowScroll(ViewPager.FOCUS_RIGHT);
		}
	};

	View.OnClickListener OnEndClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			postFeedback();
		}
	};

	private void postFeedback() {
		Opinion opinion = Opinion.getOpinion(this, booth.getId());

		List<Answer> answers = new ArrayList<>();
		RealmResults<Answer> answerRealmResults = Answer.getAnswers(this, booth.getId());
		for(Answer answer : answerRealmResults) {
			answers.add(answer);
		}
		Call<Response> response = AppUtil.getHewApiInstance().postFeedback(
				booth.getId(),
				loginSetting.getLoginId(),
				opinion == null ? null : opinion.getContent(),
				answers == null ? null : answers);

		response.enqueue(new Callback<Response>() {
			@Override
			public void onResponse(Call<me.hiroaki.hew.model.Response> call, retrofit2.Response<Response> response) {
				if (!response.isSuccess()) {
					Log.d(TAG, String.valueOf(response.code()));
					Log.d(TAG, response.raw().toString());
					Log.e(TAG, "Request is not success");
					return;
				}

				if (response.code() < 300) {
					Realm realm = Realm.getInstance(FeedBackActivity.this);
					realm.beginTransaction();

					BoothDone boothDone = BoothDone.getBoothDone(FeedBackActivity.this, booth.getId());
					boothDone.setDoneFeedbackFlag(true);
					realm.copyToRealmOrUpdate(boothDone);
					realm.commitTransaction();

					Toast.makeText(getApplicationContext(), "送信完了", Toast.LENGTH_LONG).show();
					finish();
					Log.d(TAG, "Request is success. Response is saved.");
				} else {
					Toast.makeText(getApplicationContext(), "送信失敗", Toast.LENGTH_LONG).show();
					Log.e(TAG, "Request is success. But Response code is " + response.code());
				}
			}

			@Override
			public void onFailure(Call<me.hiroaki.hew.model.Response> call, Throwable t) {
				Toast.makeText(getApplicationContext(), "onFailure", Toast.LENGTH_LONG).show();
				Log.e(TAG, t.getMessage());
				Log.e(TAG, "Request is failure");
			}
		});
	}


	private ViewPagerAdapter addQuestionnaire(ViewPagerAdapter adapter, RealmResults<Questionnaire> questionnaires) {
		for (Questionnaire questionnaire: questionnaires) {
			Answer answer = Answer.getAnswer(this, booth.getId(), loginSetting.getLoginId(), eventCategory.getId(), questionnaire.getLineNum());
			FeedBackFragment feedBackFragment = FeedBackFragment.newInstance(
					questionnaire.getId(),
					answer == null ? 0 : answer.getAnswerNum()
			);
			pageScrolledListener.add(feedBackFragment);
			adapter.addFragment(feedBackFragment, String.valueOf(questionnaire.getLineNum()));
		}
		return adapter;
	}

	private ViewPagerAdapter addOpinion(ViewPagerAdapter adapter) {
		Opinion opinion = Opinion.getOpinion(this, booth.getId(), loginSetting.getLoginId());
		OpinionFragment opinionFragment = OpinionFragment.newInstance(opinion == null ? "" : opinion.getContent());
		pageScrolledListener.add(opinionFragment);
		adapter.addFragment(opinionFragment, "意見");
		return adapter;
	}


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

	@Override
	public void onCheckedChanged(int lineNum, int checkNum) {
		Realm realm = Realm.getInstance(this);
		realm.beginTransaction();

		Answer answer = Answer.getAnswer(this, booth.getId(), loginSetting.getLoginId(), eventCategory.getId(), lineNum);
		if (answer == null) {
			answer = realm.createObject(Answer.class);
			answer.setId(Answer.getAutoIncrementId(this));

			answer.setBoothId(booth.getId());
			answer.setStudentId(loginSetting.getLoginId());
			answer.setEventCategoryId(eventCategory.getId());
			answer.setQuestionnaireLineNum(lineNum);
		}
		answer.setAnswerNum(checkNum);

		realm.copyToRealmOrUpdate(answer);
		realm.commitTransaction();
	}

	@Override
	public void onTextChanged(String content) {
		Realm realm = Realm.getInstance(this);
		realm.beginTransaction();

		Opinion opinion = Opinion.getOpinion(this, booth.getId(), loginSetting.getLoginId());
		if (opinion == null) {
			opinion = realm.createObject(Opinion.class);
			opinion.setId(Opinion.getAutoIncrementId(this));

			opinion.setBoothId(booth.getId());
			opinion.setStudentId(loginSetting.getLoginId());
		}
		opinion.setContent(content);

		realm.copyToRealmOrUpdate(opinion);
		realm.commitTransaction();
	}

	ViewPager.OnPageChangeListener OnFeedbackChangeListener = new ViewPager.OnPageChangeListener() {
		@Override
		public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
		}

		@Override
		public void onPageSelected(int position) {
			Log.d(TAG, "onPageSelected");
			if (position == 0) {
				stepPrev.setVisibility(View.GONE);
			} else {
				stepPrev.setVisibility(View.VISIBLE);
			}

			if (position + 1 == viewPager.getAdapter().getCount()) {
				stepNext.setVisibility(View.GONE);
				stepEnd.setVisibility(View.VISIBLE);
			} else {
				stepEnd.setVisibility(View.GONE);
				stepNext.setVisibility(View.VISIBLE);
			}

			pageScrolledListener.get(position).onPageScrolled();

		}

		@Override
		public void onPageScrollStateChanged(int state) {
			Log.d(TAG, "onPageScrollStateChanged");
		}
	};

	@Override
	public void onRadioStatusChanged(boolean status) {
		Log.d(TAG, "onRadioStatusChanged" + status);
		if (status) setStepNextTrue();
		else setStepNextFalse();
	}

	private void setStepNextTrue() {
		Log.d(TAG, "setStepNextTrue");
		stepNext.setTextColor(getResources().getColor(R.color.colorPrimary));
		stepNext.setOnClickListener(OnNextClickListener);
	}

	private void setStepNextFalse() {
		Log.d(TAG, "setStepNextFalse");
		stepNext.setTextColor(getResources().getColor(R.color.grey500));
		stepNext.setOnClickListener(null);
	}

}
