package me.hiroaki.hew.activity;

import android.content.Intent;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import butterknife.Bind;
import butterknife.ButterKnife;
import me.hiroaki.hew.R;
import me.hiroaki.hew.model.RealmObject.Booth;
import me.hiroaki.hew.model.RealmObject.Category;
import me.hiroaki.hew.util.AppUtil;

public class BoothDetailActivity extends AppCompatActivity {
	private static final String TAG = BoothDetailActivity.class.getSimpleName();

	public static final String EXTRA_BOOTH = "booth";
	public static final String EXTRA_CATEGORY = "category";

	@Bind(R.id.backdrop)
	ImageView backdrop;
	@Bind(R.id.toolbar)
	Toolbar toolbar;
	@Bind(R.id.good)
	FloatingActionButton good;
	@Bind(R.id.responsible)
	TextView responsible;
	@Bind(R.id.detail)
	TextView detail;
	@Bind(R.id.button)
	Button button;

	Category category;
	Booth booth;

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
		toolbar.setTitle(booth.getName());
		Picasso.with(this).load(AppUtil.getBoothImageUrl(booth.getId())).error(R.drawable.no_image_event).into(backdrop);



		if (!category.getEventCategory().isGoodFlag()) {
			CoordinatorLayout.LayoutParams p = (CoordinatorLayout.LayoutParams) good.getLayoutParams();
			p.setBehavior(null);
			p.setAnchorId(View.NO_ID);
			good.setLayoutParams(p);
			good.setVisibility(View.GONE);
		} else {
			good.setVisibility(View.VISIBLE);
		}



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

		button.setOnClickListener(OnButtonClickListener);
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
