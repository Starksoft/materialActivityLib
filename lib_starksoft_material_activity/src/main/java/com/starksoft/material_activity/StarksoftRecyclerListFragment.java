package com.starksoft.material_activity;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.ColorRes;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

public class StarksoftRecyclerListFragment extends Fragment
{
	static final int INTERNAL_EMPTY_ID = R.id.emptyTextView;
	static final int INTERNAL_PROGRESS_CONTAINER_ID = R.id.progressBar;
	static final int INTERNAL_LIST_CONTAINER_ID = R.id.swipeRefreshLayout;
	static final int INTERNAL_HINT_CONTAINER_ID = R.id.hintTextView;
	static final int INTERNAL_FAB_CONTAINER_ID = R.id.floatingActionButton;

	private boolean isFabEnabled = false;
//	private boolean isSwipeRefreshLayoutEnabled = true;

	final private Handler mHandler = new Handler();
	final private Runnable mRequestFocus = new Runnable()
	{
		public void run()
		{
			mList.focusableViewAvailable(mList);
		}
	};

	EmptyRecyclerView.Adapter<?> mAdapter;
	EmptyRecyclerView mList;

	TextView mStandardEmptyView;
	TextView mStandardHintView;
	View mProgressContainer;
	View mListContainer;
	CharSequence mEmptyText;
	boolean mListShown;
	SwipeRefreshLayout mSwipeRefreshLayout;
	FloatingActionButton mFloatingActionButton;

	public StarksoftRecyclerListFragment()
	{}

	public SwipeRefreshLayout getSwipeRefreshLayout()
	{
		return mSwipeRefreshLayout;
	}

	public void setSwipeRefreshLayoutOnRefreshListener(SwipeRefreshLayout.OnRefreshListener listener)
	{
		if (mSwipeRefreshLayout == null)
			throw new RuntimeException("Can`t use this with custom content view");

		mSwipeRefreshLayout.setOnRefreshListener(listener);
	}

//	public void setSwipeRefreshLayoutEnabled(boolean state)
//	{
//		isSwipeRefreshLayoutEnabled = state;
//	}

	public void setFabEnabled(boolean state)
	{
		isFabEnabled = state;
		ensureList();
	}

	public FloatingActionButton getFloatingActionButton()
	{
		return mFloatingActionButton;
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		return inflater.inflate(R.layout.list_fragment_layout, container, false);
	}

	private int getColor(@ColorRes int id)
	{
		return getResources().getColor(id);
	}

