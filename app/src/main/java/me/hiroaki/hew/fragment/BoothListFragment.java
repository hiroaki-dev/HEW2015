package me.hiroaki.hew.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import butterknife.Bind;
import butterknife.ButterKnife;
import co.moonmonkeylabs.realmrecyclerview.RealmRecyclerView;
import io.realm.Realm;
import io.realm.RealmBasedRecyclerViewAdapter;
import io.realm.RealmResults;
import io.realm.RealmViewHolder;
import me.hiroaki.hew.R;
import me.hiroaki.hew.activity.BoothDetailActivity;
import me.hiroaki.hew.model.RealmObject.Booth;
import me.hiroaki.hew.model.RealmObject.Category;
import me.hiroaki.hew.util.AppUtil;

public class BoothListFragment extends Fragment {
	private static final String TAG = BoothListFragment.class.getSimpleName();

	public static final String BUNDLE_KEY = "category";

	@Bind(R.id.recyclerview)
	RealmRecyclerView recyclerView;

	Category category;

	public BoothListFragment() {}


	public static BoothListFragment newInstance(String categoryId) {
		BoothListFragment fragment = new BoothListFragment();

		Bundle args = new Bundle();
		args.putString(BUNDLE_KEY, categoryId);
		fragment.setArguments(args);

		return fragment;
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_booth_list, container, false);
		ButterKnife.bind(this, view);
		setupRecyclerView(recyclerView);
		return view;
	}


	private void setupRecyclerView(final RealmRecyclerView recyclerView) {
		if (getArguments() == null) return;

		category = Category.getCategory(getContext(), getArguments().getString(BUNDLE_KEY));

		RealmResults<Booth> boothRealmResults = category
				.getBooths()	// RealmList<Booth>取得
				.where()
				.findAll();		// RealmResult<Booth>取得

		recyclerView.addItemDocoration(new DividerItemDecoration(getActivity()));
		recyclerView.setAdapter(new BoothRecyclerViewAdapter(getActivity(), boothRealmResults, true, true));
		recyclerView.setOnRefreshListener(new RealmRecyclerView.OnRefreshListener() {
			@Override
			public void onRefresh() {
				Log.d(TAG, "onRefresh");
				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						// 更新が終了したらインジケータ非表示
						recyclerView.setRefreshing(false);
						recyclerView.setEnabled(false);
					}
				}, 2000);
			}
		});

	}


	public class DividerItemDecoration extends RecyclerView.ItemDecoration {
		private final int[] ATTRS = new int[]{
				android.R.attr.listDivider
		};

		private Drawable mDivider;

		public DividerItemDecoration(Context context) {
			final TypedArray a = context.obtainStyledAttributes(ATTRS);
			mDivider = a.getDrawable(0);
			a.recycle();
		}

		@Override
		public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
			drawVertical(c, parent);
		}

		public void drawVertical(Canvas c, RecyclerView parent) {
			final int left = parent.getPaddingLeft();
			final int right = parent.getWidth() - parent.getPaddingRight();

			final int childCount = parent.getChildCount();
			for (int i = 0; i < childCount; i++) {
				final View child = parent.getChildAt(i);
				final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child
						.getLayoutParams();
				final int top = child.getBottom() + params.bottomMargin;
				final int bottom = top + mDivider.getIntrinsicHeight();
				mDivider.setBounds(left, top, right, bottom);
				mDivider.draw(c);
			}
		}

		@Override
		public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
			outRect.set(0, 0, 0, mDivider.getIntrinsicHeight());
		}
	}

	public class BoothRecyclerViewAdapter extends RealmBasedRecyclerViewAdapter<Booth, BoothRecyclerViewAdapter.ViewHolder> {
		public BoothRecyclerViewAdapter(Context context, RealmResults<Booth> realmResults, boolean automaticUpdate, boolean animateResults) {
			super(context, realmResults, automaticUpdate, animateResults);
		}

		@Override
		public ViewHolder onCreateRealmViewHolder(ViewGroup viewGroup, int viewType) {
			View view = inflater.inflate(R.layout.item_booth, viewGroup, false);
			ViewHolder viewHolder = new ViewHolder((RelativeLayout) view);
			return viewHolder;
		}

		@Override
		public void onBindRealmViewHolder(ViewHolder viewHolder, int position) {
			final Booth booth = realmResults.get(position);
			Picasso.with(getContext()).load(AppUtil.getBoothImageUrl(booth.getId())).error(R.drawable.no_image_booth).into(viewHolder.boothImageIcon);
			viewHolder.boothName.setText(booth.getName());

			String[] representatives = AppUtil.getSplitedString(booth.getRepresentative());
			String representative;
			if (representatives.length > 1) representative = representatives[0] + " 他";
			else representative = representatives[0];
			viewHolder.representative.setText(representative);

			if (category.getEventCategory().isGoodFlag()) {
				viewHolder.good.setText(booth.getGood() + "件");
			} else {
				viewHolder.good.setVisibility(View.GONE);
			}
			viewHolder.mView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Context context = v.getContext();
					Intent intent = new Intent(context, BoothDetailActivity.class);
					intent.putExtra(BoothDetailActivity.EXTRA_BOOTH, booth.getId());
					intent.putExtra(BoothDetailActivity.EXTRA_CATEGORY, getArguments().getString(BUNDLE_KEY));
					context.startActivity(intent);
				}
			});
		}



		public class ViewHolder extends RealmViewHolder {
			public View mView;
			@Bind(R.id.booth_image_icon)
			public ImageView boothImageIcon;
			@Bind(R.id.feedback)
			public ImageView feedback;

			@Bind(R.id.booth_name)
			public TextView boothName;
			@Bind(R.id.representative)
			public TextView representative;
			@Bind(R.id.good)
			public TextView good;

			public ViewHolder(View container) {
				super(container);
				ButterKnife.bind(this, container);
				mView = container;
			}
		}
	}

}
