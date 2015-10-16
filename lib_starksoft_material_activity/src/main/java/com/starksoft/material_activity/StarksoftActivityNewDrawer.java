package com.starksoft.material_activity;

import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.view.ActionMode.Callback;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ReplacementSpan;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;

public class StarksoftActivityNewDrawer extends AppCompatActivity
{
	private DrawerLayout mDrawerLayout;
	private NavigationView mNavigationView;
	private ActionBarDrawerToggle mDrawerToggle;
	private CharSequence mDrawerTitle;
	private CharSequence mTitle;

	//	private StarksoftActivity mActivity;
	private Fragment activeFragment;
	// Одновременно может быть только один ActionMode в пределах одной Activity
	private ActionMode mActionMode;

	/////////////////////////////////////////////////////////////////////////////////////////////////
	// ActionMode related methods
	/////////////////////////////////////////////////////////////////////////////////////////////////
	public ActionMode startMyActionMode(Callback mActionModeCallback)
	{
		if (mActionModeCallback == null)
			return mActionMode = null;

		return mActionMode = startSupportActionMode(mActionModeCallback);
	}

	/**
	 * returns true if mode was closed
	 */
	public boolean closeActionMode()
	{
		boolean isModeEnabled = mActionMode != null;
		if (isModeEnabled)
			mActionMode.finish();

		return isModeEnabled;
	}

	public ActionMode getActionMode()
	{
		return mActionMode;
	}

	/**
	 * Узнаем запустил ли юзер ActionMode
	 */
	public boolean isActionModeRunning()
	{
		return getActionMode() != null;
	}
	/////////////////////////////////////////////////////////////////////////////////////////////////
	// DrawerLayout related methods
	/////////////////////////////////////////////////////////////////////////////////////////////////

	public NavigationView getNavigationView()
	{
		return mNavigationView;
	}

	public void disableDrawer(int lockMode)
	{
		if (mDrawerLayout == null || getSupportActionBar() == null)
			return;

		mDrawerLayout.setDrawerLockMode(lockMode, mNavigationView);
		getSupportActionBar().setDisplayHomeAsUpEnabled(false);
	}

