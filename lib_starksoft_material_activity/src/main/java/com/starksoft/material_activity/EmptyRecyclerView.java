package com.starksoft.material_activity;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

public class EmptyRecyclerView extends RecyclerView {
	private View emptyView;
	final private AdapterDataObserver observer = new AdapterDataObserver() {
		@Override
		public void onChanged() {
			checkIfEmpty();
		}

		@Override
		public void onItemRangeInserted(int positionStart, int itemCount) {
			checkIfEmpty();
		}

		@Override
		public void onItemRangeRemoved(int positionStart, int itemCount) {
			checkIfEmpty();
		}
	};

	public EmptyRecyclerView(Context context) {
		super(context);
	}

	public EmptyRecyclerView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public EmptyRecyclerView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	void checkIfEmpty() {
		if (emptyView != null) {
			final boolean emptyViewVisible = getAdapter() == null || getAdapter().getItemCount() == 0;
			emptyView.setVisibility(emptyViewVisible ? VISIBLE : GONE);
			setVisibility(emptyViewVisible ? GONE : VISIBLE);
		}
	}

	@Override
	public void setAdapter(Adapter adapter) {
		final Adapter oldAdapter = getAdapter();
		if (oldAdapter != null) {
			oldAdapter.unregisterAdapterDataObserver(observer);
		}

		super.setAdapter(adapter);
		if (adapter != null) {
			adapter.registerAdapterDataObserver(observer);
		}

		checkIfEmpty();
	}

	@Override
	public boolean canScrollVertically(int direction) {
		// check if scrolling up
		if (direction < 1) {
			boolean original = super.canScrollVertically(direction);
			return !original && getChildAt(0) != null && getChildAt(0).getTop() < 0 || original;
		}
		return super.canScrollVertically(direction);

	}

	public void setEmptyView(View emptyView) {
		this.emptyView = emptyView;
		checkIfEmpty();
	}
	/* Fix of NullPointerException
	*  http://stackoverflow.com/questions/26702633/why-am-i-getting-a-null-reference-on-my-recyclerview/26908738#26908738
	*  https://code.google.com/p/android/issues/detail?id=79244
	*
	*  this happens when setLayoutManager isn`t called
	* */

	@Override
	public void stopScroll() {
		try {
			super.stopScroll();
		} catch (NullPointerException exception) {
			/**
			 *  The mLayout has been disposed of before the
			 *  RecyclerView and this stops the application
			 *  from crashing.
			 */
		}
	}
}