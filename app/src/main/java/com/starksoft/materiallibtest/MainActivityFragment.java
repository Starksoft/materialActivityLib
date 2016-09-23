package com.starksoft.materiallibtest;

import android.graphics.drawable.Drawable;
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

import com.starksoft.commons.BaseActivityNewDrawer;
import com.starksoft.commons.fragments.BaseRecyclerViewListFragment;
import com.starksoft.commons.helpers.ErrorLayoutManager;
import com.starksoft.commons.views.ErrorLayoutContainer;

import java.io.Serializable;

public class MainActivityFragment extends BaseRecyclerViewListFragment implements SwipeRefreshLayout.OnRefreshListener, ErrorLayoutContainer.ErrorLayoutCallback {
	private Handler mHandler;
	//	private boolean isEmpty = false;
	public static final String EXTRA_OPTIONS = "options";
	/* If false - disables list progressbar when refreshing*/
	static final boolean REFRESH_LIST = false;
	private Options currentOptions;
	private ErrorLayoutManager errorLayoutManager;

	class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
		private String[] mDataset;

		class ViewHolder extends RecyclerView.ViewHolder {
			TextView mTextView;

			ViewHolder(TextView v) {
				super(v);
				mTextView = v;
			}
		}

		MyAdapter(String[] myDataset) {
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

			Drawable drawable = getResources().getDrawable(R.drawable.fab_src);

			holder.mTextView.setCompoundDrawables(drawable, null, null, null);
		}

		@Override
		public int getItemCount() {
			return mDataset.length;
		}
	}

	enum Options implements Serializable {
		EMPTY_LIST, ERROR, FILLED_LIST
	}

	public static MainActivityFragment newInstance(Options options) {
		MainActivityFragment fragment = new MainActivityFragment();

		Bundle args = new Bundle();
		args.putSerializable(EXTRA_OPTIONS, options);
		fragment.setArguments(args);

		return fragment;
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
		if (b != null) {
			currentOptions = (Options) b.getSerializable(EXTRA_OPTIONS);
		}

		errorLayoutManager = new ErrorLayoutManager(getActivity(), this);

		mHandler = new Handler();
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				setEmptyText("No data!");

				switch (currentOptions) {
					case EMPTY_LIST:
						setListAdapter(null);
						break;

					case ERROR:
						errorLayoutManager.showNetworkErrorLayout(MainActivityFragment.this, true);
						break;

					default:
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
		((BaseActivityNewDrawer) getActivity()).setNavigationViewCounter(R.id.filledList, getRecyclerViewListAdapter().getItemCount());
	}

	@Override
	public void onRefresh() {
		// Это защищает от ложных срабатываний, при отстутствии адаптера
		if (!getSwipeRefreshLayout().isEnabled()) {
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

	@Override
	public void onErrorLayoutMainButtonClick(View view) {
		Snackbar.make(getView(), "onErrorLayoutMainButtonClick!", Snackbar.LENGTH_LONG).show();
	}
}