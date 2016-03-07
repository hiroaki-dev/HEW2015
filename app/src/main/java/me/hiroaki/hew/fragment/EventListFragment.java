package me.hiroaki.hew.fragment;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;
import co.moonmonkeylabs.realmrecyclerview.RealmRecyclerView;
import io.realm.Realm;
import io.realm.RealmBasedRecyclerViewAdapter;
import io.realm.RealmResults;
import io.realm.RealmViewHolder;
import io.realm.Sort;
import me.hiroaki.hew.R;
import me.hiroaki.hew.activity.BoothListActivity;
import me.hiroaki.hew.model.RealmObject.Event;
import me.hiroaki.hew.util.AppUtil;


public class EventListFragment extends Fragment {
	private static final String TAG = EventListFragment.class.getSimpleName();

	public static final String BUNDLE_KEY = "tab";


	@Bind(R.id.recyclerview)
	RealmRecyclerView recyclerView;

	public EventListFragment() {
	}

	public static EventListFragment newInstance(String tab) {
		EventListFragment fragment = new EventListFragment();

		Log.d(TAG, tab);

		Bundle args = new Bundle();
		args.putString(BUNDLE_KEY, tab);
		fragment.setArguments(args);

		return  fragment;
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_event_list, container, false);
		ButterKnife.bind(this, view);
		// Listenerをセット
		setupRecyclerView(recyclerView);
		recyclerView.setOnRefreshListener(new RealmRecyclerView.OnRefreshListener() {
			@Override
			public void onRefresh() {
				Log.d(TAG, "onRefresh");
				AppUtil.getEvents(getActivity());
				recyclerView.setRefreshing(false);
//			recyclerView.setIsRefreshing(false);
			}
		});
		return view;
	}



	private void setupRecyclerView(RealmRecyclerView recyclerView) {
		if (this.getArguments() == null) return;
		Realm realm = Realm.getInstance(recyclerView.getContext());

		RealmResults<Event> eventRealmResults = null;
		switch (getArguments().getString(BUNDLE_KEY)) {
			case "すべて" :
				eventRealmResults = realm
						.where(Event.class)
						.findAllSorted("id", Sort.ASCENDING);
				break;
			case "開催中" :
				eventRealmResults = realm
						.where(Event.class)
						.lessThanOrEqualTo("start", new Date())
						.greaterThanOrEqualTo("end", new Date())
						.findAllSorted("id", Sort.ASCENDING);
				break;
			case "終了" :
				eventRealmResults = realm
						.where(Event.class)
						.lessThan("end", new Date())
						.findAllSorted("id", Sort.ASCENDING);
				break;
			default:
				return;
		}
		recyclerView.setAdapter(new EventRecyclerViewAdapter(getActivity(), eventRealmResults, true, true));
	}

	public class EventRecyclerViewAdapter extends RealmBasedRecyclerViewAdapter<Event, EventRecyclerViewAdapter.ViewHolder> {
		public EventRecyclerViewAdapter(Context context, RealmResults<Event> realmResults, boolean automaticUpdate, boolean animateResults) {
			super(context, realmResults, automaticUpdate, animateResults);
		}

		@Override
		public ViewHolder onCreateRealmViewHolder(ViewGroup viewGroup, int viewType) {
			View view = inflater.inflate(R.layout.item_event, viewGroup, false);
			ViewHolder viewHolder = new ViewHolder((RelativeLayout) view);
			return viewHolder;
		}

		@Override
		public void onBindRealmViewHolder(ViewHolder viewHolder, int position) {
			final Event event = realmResults.get(position);

			// VOS NoImage対策
			if (event.getCategory().first().getBooths().first().getId().substring(0,1).equals("V")) {
				viewHolder.eventImage.setImageDrawable(getResources().getDrawable(R.drawable.no_image_event));
			}
			Picasso.with(getContext()).load(AppUtil.getEventImageUrl(event.getId())).error(R.drawable.no_image_event).into(viewHolder.eventImage);
			viewHolder.eventName.setText(event.getName());
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH時mm分");
			SimpleDateFormat sdf2 = new SimpleDateFormat("MM月dd日 HH時mm分");
			viewHolder.eventTime.setText(sdf.format(event.getStart()) + " ~ " + sdf2.format(event.getEnd()));

			viewHolder.eventCard.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Context context = v.getContext();
					Intent intent = new Intent(context, BoothListActivity.class);
					intent.putExtra(BoothListActivity.EXTRA_NAME, event.getId());
					context.startActivity(intent);
				}
			});
		}



		public class ViewHolder extends RealmViewHolder{
			@Bind(R.id.event_card)
			public CardView eventCard;
			@Bind(R.id.event_image)
			public ImageView eventImage;
			@Bind(R.id.event_name)
			public TextView eventName;
			@Bind(R.id.event_time)
			public TextView eventTime;

			public ViewHolder(RelativeLayout container) {
				super(container);
				ButterKnife.bind(this, container);
			}
		}
	}
}
