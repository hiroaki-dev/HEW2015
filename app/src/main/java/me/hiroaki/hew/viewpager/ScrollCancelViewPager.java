package me.hiroaki.hew.viewpager;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by hiroaki on 2016/02/28.
 */
public class ScrollCancelViewPager extends ViewPager {
	public ScrollCancelViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public ScrollCancelViewPager(Context context) {
		super(context);
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		return false;
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		return false;
	}
}