	/**
	 * Attach to list view once the view hierarchy has been created.
	 */
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState)
	{
		super.onViewCreated(view, savedInstanceState);
		ensureList();
	}

	/**
	 * Detach from list view.
	 */
	@Override
	public void onDestroyView()
	{
		mHandler.removeCallbacks(mRequestFocus);
		mList = null;
		mListShown = false;
		mProgressContainer = mListContainer = mFloatingActionButton = null;
		mStandardEmptyView = mStandardHintView = null;
		super.onDestroyView();
	}

	/**
	 * Provide the adapter for the recycler view.
	 */
	public void setListAdapter(RecyclerView.Adapter<?> adapter)
	{
		// If LayoutManager is missing set to default
		if (mList.getLayoutManager() == null)
		{
			throw new IllegalStateException("LayoutManager is not set in RecyclerView!");
		}
		boolean hadAdapter = mAdapter != null;
		mAdapter = adapter;
		if (mList != null)
		{
			mList.setAdapter(adapter);
			if (!mListShown && !hadAdapter)
			{
				// The list was hidden, and previously didn't have an
				// adapter. It is now time to show it.
				setListShown(true, getView().getWindowToken() != null);
			}
		}
	}

	/**
	 * Get the activity's list view widget.
	 */
	public RecyclerView getRecyclerListView()
	{
		ensureList();
		return mList;
	}

	/**
	 * The default content for a ListFragment has a TextView that can be shown
	 * when the list is empty. If you would like to have it shown, call this
	 * method to supply the text it should use.
	 */
	public void setEmptyText(CharSequence text)
	{
		ensureList();
		if (mStandardEmptyView == null)
		{
			throw new IllegalStateException("Can't be used with a custom content view");
		}
		mStandardEmptyView.setText(text);
		if (mEmptyText == null)
		{
			mList.setEmptyView(mStandardEmptyView);
		}
		mEmptyText = text;
	}

	public void setHintText(CharSequence text)
	{
		ensureList();

		if (mStandardHintView == null)
		{
			throw new IllegalStateException("Can't be used with a custom content view");
		}
		mStandardHintView.setText(text);
		mStandardHintView.setVisibility(View.VISIBLE);
		mStandardHintView.startAnimation(AnimationUtils.loadAnimation(getActivity(), android.R.anim.fade_in));
	}

	public void removeHint()
	{
//		if (!TextUtils.isEmpty(mEmptyText))
//			throw new RuntimeException("Can`t show hint when showing empty view!");

		mStandardHintView.startAnimation(AnimationUtils.loadAnimation(getActivity(), android.R.anim.fade_out));
		mStandardHintView.setVisibility(View.GONE);
	}

	/**
	 * Control whether the list is being displayed. You can make it not
	 * displayed if you are waiting for the initial data to show in it. During
	 * this time an indeterminant progress indicator will be shown instead.
	 * <p/>
	 * <p/>
	 * Applications do not normally need to use this themselves. The default
	 * behavior of ListFragment is to start with the list not being shown, only
	 * showing it once an adapter is given with
	 * {@link #setListAdapter(RecyclerView.Adapter)}. If the list at that point had not
	 * been shown, when it does get shown it will be do without the user ever
	 * seeing the hidden state.
	 *
	 * @param shown If true, the list view is shown; if false, the progress
	 *              indicator. The initial value is true.
	 */
	public void setListShown(boolean shown)
	{
		setListShown(shown, true);
	}

	/**
	 * Like {@link #setListShown(boolean)}, but no animation is used when
	 * transitioning from the previous state.
	 */
	public void setListShownNoAnimation(boolean shown)
	{
		setListShown(shown, false);
	}

	/**
	 * Control whether the list is being displayed. You can make it not
	 * displayed if you are waiting for the initial data to show in it. During
	 * this time an indeterminant progress indicator will be shown instead.
	 *
	 * @param shown   If true, the list view is shown; if false, the progress
	 *                indicator. The initial value is true.
	 * @param animate If true, an animation will be used to transition to the new
	 *                state.
	 */
	private void setListShown(boolean shown, boolean animate)
	{
		ensureList();

//		if (TextUtils.isEmpty(mStandardEmptyView.getText().toString()))
//		{
//			throw new IllegalStateException("setEmptyText is never called");
//		}

		if (mProgressContainer == null)
		{
			throw new IllegalStateException("Can't be used with a custom content view");
		}
		if (mListShown == shown)
		{
			return;
		}
		mListShown = shown;

//		mSwipeRefreshLayout.setEnabled(shown);
//		mSwipeRefreshLayout.setClickable(shown);
//		mSwipeRefreshLayout.setFocusable(shown);
		if (shown)
		{
			if (animate)
			{
				mProgressContainer.startAnimation(AnimationUtils.loadAnimation(getActivity(), android.R.anim.fade_out));
				mListContainer.startAnimation(AnimationUtils.loadAnimation(getActivity(), android.R.anim.fade_in));

				if (isFabEnabled)
					mFloatingActionButton.startAnimation(AnimationUtils.loadAnimation(getActivity(), android.R.anim.fade_in));

			}
			else
			{
				mProgressContainer.clearAnimation();
				mListContainer.clearAnimation();

				if (isFabEnabled)
					mFloatingActionButton.clearAnimation();
			}
			mProgressContainer.setVisibility(View.GONE);
			mListContainer.setVisibility(View.VISIBLE);

			mFloatingActionButton.setVisibility(isFabEnabled ? View.VISIBLE : View.GONE);
		}
		else
		{
			if (animate)
			{
				mProgressContainer.startAnimation(AnimationUtils.loadAnimation(getActivity(), android.R.anim.fade_in));
				mListContainer.startAnimation(AnimationUtils.loadAnimation(getActivity(), android.R.anim.fade_out));

				if (isFabEnabled)
					mFloatingActionButton.startAnimation(AnimationUtils.loadAnimation(getActivity(), android.R.anim.fade_out));
			}
			else
			{
				mProgressContainer.clearAnimation();
				mListContainer.clearAnimation();

				if (isFabEnabled)
					mFloatingActionButton.clearAnimation();
			}
			mProgressContainer.setVisibility(View.VISIBLE);
			mListContainer.setVisibility(View.GONE);

			mFloatingActionButton.setVisibility(isFabEnabled ? View.VISIBLE : View.GONE);
		}
	}

	/**
	 * Get the RecyclerView.Adapter associated with this activity's RecyclerView.
	 */
	public RecyclerView.Adapter<?> getRecyclerViewListAdapter()
	{
		return mAdapter;
	}

	private void ensureList()
	{
		if (mList != null)
		{
			return;
		}
		View root = getView();
		if (root == null)
		{
			throw new IllegalStateException("Content view not yet created");
		}
		if (root instanceof EmptyRecyclerView)
		{
			mList = (EmptyRecyclerView) root;
		}
		else
		{
			mStandardEmptyView = (TextView) root.findViewById(INTERNAL_EMPTY_ID);
			mStandardEmptyView.setVisibility(View.GONE);

			mStandardHintView = (TextView) root.findViewById(INTERNAL_HINT_CONTAINER_ID);
			mProgressContainer = root.findViewById(INTERNAL_PROGRESS_CONTAINER_ID);

			mFloatingActionButton = (FloatingActionButton) root.findViewById(INTERNAL_FAB_CONTAINER_ID);
			mListContainer = root.findViewById(INTERNAL_LIST_CONTAINER_ID);
			mSwipeRefreshLayout = (SwipeRefreshLayout) mListContainer;
			if (mSwipeRefreshLayout != null)
			{
//				mSwipeRefreshLayout.setEnabled(isSwipeRefreshLayoutEnabled);
				// Override this resources to customize SwipeRefreshLayout`s colors
				mSwipeRefreshLayout.setColorSchemeResources(R.color.swipe_color_1, R.color.swipe_color_2, R.color.swipe_color_3, R.color.swipe_color_4);
			}

			View rawListView = root.findViewById(android.R.id.list);
			if (!(rawListView instanceof EmptyRecyclerView))
			{
				if (rawListView == null)
					throw new RuntimeException("Your content must have a EmptyRecyclerView whose id attribute is " + "'android.R.id.list'");

				throw new RuntimeException("Content has view with id attribute 'android.R.id.list' " + "that is not a EmptyRecyclerView class");
			}
			mList = (EmptyRecyclerView) rawListView;

			mFloatingActionButton.setVisibility(isFabEnabled ? View.VISIBLE : View.GONE);
			// Нужно для класса поведения кнопки, чтобы она не появлялась, когда заперщена
			mFloatingActionButton.setEnabled(isFabEnabled);

			if (mEmptyText != null)
			{
				mStandardEmptyView.setText(mEmptyText);
			}
		}
		mListShown = true;

		if (mAdapter != null)
		{
			RecyclerView.Adapter<?> adapter = mAdapter;
			mAdapter = null;
			setListAdapter(adapter);
		}
		else
		{
			// We are starting without an adapter, so assume we won't
			// have our data right away and start with the progress indicator.
			if (mProgressContainer != null)
				setListShown(false, false);
		}
		mHandler.post(mRequestFocus);
	}

}
