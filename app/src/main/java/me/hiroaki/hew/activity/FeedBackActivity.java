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

import butterknife.Bind;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmResults;
import me.hiroaki.hew.R;
import me.hiroaki.hew.feedback.OnPageScrolledListener;
import me.hiroaki.hew.fragment.FeedBackFragment.OnQuestionnaireCheckedChangeListener;
import me.hiroaki.hew.model.RealmObject.Answer;
import me.hiroaki.hew.model.RealmObject.Opinion;
import me.hiroaki.hew.viewpager.ScrollCancelViewPager;
import me.hiroaki.hew.viewpager.ViewPagerAdapter;
import me.hiroaki.hew.fragment.FeedBackFragment;
import me.hiroaki.hew.fragment.OpinionFragment;
import me.hiroaki.hew.model.RealmObject.Booth;
import me.hiroaki.hew.model.RealmObject.EventCategory;
import me.hiroaki.hew.model.RealmObject.Questionnaire;


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

	EventCategory eventCategory;
	Booth booth;

//	me.hiroaki.hew.feedback.OnNextClickListener onNextClickListener;
//	me.hiroaki.hew.feedback.OnPrevClickListener onPrevClickListener;
	OnPageScrolledListener pageScrolledListener;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_feed_back);

		ButterKnife.bind(this);

		setSupportActionBar(toolbar);

		ActionBar actionBar = getSupportActionBar();
		actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);
		actionBar.setDisplayHomeAsUpEnabled(true);


		Intent intent = getIntent();
		int eventCategoryId = intent.getIntExtra(EXTRA_EVENT_CATEGORY, 0);
		String boothId = intent.getStringExtra(EXTRA_BOOTH);


		eventCategory = EventCategory.getEventCategory(this, eventCategoryId);
		booth = Booth.getBooth(this, boothId);

		stepPrev.setOnClickListener(OnPrevClickListener);
		stepNext.setOnClickListener(OnNextClickListener);
//		stepEnd.setOnClickListener(OnEndClickListener);

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
//			onPrevClickListener.onPrevClicked();
		}
	};

	View.OnClickListener OnNextClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
//			setStepNextFalse();
			viewPager.arrowScroll(ViewPager.FOCUS_RIGHT);
//			onNextClickListener.onNextClicked();
		}
	};

	View.OnClickListener OnEndClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {

		}
	};


	private ViewPagerAdapter addQuestionnaire(ViewPagerAdapter adapter, RealmResults<Questionnaire> questionnaires) {
		for (Questionnaire questionnaire: questionnaires) {
			// TODO: 学籍番号書き換え
			Answer answer = Answer.getAnswer(this, booth.getId(), "ohs503001", eventCategory.getId(), questionnaire.getLineNum());
			FeedBackFragment feedBackFragment = FeedBackFragment.newInstance(
					questionnaire.getId(),
					answer == null ? 0 : answer.getAnswerNum()
			);
//			onPrevClickListener = feedBackFragment;
//			onNextClickListener = feedBackFragment;
			pageScrolledListener = feedBackFragment;
			adapter.addFragment(feedBackFragment, String.valueOf(questionnaire.getLineNum()));
		}
		return adapter;
	}

	private ViewPagerAdapter addOpinion(ViewPagerAdapter adapter) {
		// TODO: 学籍番号書き換え
		Opinion opinion = Opinion.getOpinion(this, booth.getId(), "ohs503001");
		OpinionFragment opinionFragment = OpinionFragment.newInstance(opinion == null ? "" : opinion.getContent());
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

		// TODO: 学籍番号書き換え
		Answer answer = Answer.getAnswer(this, booth.getId(), "ohs503001", eventCategory.getId(), lineNum);
		if (answer == null) {
			answer = realm.createObject(Answer.class);
			answer.setId(Answer.getAutoIncrementId(this));

			answer.setBoothId(booth.getId());
			answer.setStudentId("ohs503001");
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

		Opinion opinion = Opinion.getOpinion(this, booth.getId(), "ohs503001");
		if (opinion == null) {
			opinion = realm.createObject(Opinion.class);
			opinion.setId(Opinion.getAutoIncrementId(this));

			opinion.setBoothId(booth.getId());
			opinion.setStudentId("ohs503001");
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

		}

		@Override
		public void onPageScrollStateChanged(int state) {
			Log.d(TAG, "onPageScrollStateChanged");
			pageScrolledListener.onPageScrolled();
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
	}

	private void setStepNextFalse() {
		Log.d(TAG, "setStepNextFlase");
		stepNext.setTextColor(getResources().getColor(R.color.grey500));
	}

//	@Override
//	public void onRadioChecked(boolean flag) {
//		if (flag) {
////			stepNext.setOnClickListener(OnNextClickListener);
//			stepNext.setTextColor(getResources().getColor(R.color.colorPrimary));
//			stepPrev.setTextColor(getResources().getColor(R.color.colorPrimary));
//		} else {
////			stepNext.setOnClickListener(null);
//			stepNext.setTextColor(getResources().getColor(R.color.grey500));
//			stepPrev.setTextColor(getResources().getColor(R.color.grey500));
//		}
//	}
}
