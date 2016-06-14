package com.starksoft.materiallibtest;

import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.starksoft.material_activity.StarksoftActivityNewDrawer;
import com.starksoft.material_activity.StarksoftRecyclerListFragment;

public class MainActivityFragment extends StarksoftRecyclerListFragment implements SwipeRefreshLayout.OnRefreshListener {
	private Handler mHandler;
	private boolean isEmpty = false;
	/* If false - disables list progressbar when refreshing*/
	static final boolean REFRESH_LIST = false;

	public MainActivityFragment() {
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		// Инициализация кнопки нужна здесь
		setFabEnabled(true);
		super.onViewCreated(view, savedInstanceState);

		LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
		getRecyclerListView().setLayoutManager(linearLayoutManager);
		getSwipeRefreshLayout().setOnRefreshListener(this);

		Bundle b = getArguments();
		isEmpty = b != null && b.getBoolean("empty");

		mHandler = new Handler();
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				setEmptyText("No data!");

				if (isEmpty) {
					setListAdapter(null);
				} else {
					loadAdapter(500);
				}
			}
		}, 1000);

		getFloatingActionButton().setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Snackbar.make(v, "TEST!", Snackbar.LENGTH_LONG).setAction("ACTION!", new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						Toast.makeText(getActivity(), "Fired action!", Toast.LENGTH_LONG).show();
					}
				}).show();
			}
		});
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		if (mHandler != null) {
			mHandler.removeCallbacksAndMessages(null);
		}
	}

	private void loadAdapter(int count) {
		String items = "";
		for (int i = 0; i < count; i++)
			items += "Item  " + (i + 1) + ";";

		setListAdapter(new MyAdapter(items.split(";")));
		setHintText("Items: " + getRecyclerViewListAdapter().getItemCount());
		((StarksoftActivityNewDrawer) getActivity()).setNavigationViewCounter(R.id.filledList, getRecyclerViewListAdapter().getItemCount());
	}

	@Override
	public void onRefresh() {
		// Это защищает от ложных срабатываний, при отстутствии адаптера
		if (!getSwipeRefreshLayout().isEnabled()){
			return;
		}

		if (REFRESH_LIST) {
			setListShown(false);
		}

		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				loadAdapter(getRecyclerViewListAdapter().getItemCount() + 1);
				getSwipeRefreshLayout().setRefreshing(false);
				if (REFRESH_LIST) {
					setListShown(true);
				}
			}
		}, 2000);
	}

	class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
		private String[] mDataset;

		public class ViewHolder extends RecyclerView.ViewHolder {
			public TextView mTextView;

			public ViewHolder(TextView v) {
				super(v);
				mTextView = v;
			}
		}

		public MyAdapter(String[] myDataset) {
			mDataset = myDataset;
		}

		@Override
		public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
			TextView t = new TextView(getActivity());
			t.setTextSize(20);
			return new ViewHolder(t);
		}

		@Override
		public void onBindViewHolder(ViewHolder holder, int position) {
			holder.mTextView.setText(mDataset[position]);
		}

		@Override
		public int getItemCount() {
			return mDataset.length;
		}
	}
}