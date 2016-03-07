package me.hiroaki.hew.activity;

import android.content.Intent;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.realm.Realm;
import me.hiroaki.hew.R;
import me.hiroaki.hew.model.LoginInfo;
import me.hiroaki.hew.model.RealmObject.Answer;
import me.hiroaki.hew.model.RealmObject.Booth;
import me.hiroaki.hew.model.RealmObject.BoothDone;
import me.hiroaki.hew.model.RealmObject.Category;
import me.hiroaki.hew.model.RealmObject.Event;
import me.hiroaki.hew.model.RealmObject.EventCategory;
import me.hiroaki.hew.model.LoginInfomation;
import me.hiroaki.hew.model.RealmObject.Opinion;
import me.hiroaki.hew.model.RealmObject.Questionnaire;
import me.hiroaki.hew.util.AppUtil;
import me.hiroaki.hew.util.LoginSetting;
import me.hiroaki.hew.viewpager.ViewPagerAdapter;
import me.hiroaki.hew.fragment.EventListFragment;

public class EventListActivity extends AppCompatActivity {
	private static final String TAG = EventListActivity.class.getSimpleName();

	@Bind(R.id.drawer_layout)
	DrawerLayout drawerLayout;

	@Bind(R.id.nav_view)
	NavigationView navigationView;

	@Bind(R.id.toolbar)
	Toolbar toolbar;

	@Bind(R.id.viewpager)
	ViewPager viewPager;

	@Bind(R.id.tabs)
	TabLayout tabLayout;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_event_list);
		ButterKnife.bind(this);

		setSupportActionBar(toolbar);

		ActionBar actionBar = getSupportActionBar();
		actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_white_18dp);
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setTitle(getResources().getString(R.string.title));
		AppUtil.getEvents(getApplicationContext());

		if (navigationView != null) {
			navigationView.setNavigationItemSelectedListener(OnItemSelectedListener);

			LoginInfo loginInfo = new LoginInfo(this);
			View view = navigationView.getHeaderView(0);
			TextView name = ButterKnife.findById(view, R.id.name);
			name.setText(loginInfo.getName());
			TextView studentId = ButterKnife.findById(view,R.id.student_id);
			studentId.setText(loginInfo.getLoginId());
		}


		if (viewPager != null) {
			setupViewPager(viewPager);
		}

		tabLayout.setupWithViewPager(viewPager);
		viewPager.setCurrentItem(1);
	}

	NavigationView.OnNavigationItemSelectedListener OnItemSelectedListener = new NavigationView.OnNavigationItemSelectedListener() {
		@Override
		public boolean onNavigationItemSelected(MenuItem item) {
			switch (item.getItemId()) {
				case R.id.all:
					viewPager.setCurrentItem(0);
					break;
				case R.id.now_open:
					viewPager.setCurrentItem(1);
					break;
				case R.id.end:
					viewPager.setCurrentItem(2);
					break;
				default:
					logout();
					break;
			}
			drawerLayout.closeDrawers();
			return false;
		}
	};

	private void logout() {
		Realm realm = Realm.getInstance(this);
		realm.beginTransaction();
		Answer.getAllAnswers(this).clear();
		Booth.getAllBooth(this).clear();
		Category.getAllCategory(this).clear();
		Event.getAllEvent(this).clear();
		EventCategory.getAllEventCategory(this).clear();
		Opinion.getAllOpinion(this).clear();
		Questionnaire.getAllQuestionnaire(this).clear();
		BoothDone.getAllBoothDone(this).clear();
		realm.commitTransaction();

		LoginSetting loginSetting = new LoginSetting(EventListActivity.this);
		loginSetting.removeLogin();
		Intent intent = new Intent(EventListActivity.this, LoginActivity.class);
		startActivity(intent);
		finish();
	}


	private void setupViewPager(ViewPager viewPager) {
		ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
		adapter.addFragment(EventListFragment.newInstance("すべて"), "すべて");
		adapter.addFragment(EventListFragment.newInstance("開催中"), "開催中");
		adapter.addFragment(EventListFragment.newInstance("終了"), "終了");
		viewPager.setAdapter(adapter);
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				drawerLayout.openDrawer(GravityCompat.START);
				return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
