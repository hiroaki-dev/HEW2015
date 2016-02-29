package me.hiroaki.hew.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatEditText;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import me.hiroaki.hew.R;


public class OpinionFragment extends Fragment {
	private static final String TAG = OpinionFragment.class.getSimpleName();

	private static final String BUNDLE_KEY = "content";

	OnOpinionTextChangeListener opinionTextChangeListener;

	@Bind(R.id.count)
	TextView count;

	@Bind(R.id.opinion)
	AppCompatEditText opinion;

	public OpinionFragment() {
	}

	public static OpinionFragment newInstance(String content) {
		OpinionFragment fragment = new OpinionFragment();

		Bundle args = new Bundle();
		args.putString(BUNDLE_KEY, content);
		fragment.setArguments(args);
		return fragment;
	}



	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view =  inflater.inflate(R.layout.fragment_opinion, container, false);
		ButterKnife.bind(this, view);

		opinion.addTextChangedListener(OpinionWatcher);
		opinion.setText(getArguments().getString(BUNDLE_KEY));




		count.setText(opinion.length() + "文字");
		// TODO: RealmにquestionnaireIdで問い合わせてある場合は値をセット

		return view;
	}





	@Override
	public void onAttach(Context context) {
		super.onAttach(context);
		try {
			opinionTextChangeListener = (OnOpinionTextChangeListener) getActivity();
		} catch (ClassCastException e) {
			throw new ClassCastException(getActivity().toString()
					+ " must implement OnOpinionTextChangeListener");
		}
	}

	TextWatcher OpinionWatcher = new TextWatcher() {
		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {

		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int changeCount) {
		}

		@Override
		public void afterTextChanged(Editable s) {
			count.setText(s.length() + "文字");
			opinionTextChangeListener.onTextChanged(s.toString());
		}
	};

	@Override
	public void onDetach() {
		super.onDetach();
		opinionTextChangeListener = null;
	}

	public interface OnOpinionTextChangeListener {
		void onTextChanged(String opinion);
	}

}
