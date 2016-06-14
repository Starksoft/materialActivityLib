package com.starksoft.material_activity;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;

public class StarksoftActivity extends AppCompatActivity {
	DrawerLayout mDrawerLayout;
	ListView mDrawerList;
	private ActionBarDrawerToggle mDrawerToggle;

	// флаг по которому определяем нужно ли завершать активити
	private boolean allowActivityFinish = true;

	//поддерживаются адаптеры- наследники ListAdapter
	private ListAdapter mListAdapter;
	private CharSequence mDrawerTitle;
	private CharSequence mTitle;

	//	private StarksoftActivity mActivity;
	private Fragment activeFragment;
	// Одновременно может быть только один ActionMode в пределах одной Activity
	ActionMode mActionMode;

	/////////////////////////////////////////////////////////////////////////////////////////////////
	// ActionMode related methods
	/////////////////////////////////////////////////////////////////////////////////////////////////
	public ActionMode startMyActionMode(Callback mActionModeCallback) {
		if (mActionModeCallback == null) return mActionMode = null;

		return mActionMode = startSupportActionMode(mActionModeCallback);
	}

	/**
	 * returns true if mode was closed
	 */
	boolean closeActionMode() {
		boolean isModeEnabled = mActionMode != null;
		if (isModeEnabled) mActionMode.finish();

		return isModeEnabled;
	}

	public ActionMode getActionMode() {
		return mActionMode;
	}

	/**
	 * Узнаем запустил ли юзер ActionMode
	 */
	public boolean isActionModeRunning() {
		return getActionMode() != null;
	}
	/////////////////////////////////////////////////////////////////////////////////////////////////
	// DrawerLayout related methods
	/////////////////////////////////////////////////////////////////////////////////////////////////

	public ListView getDrawerListView() {
		return mDrawerList;
	}

	public void disableDrawer(int lockMode) {
		if (mDrawerLayout == null || getSupportActionBar() == null) return;

		mDrawerLayout.setDrawerLockMode(lockMode, mDrawerList);
		getSupportActionBar().setDisplayHomeAsUpEnabled(false);

	}

