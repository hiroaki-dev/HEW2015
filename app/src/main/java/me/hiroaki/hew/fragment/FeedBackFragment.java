package me.hiroaki.hew.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatRadioButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import me.hiroaki.hew.R;
import me.hiroaki.hew.feedback.OnPageScrolledListener;
import me.hiroaki.hew.model.RealmObject.Questionnaire;

import static me.hiroaki.hew.activity.FeedBackActivity.*;


public class FeedBackFragment extends Fragment implements OnPageScrolledListener {
	private static final String TAG = FeedBackFragment.class.getSimpleName();

	public static final String BUNDLE_KEY_QUESTIONNAIRE = "questionnaire_id";
	public static final String BUNDLE_KEY_ANSWER_NUM = "answer_num";

	private OnQuestionnaireCheckedChangeListener questionnaireCheckedChangeListener;
//	private OnRadioGroupCheckListener radioGroupCheckListener;
	private OnRadioStatusChangeListener radioStatusChangeListener;
	private int lineNum;

	@Bind(R.id.question_number)
	TextView questionNumber;

	@Bind(R.id.question_detail)
	TextView questionDetail;

	@Bind(R.id.choice_group)
	RadioGroup questionnaires;

	@Bind(R.id.choice1)
	AppCompatRadioButton choice1;

	@Bind(R.id.choice2)
	AppCompatRadioButton choice2;

	@Bind(R.id.choice3)
	AppCompatRadioButton choice3;

	@Bind(R.id.choice4)
	AppCompatRadioButton choice4;

	@Bind(R.id.choice5)
	AppCompatRadioButton choice5;

	public FeedBackFragment() {}

	public static FeedBackFragment newInstance(String questionnaireId, int answerNum) {
		FeedBackFragment fragment = new FeedBackFragment();

		Bundle args = new Bundle();
		args.putString(BUNDLE_KEY_QUESTIONNAIRE, questionnaireId);
		args.putInt(BUNDLE_KEY_ANSWER_NUM, answerNum);
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public void onAttach(Context context) {
		super.onAttach(context);
		try {
			questionnaireCheckedChangeListener = (OnQuestionnaireCheckedChangeListener) getActivity();
//			radioGroupCheckListener = (OnRadioGroupCheckListener) getActivity();
			radioStatusChangeListener = (OnRadioStatusChangeListener) getActivity();
		} catch (ClassCastException e) {
			throw new ClassCastException(getActivity().toString()
					+ " must implement OnQuestionnaireCheckedChangeListener");
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_feed_back, container, false);
		ButterKnife.bind(this, view);
		questionnaires.setOnCheckedChangeListener(OnQuestionnairesChangeListener);

		Questionnaire questionnaire = Questionnaire.getQuestionnaire(getContext(), getArguments().getString(BUNDLE_KEY_QUESTIONNAIRE));
		lineNum = questionnaire.getLineNum();
		questionNumber.setText("Q" + questionnaire.getLineNum());
		questionDetail.setText(questionnaire.getContent());
		choice1.setText(questionnaire.getChoice1());
		choice2.setText(questionnaire.getChoice2());
		choice3.setText(questionnaire.getChoice3());
		choice4.setText(questionnaire.getChoice4());
		choice5.setText(questionnaire.getChoice5());

		Log.d(TAG, "BUNDLE_ANSWER_NUM" + getArguments().getInt(BUNDLE_KEY_ANSWER_NUM));
		switch (getArguments().getInt(BUNDLE_KEY_ANSWER_NUM)) {
			case 1:
				questionnaires.check(R.id.choice1);
				break;
			case 2:
				questionnaires.check(R.id.choice2);
				break;
			case 3:
				questionnaires.check(R.id.choice3);
				break;
			case 4:
				questionnaires.check(R.id.choice4);
				break;
			case 5:
				questionnaires.check(R.id.choice5);
				break;
			default:
				break;
		}
//		if (getArguments().getInt(BUNDLE_KEY_ANSWER_NUM) != 0) radioGroupCheckListener.onRadioChecked(true);

		return view;
	}


	@Override
	public void onDetach() {
		super.onDetach();
		questionnaireCheckedChangeListener = null;
//		radioGroupCheckListener = null;
	}

	RadioGroup.OnCheckedChangeListener OnQuestionnairesChangeListener = new RadioGroup.OnCheckedChangeListener() {
		@Override
		public void onCheckedChanged(RadioGroup group, int checkedId) {
			if (checkedId != -1) {
				Log.d(TAG, "radio group sat (checkedId = " + checkedId);
				radioStatusChangeListener.onRadioStatusChanged(true);
				switch (checkedId) {
					case R.id.choice1:
						questionnaireCheckedChangeListener.onCheckedChanged(lineNum, 1);
						break;
					case R.id.choice2:
						questionnaireCheckedChangeListener.onCheckedChanged(lineNum, 2);
						break;
					case R.id.choice3:
						questionnaireCheckedChangeListener.onCheckedChanged(lineNum, 3);
						break;
					case R.id.choice4:
						questionnaireCheckedChangeListener.onCheckedChanged(lineNum, 4);
						break;
					case R.id.choice5:
						questionnaireCheckedChangeListener.onCheckedChanged(lineNum, 5);
						break;
					default:
						break;
				}
			}
		}
	};

	@Override
	public void onPageScrolled() {
		if (questionnaires.getCheckedRadioButtonId() == -1) radioStatusChangeListener.onRadioStatusChanged(false);
		else radioStatusChangeListener.onRadioStatusChanged(true);
	}
//
//	@Override
//	public void onNextClicked() {
//		Log.d(TAG, "FeedbackFragment onNextClicked");
//		Log.d(TAG, "getChecedRadioButtonId = " + questionnaires.getCheckedRadioButtonId());
//		radioGroupCheckListener.onRadioChecked(questionnaires.getCheckedRadioButtonId() == -1 ? false : true);
//	}
//
//	@Override
//	public void onPrevClicked() {
//		Log.d(TAG, "FeedbackFragment onPrevClicked");
//		radioGroupCheckListener.onRadioChecked(questionnaires.getCheckedRadioButtonId() == -1 ? false : true);
//	}

	public interface OnQuestionnaireCheckedChangeListener {
		void onCheckedChanged(int lineNum, int answer);
	}
//
//	public interface OnRadioGroupCheckListener {
//		void onRadioChecked(boolean flag);
//	}

	public interface OnRadioStatusChangeListener {
		void onRadioStatusChanged(boolean status);
	}

}
