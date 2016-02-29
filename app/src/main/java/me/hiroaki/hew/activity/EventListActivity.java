package me.hiroaki.hew.activity;

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

import butterknife.Bind;
import butterknife.ButterKnife;
import me.hiroaki.hew.R;
import me.hiroaki.hew.viewpager.ViewPagerAdapter;
import me.hiroaki.hew.fragment.EventListFragment;

public class EventListActivity extends AppCompatActivity {

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

		if (navigationView != null) {

			navigationView.setNavigationItemSelectedListener(OnItemSelectedListener);
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
					break;
			}
			drawerLayout.closeDrawers();
			return false;
		}
	};


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
