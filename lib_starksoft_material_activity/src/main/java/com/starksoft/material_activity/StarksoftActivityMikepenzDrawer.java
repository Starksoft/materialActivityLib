package com.starksoft.material_activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.view.ActionMode.Callback;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;

import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;

public class StarksoftActivityMikepenzDrawer extends AppCompatActivity
{
	private Drawer mDrawer;
//	private ActionBarDrawerToggle mDrawerToggle;

	private CharSequence mDrawerTitle;
	private CharSequence mTitle;

	//	private StarksoftActivity mActivity;
	private Fragment activeFragment;
	// Одновременно может быть только один ActionMode в пределах одной Activity
	ActionMode mActionMode;

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
	boolean closeActionMode()
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

	public Drawer getDrawer()
	{
		return mDrawer;
	}

	public void disableDrawer(int lockMode)
	{
		if (mDrawer == null || getSupportActionBar() == null)
			return;

//		mDrawerLayout.setDrawerLockMode(lockMode, mNavigationView);
		getSupportActionBar().setDisplayHomeAsUpEnabled(false);
	}

	//	public void enableDrawer()
//	{
//		if (mDrawerLayout == null || getSupportActionBar() == null)
//			return;
//
//		mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED, mNavigationView);
//		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//	}
//
//	public boolean closeDrawer()
//	{
//		int lockMode = mDrawerLayout.getDrawerLockMode(mNavigationView);
//		if (lockMode == DrawerLayout.LOCK_MODE_UNLOCKED && isDrawerOpen())
//		{
//			mDrawerLayout.closeDrawer(mNavigationView);
//			return true;
//		}
//		return false;
//	}
//
	public void toggleDrawer()
	{
		if (mDrawer.isDrawerOpen())
			mDrawer.closeDrawer();
		else
			mDrawer.openDrawer();
	}

	/**
	 * установим адаптер для бокового меню
	 //	 */
//	public void setDrawerAdapter(ListAdapter newAdapter)
//	{
//		if (mNavigationView != null)
//		{
//			for (int i = 0, count = mNavigationView.getChildCount(); i < count; i++)
//			{
//				final View child = mNavigationView.getChildAt(i);
//				if (child != null && child instanceof ListView)
//				{
//					final ListView menuView = (ListView) child;
//					menuView.setAdapter(newAdapter);
//				}
//			}
//		}
//
//
//	}
//
//	public ListAdapter getDrawerAdapter()
//	{
//		for (int i = 0; i < mNavigationView.getChildCount(); i++)
//		{
//			View child = mNavigationView.getChildAt(i);
//			if (child != null && child instanceof ListView)
//				return ((ListView) child).getAdapter();
//		}
//		return null;
//	}

	public void setDrawerClickListener(Drawer.OnDrawerItemClickListener listener)
	{
		if (mDrawer != null)
			mDrawer.setOnDrawerItemClickListener(listener);
	}

	public void selectDrawerItemAndSetTitle(int id, String optTitle)
	{
		if (mDrawer != null)
		{
			mDrawer.setSelection(id, false);
			mDrawer.closeDrawer();
		}
	}

	/**
	 * узнаем открыто ли боковое меню
	 */
	public boolean isDrawerOpen()
	{
		return mDrawer.isDrawerOpen();
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
		setContentView(R.layout.main_activity_frame_mikepenz_drawer);

		// Handle Toolbar
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		mDrawer = new DrawerBuilder().withToolbar(toolbar).withHasStableIds(true).withActivity(this).build();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		// The action bar home/up action should open or close the drawer.
		// ActionBarDrawerToggle will take care of this.
		return super.onOptionsItemSelected(item);
	}

//	@Override
//	public boolean onOptionsItemSelected(final MenuItem item) {
//		if (item.getItemId() == android.support.v7.appcompat.R.id.home) {
//			return mDrawerToggle.onOptionsItemSelected(item);
//		}
//		return super.onOptionsItemSelected(item);
//	}

	@Override
	public void onBackPressed()
	{
		//handle the back press :D close the drawer first and if the drawer is closed close the activity
		if (mDrawer != null && mDrawer.isDrawerOpen())
		{
			mDrawer.closeDrawer();
		}
		else
		{
			super.onBackPressed();
		}
	}

//		try
//		{
//			// Закрываем ActionMode
//			boolean actionModeClosed = closeActionMode();
//			// Закрываем меню
//			boolean drawerClosed = closeDrawer();
//
//			if (allowActivityFinish)
//				finish();
//		}
//		catch (Exception e)
//		{
//			e.printStackTrace();
//		}


	@Override
	public boolean onKeyUp(int keyCode, @NonNull KeyEvent event)
	{
		if (keyCode == KeyEvent.KEYCODE_MENU)
		{
			if (event.getAction() == KeyEvent.ACTION_UP)
			{
				try
				{
//					toggleDrawer();
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
				return true;
			}
		}
		return super.onKeyUp(keyCode, event);
	}

	@Override
	public void setTitle(CharSequence title)
	{
		mTitle = title;
		getSupportActionBar().setTitle(mTitle);
	}

}