	public void enableDrawer()
	{
		if (mDrawerLayout == null || getSupportActionBar() == null)
			return;

		mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED, mNavigationView);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	}

	public boolean closeDrawer()
	{
		int lockMode = mDrawerLayout.getDrawerLockMode(mNavigationView);
		if (lockMode == DrawerLayout.LOCK_MODE_UNLOCKED && isDrawerOpen())
		{
			mDrawerLayout.closeDrawer(mNavigationView);
			return true;
		}
		return false;
	}

	public void toggleDrawer()
	{
		if (mDrawerLayout == null || mNavigationView == null)
			return;

		int lockMode = mDrawerLayout.getDrawerLockMode(mNavigationView);
		if (lockMode == DrawerLayout.LOCK_MODE_UNLOCKED)
		{
			if (mDrawerLayout.isDrawerOpen(mNavigationView))
				mDrawerLayout.closeDrawer(mNavigationView);
			else
				mDrawerLayout.openDrawer(mNavigationView);
		}
	}

	/**
	 * установим адаптер для бокового меню
	 */
	public void setDrawerAdapter(ListAdapter newAdapter)
	{
		if (mNavigationView != null)
		{
			for (int i = 0, count = mNavigationView.getChildCount(); i < count; i++)
			{
				final View child = mNavigationView.getChildAt(i);
				if (child != null && child instanceof ListView)
				{
					final ListView menuView = (ListView) child;
					menuView.setAdapter(newAdapter);
				}
			}
		}
	}

	public ListAdapter getDrawerAdapter()
	{
		for (int i = 0; i < mNavigationView.getChildCount(); i++)
		{
			View child = mNavigationView.getChildAt(i);
			if (child != null && child instanceof ListView)
				return ((ListView) child).getAdapter();
		}
		return null;
	}

	public void setDrawerClickListener(NavigationView.OnNavigationItemSelectedListener listener)
	{
		if (mNavigationView != null)
			mNavigationView.setNavigationItemSelectedListener(listener);
	}

	public void selectDrawerItemAndSetTitle(int resId, String optTitle)
	{
		try
		{
			String title = mNavigationView.getMenu().findItem(resId).getTitle().toString();
			mDrawerLayout.closeDrawer(mNavigationView);
			setTitle(TextUtils.isEmpty(optTitle) ? title : optTitle);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public void setCounterToDrawerItem(int resId, int count)
	{
		MenuItem item = mNavigationView.getMenu().findItem(resId);

		String space = "     ";
		String result = item.getTitle() + space + count;

		SpannableString spannablecontent = new SpannableString(result);

		int countLen = (count + "").length();
		int start = result.length() - countLen;
		spannablecontent.setSpan(new RoundedBackgroundSpan(Color.argb(120, 0, 0, 0), Color.WHITE), start, result.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		item.setTitle(spannablecontent);

//		v.setEnabled(true);

	}

	/**
	 * узнаем открыто ли боковое меню
	 */
	public boolean isDrawerOpen()
	{
		return mDrawerLayout.isDrawerOpen(mNavigationView);
	}

	/////////////////////////////////////////////////////////////////////////////////////////////////
	// FragmentActivity related methods
	/////////////////////////////////////////////////////////////////////////////////////////////////
	@SuppressWarnings("ResourceType")
	public void setActiveFragment(Fragment dest)
	{
		if (getActionMode() != null)
			getActionMode().finish();

		String tag = dest.getClass().getName();

		try
		{
			FragmentManager fragmentManager = getSupportFragmentManager();
			FragmentTransaction mFragmentTransaction = fragmentManager.beginTransaction();
			mFragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
			// mFragmentTransaction.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out);
			mFragmentTransaction.replace(R.id.content_frame, activeFragment = dest, tag).commit();
		}
		// Ловим ошибку, если в фоне меняем фрагмент, падает именно здесь
		// java.lang.IllegalStateException: Can not perform this action after onSaveInstanceState
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public Fragment getActiveFragment()
	{
		return activeFragment;
	}

	/////////////////////////////////////////////////////////////////////////////////////////////////
	// Другие методы
	/////////////////////////////////////////////////////////////////////////////////////////////////
	public boolean isActionModeOrDrawerOpened()
	{
		return isActionModeRunning() || isDrawerOpen();
	}

	public interface DrawerStateCallBack
	{
		void onDrawerOpened(View view);

		void onDrawerClosed(View view);
	}

	private DrawerStateCallBack mDrawerStateCallBack;

	public void registerDrawerCallBack(DrawerStateCallBack newDrawerStateCallBack)
	{
		mDrawerStateCallBack = newDrawerStateCallBack;
	}

	/////////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_activity_frame_new_drawer);

//		mActivity = this;
		mTitle = mDrawerTitle = getTitle();
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mNavigationView = (NavigationView) findViewById(R.id.left_drawer);
		// set a custom shadow that overlays the main content when the drawer opens
		mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
		// enable ActionBar app icon to behave as action to toggle nav drawer
		ActionBar b = getSupportActionBar();
		if (b != null)
		{
			b.setDisplayHomeAsUpEnabled(true);
			b.setHomeButtonEnabled(true);
		}

		// ActionBarDrawerToggle ties together the the proper interactions
		// between the sliding drawer and the action bar app icon
		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.drawer_open, R.string.drawer_close)
		{
			public void onDrawerClosed(View view)
			{
				if (mDrawerStateCallBack != null)
					mDrawerStateCallBack.onDrawerClosed(view);

				getSupportActionBar().setTitle(mTitle);
				invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
			}

			public void onDrawerOpened(View drawerView)
			{
				if (mDrawerStateCallBack != null)
					mDrawerStateCallBack.onDrawerOpened(drawerView);

				if (mActionMode != null)
					mActionMode.finish();

				getSupportActionBar().setTitle(mDrawerTitle);
				invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
			}
		};
		mDrawerLayout.setDrawerListener(mDrawerToggle);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		// The action bar home/up action should open or close the drawer.
		// ActionBarDrawerToggle will take care of this.
		return mDrawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
	}

	@Override
	public void onBackPressed()
	{
		if (mDrawerLayout.isDrawerOpen(GravityCompat.START))
			mDrawerLayout.closeDrawer(GravityCompat.START);

		else if (isActionModeRunning())
			closeActionMode();
		else
			super.onBackPressed();
	}

	@Override
	public boolean onKeyUp(int keyCode, @NonNull KeyEvent event)
	{
		if (keyCode == KeyEvent.KEYCODE_MENU)
		{
			if (event.getAction() == KeyEvent.ACTION_UP)
			{
				toggleDrawer();
				return true;
			}
		}
		return super.onKeyUp(keyCode, event);
	}

	@Override
	public void setTitle(CharSequence title)
	{
		mTitle = title;
		if (getSupportActionBar() != null)
			getSupportActionBar().setTitle(mTitle);
	}

	/**
	 * When using the ActionBarDrawerToggle, you must call it during
	 * onPostCreate() and onConfigurationChanged()...
	 */
	@Override
	protected void onPostCreate(Bundle savedInstanceState)
	{
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		if (mDrawerToggle != null)
			mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig)
	{
		super.onConfigurationChanged(newConfig);
		// Pass any configuration change to the drawer toggls
		if (mDrawerToggle != null)
			mDrawerToggle.onConfigurationChanged(newConfig);
	}

	private class RoundedBackgroundSpan extends ReplacementSpan
	{
		private final int mPadding = 1;
		private int mBackgroundColor;
		private int mTextColor;

		public RoundedBackgroundSpan(int backgroundColor, int textColor)
		{
			super();
			mBackgroundColor = backgroundColor;
			mTextColor = textColor;
		}

		@Override
		public int getSize(Paint paint, CharSequence text, int start, int end, Paint.FontMetricsInt fm)
		{
			return (int) (mPadding + paint.measureText(text.subSequence(start, end).toString()) + mPadding);
		}

		@Override
		public void draw(Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom, Paint paint)
		{
			float width = paint.measureText(text.subSequence(start, end).toString());
			RectF rect = new RectF(x, top + mPadding, x + width + 2 * mPadding, bottom);
			paint.setColor(mBackgroundColor);
			canvas.drawRoundRect(rect, mPadding, mPadding, paint);
			paint.setColor(mTextColor);
			canvas.drawText(text, start, end, x + mPadding, y, paint);
		}
	}
}