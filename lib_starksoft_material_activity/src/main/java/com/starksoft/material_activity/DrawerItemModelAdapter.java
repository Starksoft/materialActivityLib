package com.starksoft.material_activity;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class DrawerItemModelAdapter extends ArrayAdapter<DrawerItemModel> {
	private final Context context;
	public final ArrayList<DrawerItemModel> modelsArrayList;

	public DrawerItemModelAdapter(Context context, ArrayList<DrawerItemModel> modelsArrayList) {
		super(context, R.layout.drawer_target_item, modelsArrayList);

		this.context = context;
		this.modelsArrayList = modelsArrayList;
	}

	static class ViewHolder {
		ImageView iconImageView;

		TextView titleTextView;
		TextView headerTitleTextView;
		TextView counterTextView;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		// ListView mListView = (ListView) parent;

		boolean isHeader = modelsArrayList.get(position).isGroupHeader();

		if (convertView == null) {
			if (isHeader) {
				convertView = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.drawer_group_header_item, parent, false);
			} else {
				convertView = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.drawer_target_item, parent, false);
			}

			holder = new ViewHolder();

			holder.iconImageView = (ImageView) convertView.findViewById(R.id.item_icon);
			holder.titleTextView = (TextView) convertView.findViewById(R.id.item_title);
			holder.counterTextView = (TextView) convertView.findViewById(R.id.item_counter);
			holder.headerTitleTextView = (TextView) convertView.findViewById(R.id.header);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		// Это заголовок
		if (isHeader) {
			// если это заголовок - надо отключить нажатие на него
			convertView.setEnabled(false);
			convertView.setOnClickListener(null);

			holder.headerTitleTextView.setText(modelsArrayList.get(position).getTitle());
		}
		// Это обычный элемент меню
		else {
			convertView.setEnabled(true);

			int icon = modelsArrayList.get(position).getIcon();
			// Если иконку нам не передали, убираем вьюшку
			if (icon <= 0) holder.iconImageView.setVisibility(View.GONE);
			else {
				holder.iconImageView.setVisibility(View.VISIBLE);
				holder.iconImageView.setImageResource(modelsArrayList.get(position).getIcon());
			}

			holder.titleTextView.setText(modelsArrayList.get(position).getTitle());

			String itemCounter = modelsArrayList.get(position).getCounter();
			// Если каунтер пустой - скрываем вью
			if (TextUtils.isEmpty(itemCounter) || TextUtils.equals(itemCounter, "0")) {
				holder.counterTextView.setVisibility(View.GONE);
			} else {
				holder.counterTextView.setVisibility(View.VISIBLE);
				holder.counterTextView.setText(itemCounter);
			}
		}
		return convertView;
	}
}
