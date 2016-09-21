package com.starksoft.materiallibtest;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.IdRes;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.view.Menu;
import android.view.MenuItem;

import com.starksoft.material_activity.StarksoftActivityNewDrawer;

import static com.starksoft.materiallibtest.MainActivityFragment.Options.EMPTY_LIST;
import static com.starksoft.materiallibtest.MainActivityFragment.Options.ERROR;
import static com.starksoft.materiallibtest.MainActivityFragment.Options.FILLED_LIST;

public class MainActivity extends StarksoftActivityNewDrawer implements NavigationView.OnNavigationItemSelectedListener {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		getNavigationView().inflateMenu(R.menu.drawer);
		setDrawerClickListener(this);

		if (savedInstanceState == null) {
			selectItem(R.id.filledList, 500, false);
		}
	}

	@Override
	public boolean onNavigationItemSelected(final MenuItem menuItem) {
		// Странный костыль, иначе не работает
//		menuItem.setChecked(true);
		return selectItem(menuItem.getItemId(), 500, true);
	}

	public boolean selectItem(@IdRes int resId, int counter, boolean useDelay) {
		Fragment fragment = null;

		switch (resId) {
			case R.id.filledList:
				fragment = MainActivityFragment.newInstance(FILLED_LIST);
				break;

			//			case R.id.tabsFragment:
			//				fragment = new MainActivityTabsFragment();
			//				break;

			case R.id.expandableList:
				fragment = new MainActivityExpandableListFragment();
				break;

			case R.id.errorLayoutDemo:
				fragment = MainActivityFragment.newInstance(ERROR);
				break;

			case R.id.emptyList:
				fragment = MainActivityFragment.newInstance(EMPTY_LIST);
				break;
		}
		if (fragment != null) {
			// setActiveFragment(fragment);
			final Fragment f = fragment;
			// Убираем лаг при переключении
			if (useDelay) {
				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						setActiveFragment(f, true, false);
					}
				}, 300);
			} else setActiveFragment(f, true, false);
		}
		selectDrawerItemAndSetTitle(resId, counter, null);
		return fragment != null;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		//noinspection SimplifiableIfStatement
		if (id == R.id.action_settings) {
			return true;
		}

		return super.onOptionsItemSelected(item);
	}
}