	public void enableDrawer() {
		if (mDrawerLayout == null || getSupportActionBar() == null) return;

		mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED, mDrawerList);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	}


	public boolean closeDrawer() {
		int lockMode = mDrawerLayout.getDrawerLockMode(mDrawerList);
		if (lockMode == DrawerLayout.LOCK_MODE_UNLOCKED && isDrawerOpen()) {
			mDrawerLayout.closeDrawer(mDrawerList);
			return true;
		}
		return false;
	}

	public void toggleDrawer() {
		int lockMode = mDrawerLayout.getDrawerLockMode(mDrawerList);
		if (lockMode == DrawerLayout.LOCK_MODE_UNLOCKED) {
			if (mDrawerLayout.isDrawerOpen(mDrawerList)) mDrawerLayout.closeDrawer(mDrawerList);
			else mDrawerLayout.openDrawer(mDrawerList);
		}
	}

	/**
	 * установим адаптер для бокового меню
	 */
	public void setDrawerAdapter(ListAdapter adapter) {
		allowActivityFinish = false;

		if (mDrawerList != null) mDrawerList.setAdapter(mListAdapter = adapter);
	}

	public ListAdapter getDrawerAdapter() {
		return mListAdapter;
	}

	public void setDrawerClickListener(ListView.OnItemClickListener listener) {
		allowActivityFinish = false;

		if (mDrawerList != null) mDrawerList.setOnItemClickListener(listener);
	}

	public void selectDrawerItemAndSetTitle(int position, String optTitle) {
		try {
			mDrawerList.setItemChecked(position, true);
			mDrawerLayout.closeDrawer(mDrawerList);

			if (TextUtils.isEmpty(optTitle)) {
				// Это должно падать, все нормально
				DrawerItemModel model = ((DrawerItemModelAdapter) mListAdapter).getItem(position);
				setTitle(model.getTitle());
			} else setTitle(optTitle);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * узнаем открыто ли боковое меню
	 */
	public boolean isDrawerOpen() {
		return mDrawerLayout.isDrawerOpen(mDrawerList);
	}

	/////////////////////////////////////////////////////////////////////////////////////////////////
	// FragmentActivity related methods
	/////////////////////////////////////////////////////////////////////////////////////////////////
	public void setActiveFragment(Fragment dest) {
		if (getActionMode() != null) getActionMode().finish();

		String tag = dest.getClass().getName();

		try {
			FragmentManager fragmentManager = getSupportFragmentManager();
			FragmentTransaction mFragmentTransaction = fragmentManager.beginTransaction();
			mFragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
			// mFragmentTransaction.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out);
			mFragmentTransaction.replace(R.id.content_frame, activeFragment = dest, tag).commit();
		}
		// Ловим ошибку, если в фоне меняем фрагмент, падает именно здесь
		// java.lang.IllegalStateException: Can not perform this action after onSaveInstanceState
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Fragment getActiveFragment() {
		return activeFragment;
	}

	/////////////////////////////////////////////////////////////////////////////////////////////////
	// Другие методы
	/////////////////////////////////////////////////////////////////////////////////////////////////
	public boolean isActionModeOrDrawerOpened() {
		return isActionModeRunning() || isDrawerOpen();
	}

	public interface DrawerStateCallBack {
		void onDrawerOpened(View view);

		void onDrawerClosed(View view);
	}

	private DrawerStateCallBack mDrawerStateCallBack;

	public void registerDrawerCallBack(DrawerStateCallBack newDrawerStateCallBack) {
		mDrawerStateCallBack = newDrawerStateCallBack;
	}

	/////////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_activity_frame);

		//		mActivity = this;
		mTitle = mDrawerTitle = getTitle();
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerList = (ListView) findViewById(R.id.left_drawer);
		// set a custom shadow that overlays the main content when the drawer opens
		mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
		// enable ActionBar app icon to behave as action to toggle nav drawer
		ActionBar b = getSupportActionBar();
		if (b != null) {
			b.setDisplayHomeAsUpEnabled(true);
			b.setHomeButtonEnabled(true);
		}

		// ActionBarDrawerToggle ties together the the proper interactions
		// between the sliding drawer and the action bar app icon
		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.drawer_open, R.string.drawer_close) {
			public void onDrawerClosed(View view) {
				if (mDrawerStateCallBack != null) mDrawerStateCallBack.onDrawerClosed(view);

				getSupportActionBar().setTitle(mTitle);
				invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
			}

			public void onDrawerOpened(View drawerView) {
				if (mDrawerStateCallBack != null) mDrawerStateCallBack.onDrawerOpened(drawerView);

				if (mActionMode != null) mActionMode.finish();

				getSupportActionBar().setTitle(mDrawerTitle);
				invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
			}
		};
		mDrawerLayout.setDrawerListener(mDrawerToggle);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// The action bar home/up action should open or close the drawer.
		// ActionBarDrawerToggle will take care of this.
		return mDrawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
	}

	@Override
	public void onBackPressed() {
		try {
			// Закрываем ActionMode
			boolean actionModeClosed = closeActionMode();
			// Закрываем меню
			boolean drawerClosed = closeDrawer();

			if (allowActivityFinish) finish();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean onKeyUp(int keyCode, @NonNull KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_MENU) {
			if (event.getAction() == KeyEvent.ACTION_UP) {
				try {
					toggleDrawer();
				} catch (Exception e) {
					e.printStackTrace();
				}
				return true;
			}
		}
		return super.onKeyUp(keyCode, event);
	}

	@Override
	public void setTitle(CharSequence title) {
		mTitle = title;
		getSupportActionBar().setTitle(mTitle);
	}

	/**
	 * When using the ActionBarDrawerToggle, you must call it during
	 * onPostCreate() and onConfigurationChanged()...
	 */
	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		if (mDrawerToggle != null) mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		// Pass any configuration change to the drawer toggls
		if (mDrawerToggle != null) mDrawerToggle.onConfigurationChanged(newConfig);
	}
}