package jp.senchan.android.wasatter.next.ui.view;

import jp.senchan.android.wasatter.R;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

// just a simple adapter
public class SlideMenuAdapter extends
		ArrayAdapter<MenuDesc> {
	Activity act;
	MenuDesc[] items;

	static class ViewHolder {
		public TextView label;
		public ImageView icon;
	}

	public SlideMenuAdapter(Activity act,
			MenuDesc[] items) {
		super(act, R.id.menu_label, items);
		this.act = act;
		this.items = items;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View rowView = convertView;
		if (rowView == null) {
			LayoutInflater inflater = act.getLayoutInflater();
			rowView = inflater.inflate(R.layout.menu_listitem, null);
			ViewHolder viewHolder = new ViewHolder();
			viewHolder.label = (TextView) rowView
					.findViewById(R.id.menu_label);
			viewHolder.icon = (ImageView) rowView
					.findViewById(R.id.menu_icon);
			rowView.setTag(viewHolder);
		}

		ViewHolder holder = (ViewHolder) rowView.getTag();
		String s = items[position].label;
		holder.label.setText(s);
		holder.icon.setImageResource(items[position].icon);

		return rowView;
	}
}