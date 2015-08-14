package com.starksoft.materiallibtest;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;

import com.starksoft.material_activity.DrawerItemModel;
import com.starksoft.material_activity.DrawerItemModelAdapter;
import com.starksoft.material_activity.StarksoftActivity;

import java.util.ArrayList;

public class MainActivity extends StarksoftActivity implements AdapterView.OnItemClickListener
{
	int currentDrawerItem;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
//		setContentView(R.layout.activity_main);

		setDrawerAdapter(new DrawerItemModelAdapter(this, generateData()));
		setDrawerClickListener(this);

		if (savedInstanceState == null)
		{
			selectItem(0, false);
		}
	}

	// Менюшка
	private ArrayList<DrawerItemModel> generateData()
	{
		ArrayList<DrawerItemModel> models = new ArrayList<>();

		models.add(new DrawerItemModel(0, "RecyclerListFragment", null));
		models.add(new DrawerItemModel(0, "Undef", null));

		return models;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id)
	{
		selectItem(position, true);
	}

	public void selectItem(int position, boolean useDelay)
	{
		Fragment fragment = null;

		switch (currentDrawerItem = position)
		{
			case 0:
				fragment = new MainActivityFragment();
				break;

			case 1:
				return;

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
		selectDrawerItemAndSetTitle(position, null);
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
