package me.hiroaki.hew.activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.realm.RealmResults;
import me.hiroaki.hew.R;
import me.hiroaki.hew.viewpager.ViewPagerAdapter;
import me.hiroaki.hew.fragment.BoothListFragment;
import me.hiroaki.hew.model.RealmObject.Category;
import me.hiroaki.hew.model.RealmObject.Event;
import me.hiroaki.hew.util.AppUtil;

public class BoothListActivity extends AppCompatActivity {
	private static final String TAG = BoothListActivity.class.getSimpleName();

	public static final String EXTRA_NAME = "event";

	@Bind(R.id.collapsing_toolbar)
	CollapsingToolbarLayout collapsingToolbarLayout;

	@Bind(R.id.toolbar)
	Toolbar toolbar;

	@Bind(R.id.backdrop)
	AppCompatImageView backdrop;

	@Bind(R.id.viewpager)
	ViewPager viewPager;

	@Bind(R.id.tabs)
	TabLayout tabLayout;

	Event event;
	int currentPosition = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_booth_list);
		ButterKnife.bind(this);

		setSupportActionBar(toolbar);

		ActionBar actionBar = getSupportActionBar();
		actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);
		actionBar.setDisplayHomeAsUpEnabled(true);

		backdrop.setOnClickListener(OnBackDropClickListener);


		Intent intent = getIntent();
		int id = intent.getIntExtra(EXTRA_NAME, -1);
		event = Event.getEvent(this, id);
		collapsingToolbarLayout.setTitle(event.getName());
		Picasso.with(this).load(AppUtil.getEventImageUrl(event.getId())).error(R.drawable.no_image_event).into(backdrop);

		viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
			@Override
			public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

			}

			@Override
			public void onPageSelected(int position) {
				currentPosition = position;
			}

			@Override
			public void onPageScrollStateChanged(int state) {

			}
		});

	}

	@Override
	protected void onResume() {
		super.onResume();

		setupViewPager(viewPager, event);
		tabLayout.setupWithViewPager(viewPager);

	}


	View.OnClickListener OnBackDropClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			MaterialDialog dialog = new MaterialDialog.Builder(BoothListActivity.this)
					.title(event.getName())
					.customView(R.layout.dialog_event_detail, true)
					.build();
			ImageView eventImage = (ImageView) dialog.findViewById(R.id.event_image);
			TextView eventDetail = (TextView) dialog.findViewById(R.id.event_detail);
			TextView eventTime = (TextView) dialog.findViewById(R.id.event_time);

			Picasso.with(BoothListActivity.this).load(AppUtil.getEventImageUrl(event.getId())).error(R.drawable.no_image_event).into(eventImage);
			eventDetail.setText(event.getDetail());
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH時mm分");
			eventTime.setText(sdf.format(event.getStart()) + " ~\n" + sdf.format(event.getEnd()));

			dialog.show();
		}
	};

	private void setupViewPager(ViewPager viewPager, Event event) {
		RealmResults<Category> categoryRealmResults = event.getCategory().where().findAll();
		ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
		for (Category category: categoryRealmResults) {
			BoothListFragment boothListFragment = BoothListFragment.newInstance(category.getId());
			adapter.addFragment(boothListFragment, category.getEventCategory().getName());
		}
		viewPager.setAdapter(adapter);
		viewPager.setCurrentItem(currentPosition);
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
}
