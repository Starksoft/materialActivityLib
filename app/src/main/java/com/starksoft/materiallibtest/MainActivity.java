package com.starksoft.materiallibtest;

import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.view.Menu;
import android.view.MenuItem;

import com.starksoft.material_activity.DrawerItemModel;
import com.starksoft.material_activity.StarksoftActivityNewDrawer;

import java.util.ArrayList;

public class MainActivity extends StarksoftActivityNewDrawer implements NavigationView.OnNavigationItemSelectedListener
{
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
//		setContentView(R.layout.activity_main);

//		setDrawerAdapter(new DrawerItemModelAdapter(this, generateData()));
//		setDrawerClickListener(this);

		getNavigationView().setNavigationItemSelectedListener(this);
		getNavigationView().inflateMenu(R.menu.drawer);

		if (savedInstanceState == null)
		{
			selectItem(R.id.filledList, false);
		}
	}

	@Override
	public boolean onNavigationItemSelected(final MenuItem menuItem)
	{
		return selectItem(menuItem.getItemId(), true);
	}

	// Менюшка
	private ArrayList<DrawerItemModel> generateData()
	{
		ArrayList<DrawerItemModel> models = new ArrayList<>();

		models.add(new DrawerItemModel(0, "RecyclerListFragment", "1"));
		models.add(new DrawerItemModel(0, "Empty RecyclerListFragment", "7"));
		models.add(new DrawerItemModel(0, "Undef", "3"));

		return models;
	}

	public boolean selectItem(int resId, boolean useDelay)
	{
		Fragment fragment = null;

		switch (resId)
		{
			case R.id.filledList:
				fragment = new MainActivityFragment();
				break;

			case R.id.emptyList:
				fragment = new MainActivityFragment();
				Bundle b = new Bundle();
				b.putBoolean("empty", true);
				fragment.setArguments(b);
				break;
		}
		if (fragment != null)
		{
			// setActiveFragment(fragment);
			final Fragment f = fragment;
			// Убираем лаг при переключении
			if (useDelay)
			{
				new Handler().postDelayed(new Runnable()
				{
					@Override
					public void run()
					{
						setActiveFragment(f);
					}
				}, 300);
			}
			else
				setActiveFragment(f);
		}
		selectDrawerItemAndSetTitle(resId, null);

		return fragment != null;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		//noinspection SimplifiableIfStatement
		if (id == R.id.action_settings)
		{
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

}
